package com.zkq.javalike.lucene.searcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.zkq.javalike.jdbc.JdbcUtil;

/**
 * 建立索引的类
 *
 */
public class DBMultiFieldIndexer {
	
	public static String indexDir = "/Volumes/MyDISK/workspace/java/javalike/lucene/lucene-web-01/index5"; //将索引保存到的路径

    private IndexWriter writer; //写索引实例

    public DBMultiFieldIndexer() {
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

    public int indexDBTable() {
    	JdbcUtil jdbcUtil = new JdbcUtil();
    	Connection conn = null;;
    	Statement stmt = null;
    	ResultSet rs = null;
		try {
			conn = jdbcUtil.getJDBCConn();
			stmt = conn.createStatement();
			
			String sql = "select * from test_normal_data";
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Document doc = new Document();
				doc.add(new StoredField("id", rs.getInt(1)));
		        doc.add(new StringField("title",rs.getString(2),Store.YES));
		        doc.add(new TextField("desc",rs.getString(3),Store.YES));
		        doc.add(new TextField("name",rs.getString(4),Store.YES));
		        		
				writer.addDocument(doc);
			}
			
			return writer.numDocs();
		} catch (Exception e) {
			e.printStackTrace();
			
			return -1;
		} finally {
			jdbcUtil.closeResultSet(rs);
			jdbcUtil.closeStatement(stmt);
			jdbcUtil.closeConn(conn);
		}
    }

    public void doIndex() {
    	int indexedCount = 0;
    	long startTime = System.currentTimeMillis(); //记录索引开始时间
    	try {
    		indexedCount = indexDBTable();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	long endTime = System.currentTimeMillis(); //记录索引结束时间
    	System.out.println("索引耗时" + (endTime-startTime) + "毫秒");
    	System.out.println("共索引了" + indexedCount + "条数据");
    }

    public static void main(String[] args) {
    	DBMultiFieldIndexer indexer = new DBMultiFieldIndexer();
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