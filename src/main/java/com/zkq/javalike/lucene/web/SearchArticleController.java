package com.zkq.javalike.lucene.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zkq.javalike.lucene.model.ArticleData;
import com.zkq.javalike.lucene.model.ResponseModel;
import com.zkq.javalike.lucene.searcher.DBMultiFieldSearcher;
import com.zkq.javalike.lucene.searcher.LuceneUtil;


/**
 * @author zkq
 *
 */
@Controller
public class SearchArticleController {
	
    @RequestMapping("/searchArticle")
    @ResponseBody
    public Object searchArticle(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestParam(required=false) String query) {
    	
    	DBMultiFieldSearcher searcher = new DBMultiFieldSearcher();
    	TopDocs topDocs = searcher.search(query, 50);
    	
    	ResponseModel resp = new ResponseModel();
    	resp.setSuccess(true);
    	resp.setTotalHits(topDocs.totalHits);
    	resp.setMaxScore(topDocs.getMaxScore());
    	resp.setQueryLexer(LuceneUtil.lexer(query));
    	
    	try {
    		for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
    			Document doc;
    			doc = searcher.searcher.doc(scoreDoc.doc);
    			
    			ArticleData item = new ArticleData();
    			item.setScore(scoreDoc.score);
    			item.setId(doc.getField("id").numericValue().intValue());
    			item.setDocId(scoreDoc.doc);
    			item.setTitle(doc.get("title"));
    			item.setDesc(searcher.highlighterField("desc",doc.get("desc")));
    			item.setDescLexer("["+LuceneUtil.lexer(doc.get("desc"))+"]");
    			item.setName(searcher.highlighterField("name",doc.get("name")));
    			item.setNameLexer("["+LuceneUtil.lexer(doc.get("name"))+"]");
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
}