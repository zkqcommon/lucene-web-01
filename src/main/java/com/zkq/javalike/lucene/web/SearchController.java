package com.zkq.javalike.lucene.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zkq.javalike.lucene.searcher.DBSearcher;
import com.zkq.javalike.lucene.searcher.LuceneUtil;
import com.zkq.javalike.lucene.searcher.Searcher;


/**
 * @author zkq
 *
 */
@Controller
public class SearchController {
	
    @RequestMapping("/search")
    @ResponseBody
    public Object search(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestParam(required=false) String query) {
    	
        Searcher searcher = new Searcher();
        TopDocs topDocs = searcher.search(query, 50);
        
        ResponseModel resp = new ResponseModel();
        resp.setSuccess(true);
        resp.setTotalHits(topDocs.totalHits);
        resp.setMaxScore(topDocs.getMaxScore());
        
        try {
	        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
	        	Document doc;
					doc = searcher.searcher.doc(scoreDoc.doc);
	        	
	        	ResponseModelItem item = new ResponseModelItem();
	        	item.setScore(scoreDoc.score);
	        	item.setFileName(doc.get("fileName"));
	        	item.setFullPath(doc.get("fullPath"));
	        	item.setContent(FileUtils.readFileToString(new File(doc.get("fullPath"))));
	        	resp.results.add(item);
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        	resp.setSuccess(false);
        } finally {
        	searcher.close();
        }
        return resp;
    }
    @RequestMapping("/search2")
    @ResponseBody
    public Object search2(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestParam(required=false) String query) {
    	
    	DBSearcher searcher = new DBSearcher();
    	TopDocs topDocs = searcher.search(query, 50);
    	
    	ResponseModel resp = new ResponseModel();
    	resp.setSuccess(true);
    	resp.setTotalHits(topDocs.totalHits);
    	resp.setMaxScore(topDocs.getMaxScore());
    	
    	try {
    		for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
    			Document doc;
    			doc = searcher.searcher.doc(scoreDoc.doc);
    			
    			ResponseModelItem item = new ResponseModelItem();
    			item.setScore(scoreDoc.score);
    			item.setContent(searcher.highlighterField("content",doc.get("content")));
    			item.setContentLexer("["+LuceneUtil.lexer(doc.get("content"))+"]");
    			item.setExplain("<pre>"+searcher.explain(scoreDoc.doc)+"</pre>");

    			resp.results.add(item);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		resp.setSuccess(false);
    	} finally {
    		searcher.close();
    	}
    	return resp;
    }
    
    @RequestMapping("/search3")
    @ResponseBody
    public Object search3(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestParam(required=false) String query) {
    	
    	DBSearcher searcher = new DBSearcher(1);
    	TopDocs topDocs = searcher.search(query, 50);
    	
    	ResponseModel resp = new ResponseModel();
    	resp.setSuccess(true);
    	resp.setTotalHits(topDocs.totalHits);
    	resp.setMaxScore(topDocs.getMaxScore());
    	
    	try {
    		for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
    			Document doc;
    			doc = searcher.searcher.doc(scoreDoc.doc);
    			
    			ResponseModelItem item = new ResponseModelItem();
    			item.setScore(scoreDoc.score);
    			item.setContent(searcher.highlighterField("content",doc.get("content")));
    			item.setContentLexer("["+LuceneUtil.lexer(doc.get("content"))+"]");
    			item.setExplain("<pre>"+searcher.explain(scoreDoc.doc)+"</pre>");

    			resp.results.add(item);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		resp.setSuccess(false);
    	} finally {
    		searcher.close();
    	}
    	return resp;
    }
    
    public class ResponseModel {
    	private boolean success;
    	private float totalHits;
    	private float MaxScore;
    	private List<ResponseModelItem> results = new ArrayList<ResponseModelItem>();
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
		public List<ResponseModelItem> getResults() {
			return results;
		}
		public void setResults(List<ResponseModelItem> results) {
			this.results = results;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
    }
    public class ResponseModelItem {
    	private float score;
    	private String fileName;
    	private String fullPath;
    	private String content;
    	private String queryLexer;
    	private String contentLexer;
    	private String explain;
    	
		public float getScore() {
			return score;
		}
		public void setScore(float score) {
			this.score = score;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getFullPath() {
			return fullPath;
		}
		public void setFullPath(String fullPath) {
			this.fullPath = fullPath;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getQueryLexer() {
			return queryLexer;
		}
		public void setQueryLexer(String queryLexer) {
			this.queryLexer = queryLexer;
		}
		public String getContentLexer() {
			return contentLexer;
		}
		public void setContentLexer(String contentLexer) {
			this.contentLexer = contentLexer;
		}
		public String getExplain() {
			return explain;
		}
		public void setExplain(String explain) {
			this.explain = explain;
		}
		
    }
}