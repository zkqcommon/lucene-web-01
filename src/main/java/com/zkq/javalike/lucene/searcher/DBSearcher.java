package com.zkq.javalike.lucene.searcher;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class DBSearcher {
	
	public IndexSearcher searcher;
	private IndexReader reader;

	private Query currentQuery;
	private Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
	private Highlighter highlighter;
	
	public DBSearcher() {
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(DBIndexer.indexDir)));
			// 通过获取要查询的路径，也就是索引所在的位置，创建IndexReader
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new ClassicSimilarity());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	public DBSearcher(int type) {
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(DBIndexer.indexDir2)));
			// 通过获取要查询的路径，也就是索引所在的位置，创建IndexReader
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new ClassicSimilarity());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void refreshHighlighter() {
        // 高亮处理器
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color=red>[","]</font></b>"); // 如果不指定参数的话，默认是加粗，即<b><b/>
        QueryScorer scorer = new QueryScorer(currentQuery); // 计算得分，会初始化一个查询结果最高的得分
//      Fragmenter fragmenter = new NullFragmenter(); //
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); // 根据这个得分计算出一个片段
        highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
	}
	
	public TopDocs search(String q,int topCount) {	
		if(topCount <= 0) {
			topCount = 10;
		}
		try {
//			Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
			QueryParser parser = new QueryParser("content", analyzer); // 查询解析器
			currentQuery = parser.parse(q); // 通过解析要查询的String，获取查询对象

			TopDocs topDocs = searcher.search(currentQuery, topCount);
			
			refreshHighlighter();
			
			return topDocs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}
	

	
	public String highlighterField(String field,String text) {
		if(highlighter == null) {
			return "highlighter=null";
		}
		try {
			// 显示高亮
	        String summary = "error";
			if(text != null) {
				summary  = highlighter.getBestFragment(analyzer,field,text);
				if(summary == null) {
					summary = text;
				}
			}
			
			return summary;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public void search(String indexDir, String q) {

		IndexReader reader = null;
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(indexDir))); // 通过获取要查询的路径，也就是索引所在的位置，创建IndexReader
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new ClassicSimilarity());

//			Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
			QueryParser parser = new QueryParser("content", analyzer); // 查询解析器
			currentQuery = parser.parse(q); // 通过解析要查询的String，获取查询对象

			long startTime = System.currentTimeMillis(); // 记录索引开始时间
			TopDocs topDocs = searcher.search(currentQuery, 10);
			long endTime = System.currentTimeMillis(); // 记录索引结束时间
			
			System.out.println("匹配" + q + "共耗时" + (endTime-startTime) + "毫秒");
	        System.out.println("查询到" + topDocs.totalHits + "条记录");
	        
	        refreshHighlighter();
	        
	        // 高亮处理器
//	        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>"); // 如果不指定参数的话，默认是加粗，即<b><b/>
//	        QueryScorer scorer = new QueryScorer(query); // 计算得分，会初始化一个查询结果最高的得分
////	        Fragmenter fragmenter = new NullFragmenter(); //
//	        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); // 根据这个得分计算出一个片段
//	        Highlighter highlighter = new Highlighter(formatter, scorer);
//	        highlighter.setTextFragmenter(fragmenter);
	        
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) { // 取出每条查询结果
				Document doc = searcher.doc(scoreDoc.doc); // scoreDoc.doc相当于docID,根据这个docID来获取文档
				
//				// 查询解释器，解释详细得分原理
//				Explanation explain = searcher.explain(query,scoreDoc.doc);
//				System.out.println(explain);
//				
//				String desc = doc.get("desc");
//				System.out.println("得分" + scoreDoc.score + "," + doc.get("id") + "-" + doc.get("city") + "-"
//						+ desc);
//				
//				// 显示高亮
//				if(desc != null) {
//					TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
//					try {
//						String summary = highlighter.getBestFragment(tokenStream, desc);
//						System.out.println(summary);
//					} catch (InvalidTokenOffsetsException e) {
//						e.printStackTrace();
//					}
//				}
				
				System.out.println(doc.get("content")); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public String explain(int docid) {
		try {
			return searcher.explain(currentQuery,docid).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		String query = "医药代表贿赂法案"; // 查询这个字符
		
		new DBSearcher().search(DBIndexer.indexDir, query);
	}
}