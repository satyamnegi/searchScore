package com.sellics.search.score.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchScoreModel {
	
	private String keyword;
	private Integer score;

}
