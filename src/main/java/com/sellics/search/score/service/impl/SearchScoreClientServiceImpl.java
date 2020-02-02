package com.sellics.search.score.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sellics.search.score.service.SearchScoreClientService;

@Service
public class SearchScoreClientServiceImpl implements SearchScoreClientService {

	@Autowired
	private Environment env;

	@Override
	public List<String> getAmazonApiResponse(String keyWord) {

		//Forming Amazon search url and hitting it using RestTemplate.
		String urlAmazon = (env.getRequiredProperty("rest.amazon.url")) + keyWord;
		RestTemplate rest = new RestTemplate();
		String response = rest.getForEntity(urlAmazon, String.class).getBody();
		return convertStringResponseToList(response);

	}

	/**
	 * This function converts string response from Amazon API into List of string.
	 * @param response from Amazon API. (Input)
	 * @return List of strings. (Output)
	 */
	private List<String> convertStringResponseToList(String response) {
		
		//Filtering string as per our need to convert into List.
		String res = response.replaceAll("\\[.*?\\\",\\[", "")
				.replaceAll("\\],\\[.*?\\\"\\]", "")
				.replaceAll("\"", "")
				.trim();
				
		return Stream.of(res.split(",")).map(String::new).collect(Collectors.toList());
	}

}
