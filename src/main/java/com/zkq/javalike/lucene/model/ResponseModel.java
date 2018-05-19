package com.zkq.javalike.lucene.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseModel {
	private boolean success;
	private float totalHits;
	private float MaxScore;
	private String queryLexer;
	public List<ArticleData> results = new ArrayList<ArticleData>();
	
	public float getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(float totalHits) {
		this.totalHits = totalHits;
	}
	public float getMaxScore() {
		return MaxScore;
	}
	public void setMaxScore(float maxScore) {
		MaxScore = maxScore;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getQueryLexer() {
		return queryLexer;
	}
	public void setQueryLexer(String queryLexer) {
		this.queryLexer = queryLexer;
	}
	public List<ArticleData> getResults() {
		return results;
	}
	public void setResults(List<ArticleData> results) {
		this.results = results;
	}
}
