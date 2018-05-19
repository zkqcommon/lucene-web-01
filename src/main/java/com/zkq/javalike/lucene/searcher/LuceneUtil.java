package com.zkq.javalike.lucene.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LuceneUtil {

	public static String lexer(String text) {
		SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer();
		return lexer(text, smartChineseAnalyzer);
	}
	
	public static String lexer(String text, Analyzer analyzer) {
		StringBuilder sb = new StringBuilder();
		try {
			TokenStream tokenStream = analyzer.tokenStream("field", text);
			CharTermAttribute attribute = tokenStream
					.addAttribute(CharTermAttribute.class);
				tokenStream.reset();
			while (tokenStream.incrementToken()) {
				sb.append(attribute.toString());
				if(tokenStream.hasAttributes()) {
					sb.append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return sb.toString();
	}
	
	public static String documentMatchWord(Analyzer analyzer, String fieldName,String text) {
		StringBuilder sb = new StringBuilder();
		try {
			TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
			CharTermAttribute attribute = tokenStream
					.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				sb.append(attribute.toString());
				if(tokenStream.hasAttributes()) {
					sb.append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return sb.toString();
	}
}
