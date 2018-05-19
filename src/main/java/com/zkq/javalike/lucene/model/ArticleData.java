package com.zkq.javalike.lucene.model;

public class ArticleData {

	private Integer id;
	private Integer docId;
	private String title;
	private String desc;
	private String descLexer;
	private String name;
	private String nameLexer;
	private String explain;
	
	private float score;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public String getDescLexer() {
		return descLexer;
	}
	public void setDescLexer(String descLexer) {
		this.descLexer = descLexer;
	}
	public String getNameLexer() {
		return nameLexer;
	}
	public void setNameLexer(String nameLexer) {
		this.nameLexer = nameLexer;
	}
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
}
