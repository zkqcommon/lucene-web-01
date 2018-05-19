package com.zkq.javalike.lucene.searcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BoostingQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class DBMultiFieldSearcher {
	
	public IndexSearcher searcher;
	private IndexReader reader;
	
	private Query currentQuery;
	private String queryString = "";
	private Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
	private Highlighter highlighter;
	
	public DBMultiFieldSearcher() {
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(DBMultiFieldIndexer.indexDir)));
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
		this.queryString = q;
		if(topCount <= 0) {
			topCount = 10;
		}
		try {
//			QueryParser parser = new QueryParser("content", analyzer); // 查询解析器
//			Query query = parser.parse(q); // 通过解析要查询的String，获取查询对象
			
			String[] fields = new String[] {"desc","name"};
			Map<String,Float> boosts = new HashMap<String,Float>();
			MultiFieldQueryParser multiParser = new MultiFieldQueryParser(fields,analyzer);
			// 下面两个方式经测试结果是一样的，只是第一种是按照先计算所有分词在每一个域里的得分，然后相加，第二种是先计算每一个分词在各个域的得分然后相加
			// 第一种
//			currentQuery = MultiFieldQueryParser.parse(new String[] {queryString,queryString}, fields, analyzer);
			// 第二种
//			currentQuery = multiParser.parse(queryString);
			// 第三种（设置域的权重）
//			BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
//			QueryParser qp1 = new QueryParser("desc", analyzer);
//			QueryParser qp2 = new QueryParser("name", analyzer);
//			BoostQuery q1 = new BoostQuery(qp1.parse(queryString), 10);
//			Query q2 = qp2.parse(queryString);
//			bQuery.add(q1, BooleanClause.Occur.SHOULD);
//			bQuery.add(q2, BooleanClause.Occur.SHOULD);
//			currentQuery = bQuery.build();
			// 第四种（第三种的简单写法）
//			boosts.put("desc", 10f);
//			// 下面的boost不生效
////			currentQuery = new MultiFieldQueryParser(fields,analyzer,boosts).parse(new String[] {queryString,queryString}, fields, analyzer);
//			// 下面才生效，也是采用先计算每一个分词在各个域的得分然后相加
//			currentQuery = new MultiFieldQueryParser(fields,analyzer,boosts).parse(queryString);
			
			// 第五种 BoostingQuery 让满足另一种查询条件的查询结果评分增加权重。不影响查询结果条数，而影响排序。
			// 注意调用refreshHighlighter()前应该还原currentQuery。否则高亮不显示。
			// 7.0 7.1 7.2 版本都有BUG，官方bug代码为“8182”，官方确认7.3修复。目前有补丁包。可以参考补丁包重写BoostingQuery
			// 6.x及以下是没有问题的。
			// 该查询很重要，可以做关键字或文档的竞价排名。可以设计一个域做竞价条件，从而文档的竞价查询转为域的竞价查询。
			// 从而解决7.x没有Document.setBoost和Field.setBoost的问题。
//			Query positiveQuery = new MultiFieldQueryParser(fields,analyzer).parse(queryString);
			Query positiveQuery = MultiFieldQueryParser.parse(new String[] {queryString,queryString}, fields, analyzer);
			Query negativeQuery = new TermQuery(new Term("name", "误导"));
//			QueryParser parser = new QueryParser("name", analyzer);
//			Query negativeQuery = parser.parse("误导");
			currentQuery = new BoostingQuery(positiveQuery, negativeQuery, 10);
						
			TopDocs topDocs = searcher.search(currentQuery, topCount);
			
//			// 使用第五章查询是要调用下面这句代码
//			currentQuery = positiveQuery;
					
			refreshHighlighter();
	        
//	        // 高亮处理器
//	        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>"); // 如果不指定参数的话，默认是加粗，即<b><b/>
//	        QueryScorer scorer = new QueryScorer(query); // 计算得分，会初始化一个查询结果最高的得分
////	        Fragmenter fragmenter = new NullFragmenter(); //
//	        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); // 根据这个得分计算出一个片段
//	        Highlighter highlighter = new Highlighter(formatter, scorer);
//	        highlighter.setTextFragmenter(fragmenter);
			
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
	
	public String explain(int docid) {
		try {
			return searcher.explain(currentQuery,docid).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	public void search(String indexDir, String q) {
//		this.queryString = q;
//		try {
//			long startTime = System.currentTimeMillis(); // 记录索引开始时间
//			TopDocs topDocs = search(queryString, 10);
//			long endTime = System.currentTimeMillis(); // 记录索引结束时间
//			
//			System.out.println("匹配" + q + "共耗时" + (endTime-startTime) + "毫秒");
//	        System.out.println("查询到" + topDocs.totalHits + "条记录");
//	        
//	        // 高亮处理器
////	        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>"); // 如果不指定参数的话，默认是加粗，即<b><b/>
////	        QueryScorer scorer = new QueryScorer(query); // 计算得分，会初始化一个查询结果最高的得分
//////	        Fragmenter fragmenter = new NullFragmenter(); //
////	        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); // 根据这个得分计算出一个片段
////	        Highlighter highlighter = new Highlighter(formatter, scorer);
////	        highlighter.setTextFragmenter(fragmenter);
//	        
//			for (ScoreDoc scoreDoc : topDocs.scoreDocs) { // 取出每条查询结果
//				Document doc = searcher.doc(scoreDoc.doc); // scoreDoc.doc相当于docID,根据这个docID来获取文档
//				
////				// 查询解释器，解释详细得分原理
//				Explanation explain = searcher.explain(query,scoreDoc.doc);
////				System.out.println(explain);
////				
////				String desc = doc.get("desc");
////				System.out.println("得分" + scoreDoc.score + "," + doc.get("id") + "-" + doc.get("city") + "-"
////						+ desc);
////				
////				// 显示高亮
////				if(desc != null) {
////					TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
////					try {
////						String summary = highlighter.getBestFragment(tokenStream, desc);
////						System.out.println(summary);
////					} catch (InvalidTokenOffsetsException e) {
////						e.printStackTrace();
////					}
////				}
//				
//				System.out.println("id---->" + doc.get("id"));
//				System.out.println("title---->" + doc.get("title")); 
//				System.out.println("desc---->" + highlighterField("desc",doc.get("desc")));
//				System.out.println("name---->" + highlighterField("name",doc.get("name")));
//				System.out.println("===================================");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (Exception e) {
//				}
//			}
//		}
//	}
	
	private void printDocs(TopDocs topDocs) {
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) { // 取出每条查询结果
			Document doc;
			try {
				doc = searcher.doc(scoreDoc.doc);
				
				//		// 查询解释器，解释详细得分原理
				//		Explanation explain = searcher.explain(query,scoreDoc.doc);
				//		System.out.println(explain);
				//		
				//		String desc = doc.get("desc");
				//		System.out.println("得分" + scoreDoc.score + "," + doc.get("id") + "-" + doc.get("city") + "-"
				//				+ desc);
				//		
				//		// 显示高亮
				//		if(desc != null) {
				//			TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
				//			try {
				//				String summary = highlighter.getBestFragment(tokenStream, desc);
				//				System.out.println(summary);
				//			} catch (InvalidTokenOffsetsException e) {
				//				e.printStackTrace();
				//			}
				//		}
				
				System.out.println("id---->" + doc.get("id"));
				System.out.println("title---->" + doc.get("title")); 
				System.out.println("desc---->" + highlighterField("desc",doc.get("desc")));
				System.out.println("name---->" + highlighterField("name",doc.get("name")));
				System.out.println("===================================");
			} catch (IOException e) {
				e.printStackTrace();
			} // scoreDoc.doc相当于docID,根据这个docID来获取文档
		}
	}

	public static void main(String[] args) {
		String query = "合理用药"; // 查询这个字符
		DBMultiFieldSearcher searcher = new DBMultiFieldSearcher();
//		searcher.search(DBMultiFieldIndexer.indexDir, query);
		TopDocs topDocs = searcher.search(query,100);
		searcher.printDocs(topDocs);
	}
}