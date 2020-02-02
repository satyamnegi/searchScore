package com.sellics.search.score.service.impl;

import com.sellics.search.score.model.SearchScoreModel;
import com.sellics.search.score.service.SearchScoreClientService;
import com.sellics.search.score.service.SearchScoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SearchScoreServiceImpl implements SearchScoreService{

	
	@Autowired
	private SearchScoreClientService searchScoreClientService;
	
	@Autowired
	Environment env;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@Override
	public SearchScoreModel getSearchScore(String keyword) {
		
		keyword = StringUtils.stripToEmpty(keyword);
        if (StringUtils.isEmpty(keyword)) {
            return new SearchScoreModel(keyword, 0);
        }
        List<List<String>> searchScores = getAllSearchScores(keyword);
        if(searchScores.isEmpty()) {
        	return new SearchScoreModel(keyword, 0);
        }
		return calSearchScore(keyword, searchScores);
	}

	/**
	 * This method is responsible for calling Amazon API based on approach of splitting all keywords and hitting API one by one.
	 * @param keyword (input)
	 * @return List of all searches from Amazon API. (output)
	 */
	private List<List<String>> getAllSearchScores(String keyword) {
		
		List<Callable<List<String>>> callsList = getAllCallableList(keyword);
        Function<Future<List<String>>, List<String>> getFutureTask = getFutureTask();
        try {
            //Most Important function to call Amazon API with all callable list in a specified time.
            List<Future<List<String>>> futureList =
                    executorService.invokeAll(callsList, Long.valueOf(env.getRequiredProperty("timeout")), TimeUnit.MILLISECONDS);
            return futureList.stream()
                    .filter(task -> !task.isCancelled())
                    .map(getFutureTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (InterruptedException | CancellationException e) {
        	System.out.println("Exception caught while running executor service " + e);
        }
        //If any exception occurs we return empty list
        return Collections.emptyList();
				
	}
	
	/**
	 * This function creates a callable list which hits Amazon API by splitting input keyword.
	 * @param keyword (Input)
	 * @return List of callables. (Output)
	 */
	private List<Callable<List<String>>> getAllCallableList(String keyword){
		
		 return IntStream.rangeClosed(1, keyword.length())
                .mapToObj(index -> keyword.substring(0, index))
                .map(inputSplit -> (Callable<List<String>>) () -> searchScoreClientService.getAmazonApiResponse(inputSplit))
                .collect(Collectors.toList());
		 
	}
	
	/**
	 * This method is used to get future task.
	 * @return Future task. (Output)
	 */
	private Function<Future<List<String>>, List<String>> getFutureTask() {
		
		return (Future<List<String>> future) -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Exception caught while getting future task " + e);
            }
            return null;
        };
        
	}
	
	/**
     * This method calculates the final score from all the searched response from Amazon API.
     * average of all search calculate by: ((number of suggestion - position in suggestion) / number of suggestion) * 100
     * @param keyword (Input)
     * @param searchScores all the searches from the Amazon API.
     * @return SearchScoreModel after calculating score. (Output)
     */
    private SearchScoreModel calSearchScore(String keyword, List<List<String>> searchScores) {
        
    	AtomicInteger totalScore = new AtomicInteger(0);
        searchScores.forEach(searchScore -> {
            //This finds the first match from the output list of Amazon API.
            OptionalInt matchedIndex = IntStream.range(0, searchScore.size())
                    .filter(index -> searchScore.get(index).startsWith(keyword))
                    .findFirst();

            //Calculates score if we found any such match
            matchedIndex.ifPresent(index -> {
                int score = (((searchScore.size() - index) / searchScore.size()) * 100);
                totalScore.set(totalScore.get() + score);
            });
        });
        return new SearchScoreModel(keyword, totalScore.get() / searchScores.size());
    }

}
