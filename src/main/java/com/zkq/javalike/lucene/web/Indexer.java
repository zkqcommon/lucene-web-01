package com.zkq.javalike.lucene.web;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    public void addEntity() throws IOException {
        Article article = new Article();
        //article.setId(1);
        //article.setTitle("Lucene全文检索");
        //article.setContent("Lucene是apache软件基金会4 jakarta项目组的一个子项目，是一个开放源代码的全文检索引擎工具包，但它不是一个完整的全文检索引擎，而是一个全文检索引擎的架构，提供了完整的查询引擎和索引引擎，部分文本分析引擎（英文与德文两种西方语言）。");
        article.setId(2);
        article.setTitle("Solr搜索引擎");
        article.setContent("Solr是基于Lucene框架的搜索莹莹程序，是一个开放源代码的全文检索引擎。");

        final Path path = Paths.get(LuceneConstants.IndexDir);
        Directory directory = FSDirectory.open(path);//索引存放目录 存在磁盘
        //Directory RAMDirectory= new RAMDirectory();// 存在内存

//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.APPEND);

        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);//更新或创建索引

        Document document = new Document();
        document.add(new TextField("id", article.getId().toString(), Field.Store.YES));
        document.add(new TextField("title", article.getTitle(), Field.Store.YES));
        document.add(new TextField("content", article.getContent(), Field.Store.YES));

        indexWriter.addDocument(document);
        indexWriter.close();
    }

    public void addFile() throws IOException {
        final Path path = Paths.get(LuceneConstants.IndexDir);

        Directory directory = FSDirectory.open(path);
//        Analyzer analyzer=new StandardAnalyzer();
        Analyzer analyzer=new SmartChineseAnalyzer();

        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter indexWriter=new IndexWriter(directory,indexWriterConfig);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(LuceneConstants.ArticleDir), "UTF-8");//.txt文档,不设置格式会乱码
        BufferedReader bufferedReader=new BufferedReader(isr);

        String content="";
        while ((content=bufferedReader.readLine())!=null){
            Document document=new Document();
            document.add(new TextField("content",content,Field.Store.YES) );
            indexWriter.addDocument(document);
        }
        bufferedReader.close();
        indexWriter.close();
    }

    public List<String> SearchFiles() throws IOException, ParseException {
        String queryString = "Solr";

        final Path path = Paths.get(LuceneConstants.IndexDir);
        Directory directory = FSDirectory.open(path);//索引存储位置
//        Analyzer analyzer = new StandardAnalyzer();//分析器
        Analyzer analyzer = new SmartChineseAnalyzer();//分析器

        //单条件
        //关键词解析
        //QueryParser queryParser=new QueryParser("content",analyzer);
        //Query query=queryParser.parse(queryString);

        //多条件
        Query mQuery = MultiFieldQueryParser.parse(new String[]{"建索"},new String[]{"content"},analyzer);

        IndexReader indexReader = DirectoryReader.open(directory);//索引阅读器
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);//查询

        //TopDocs topDocs=indexSearcher.search(query,3);
        TopDocs topDocs=indexSearcher.search(mQuery,10);
        long count = topDocs.totalHits;
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;


        List<String> list=new ArrayList<String>();
        list.add(String.valueOf(count));

        Integer cnt=0;

        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);

            //list.add(cnt.toString()+"-"+"相关度："+scoreDoc.score+"-----time:"+document.get("time"));
            //list.add("|||");
            //list.add(cnt.toString()+"-"+document.get("content"));

            list.add(document.get("content"));
            cnt++;
        }

        return  list;
    }
}