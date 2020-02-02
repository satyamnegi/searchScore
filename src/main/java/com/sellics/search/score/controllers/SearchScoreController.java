package com.sellics.search.score.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sellics.search.score.model.SearchScoreModel;
import com.sellics.search.score.service.SearchScoreService;

@RestController
public class SearchScoreController {

	@Autowired
	private SearchScoreService searchScoreService;
	
	/**
	 * This is a main controller method to get the search score
	 * @param keyword (input)
	 * @return search score model in JSON format (output)
	 */
	@RequestMapping(value = "/estimate", method = RequestMethod.GET, produces = {"application/JSON"})
	public SearchScoreModel estimateSearchVolume(@RequestParam("keyword") String keyword) {
		
		SearchScoreModel score = searchScoreService.getSearchScore(keyword);
        return score;
        
    }
	
}
