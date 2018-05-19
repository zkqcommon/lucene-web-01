package com.zkq.javalike.lucene.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 建立索引的类
 * @author Ni Shengwu
 *
 */
public class Indexer {
	
	public static String indexDir = "/Volumes/MyDISK/workspace/java/javalike/lucene/lucene-web-01/index"; //将索引保存到的路径
	public static String dataDir = "/Volumes/MyDISK/workspace/java/javalike/lucene/lucene-web-01/data"; //需要索引的文件数据存放的目录
     

    private IndexWriter writer; //写索引实例

    public Indexer() {
    	try {
			writer = buildWriter(FSDirectory.open(Paths.get(indexDir)));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	private IndexWriter buildWriter(Directory indexDir) throws IOException {
		Analyzer analyzer = new SmartChineseAnalyzer(); // 使用中文分词器
		IndexWriterConfig conf = new IndexWriterConfig(analyzer); // 将标准分词器配到写索引的配置中
		conf.setSimilarity(new ClassicSimilarity());
		IndexWriter writer = new IndexWriter(indexDir, conf); // 实例化写索引对象
		return writer;
	}
    
    // 关闭写索引
    public void close() throws Exception {
    	try {
    		if(writer != null) {
    			if(writer.isOpen()) {
    				writer.close();
    			}
    		}
     	} catch(Exception e) {
     		e.printStackTrace();
     	}
    }

    // 索引指定目录下的所有文件
    public int indexAllFile(String dataDir) throws Exception {
		File dirFile = new File(dataDir);
		File[] files = dirFile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().startsWith(".")) {
					return false;
				}
				return true;
			}
		});
        for(File file : files) {
            indexFile(file); // 调用下面的indexFile方法，对每个文件进行索引
        }
        return writer.numDocs(); //返回索引的文件数
    }

    //索引指定的文件
    private void indexFile(File file) throws Exception {
        System.out.println("索引文件的路径：" + file.getCanonicalPath());
        Document doc = buildDocument(file); //获取该文件的document
        writer.addDocument(doc); //调用下面的getDocument方法，将doc添加到索引中
    }

    //获取文档，文档里再设置每个字段，就类似于数据库中的一行记录
    private Document buildDocument(File file) throws Exception{
        Document doc = new Document();
        //添加字段
        doc.add(new TextField("content", new FileReader(file))); //添加内容
        doc.add(new TextField("fileName", file.getName(), Field.Store.YES)); //添加文件名，并把这个字段存到索引文件里
        doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES)); //添加文件路径
        return doc;
    }
    
    public void doIndex() {
    	int indexedCount = 0;
    	long startTime = System.currentTimeMillis(); //记录索引开始时间
    	try {
    		indexedCount = indexAllFile(dataDir);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	long endTime = System.currentTimeMillis(); //记录索引结束时间
    	System.out.println("索引耗时" + (endTime-startTime) + "毫秒");
    	System.out.println("共索引了" + indexedCount + "个文件");
    	
    }

    public static void main(String[] args) {
    	Indexer indexer = new Indexer();
    	try {
    		indexer.doIndex();
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
     		try {
     			if(indexer != null) {
     				indexer.close();
     			}
     		} catch (Exception e) {
     			e.printStackTrace();
     		}
     	}
    }
}