package com.ssafy.ssafitlife.post.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCondition {
	private String searchType;
	private String keyword;
	private int page = 1;
	private int size = 10;

	public int getOffset() {
		return (page - 1) * size;
	}

	// Getters and Setters
}