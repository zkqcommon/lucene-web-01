package com.zkq.javalike.lucene.test;

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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	
	public IndexSearcher searcher;
	private IndexReader reader;
	
	public Searcher() {
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(Indexer.indexDir)));
			// 通过获取要查询的路径，也就是索引所在的位置，创建IndexReader
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new ClassicSimilarity());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public TopDocs search(String q,int topCount) {	
		if(topCount <= 0) {
			topCount = 10;
		}
		try {
			Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
			QueryParser parser = new QueryParser("content", analyzer); // 查询解析器
			Query query = parser.parse(q); // 通过解析要查询的String，获取查询对象

			TopDocs topDocs = searcher.search(query, topCount);
	        
			return topDocs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}
	}
	public static void search(String indexDir, String q) {

		IndexReader reader = null;
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(indexDir))); // 通过获取要查询的路径，也就是索引所在的位置，创建IndexReader
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new ClassicSimilarity());

			Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
			QueryParser parser = new QueryParser("content", analyzer); // 查询解析器
			Query query = parser.parse(q); // 通过解析要查询的String，获取查询对象

			long startTime = System.currentTimeMillis(); // 记录索引开始时间
			TopDocs topDocs = searcher.search(query, 10);
			long endTime = System.currentTimeMillis(); // 记录索引结束时间
			
			System.out.println("匹配" + q + "共耗时" + (endTime-startTime) + "毫秒");
	        System.out.println("查询到" + topDocs.totalHits + "条记录");
	        
	        
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
				
				System.out.println(doc.get("fullPath")); // fullPath是刚刚建立索引的时候我们定义的一个字段
				System.out.println("\t"+doc.get("fileName")); // fullPath是刚刚建立索引的时候我们定义的一个字段
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

	public static void main(String[] args) {
		String query = "相关法律法规有哪些"; // 查询这个字符
		
		search(Indexer.indexDir, query);
	}
}