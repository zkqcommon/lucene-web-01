package com.zkq.javalike.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/** 
 */
public class JdbcUtil {
	
	private static DruidDataSource dataSource  = new DruidDataSource();
	
	//声明线程共享变量
    public static ThreadLocal<Connection> container = new ThreadLocal<Connection>();
    
	// 配置说明，参考官方网址
    // http://blog.163.com/hongwei_benbear/blog/static/1183952912013518405588/
    
    static{
        dataSource.setUrl("jdbc:mysql://localhost:3306/jianfa_lucene?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        dataSource.setUsername("root");//用户名
        dataSource.setPassword("root");//密码
        dataSource.setInitialSize(100);
        dataSource.setMaxActive(500);
        dataSource.setMinIdle(0);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(false);
    }
    
	public Connection getJDBCConn() throws Exception{
		Connection conn =null;
		try{
		    conn = dataSource.getConnection();
		    System.out.println(Thread.currentThread().getName()+"连接已经开启......");
		    container.set(conn);
		}catch(Exception e){
		    System.out.println("连接获取失败");
		    e.printStackTrace();
		}
		return conn;
	}
	
	public int saveOrUpdate(String sql,Connection conn) throws Exception{
		Statement stmt = null;
		int count = 0;
		try {
			stmt = conn.createStatement();
			count = stmt.executeUpdate(sql);
		}finally{
			try{
				if(stmt != null){
					stmt.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return count;
	}
	
	/**
	 * 批量执行
	 * @param conn
	 * @param ps
	 * @throws Exception
	 */
	public void batchSaveData(Connection conn,PreparedStatement ps) throws Exception{
		
		try{
			ps.executeBatch();
		}  finally {
			try {
				if(ps != null){
					ps.clearBatch();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 批量执行
	 * @param conn
	 * @param ps
	 * @throws Exception
	 */
	public void batchSaveJSONArray(String table,JSONArray jsonArray) throws Exception{
//		if(jsonArray == null || jsonArray.size() < 1) {
//			return;
//		}
//		
//		for(int j=0;j<jsonArray.size();j++) {
//			JSONObject jsonObj = jsonArray.getJSONObject(j);
//		}
//		
//		Connection conn = this.getJDBCConn();
//		PreparedStatement pstmt = conn.prepareStatement("insert into " + table + "");
//		
//		try{
//			ps.executeBatch();
//		}  finally {
//			try {
//				if(ps != null){
//					ps.clearBatch();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public void closeConn(Connection conn){
		try {
			if(conn != null){
				if(conn.isClosed() == false){
					conn.close();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeStatement(Statement stmt ){
		try {
			if(stmt != null){
				stmt.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeResultSet(ResultSet rs){
	    try{
	        if(rs!=null) rs.close();
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 查询一列数据集合
	 * @param conn
	 * @param sql
	 * @return
	 */
	public List<Object> findOneColumnList(Connection conn,String sql){
		List<Object> resultList = new ArrayList<Object>();
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet set = ps.executeQuery();
			while(set.next()){
				resultList.add(set.getObject(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultList;
	}
	
	public Object getOneValue(Connection conn,String selectSql) {
		Object rtValue = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selectSql);
			if (rs.next()) {
				rtValue = rs.getObject(1);
			}
			return rtValue;
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return rtValue;
	}
	
	public List<Map<String,Object>> queryForList(Connection conn,String sql){
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String,Object>> list = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			list = new ArrayList<Map<String,Object>>();
			Map<Integer,String> columnNames = new HashMap<Integer,String>();
				int columnCount = rs.getMetaData().getColumnCount();
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i = 1;i<=columnCount;i++) {
					String columnName = rsmd.getColumnName(i);
					if(columnName==null || columnName.trim().equals("")){
						columnName = "";
					}
					columnNames.put(i, columnName.toLowerCase());
				}
				while(rs.next()) {
					Map<String,Object> rowMap = new HashMap<String,Object>();
					for(int j = 1;j<=columnCount;j++) {
						rowMap.put(columnNames.get(j), rs.getObject(j));
					}
					list.add(rowMap);
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			closeResultSet(rs);
			closeStatement(stmt);
		}
		return list;
	}
	
	public static List<Map<String,Object>> resultSet2ListMap(ResultSet rs) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<Integer,String> columnNames = new HashMap<Integer,String>();
		try {
			int columnCount = rs.getMetaData().getColumnCount();
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1;i<=columnCount;i++) {
				String columnName = rsmd.getColumnName(i);
				if(columnName==null || columnName.trim().equals("")){
					columnName = "";
				}
				columnNames.put(i, columnName.toLowerCase());
			}
			while(rs.next()) {
				Map<String,Object> rowMap = new HashMap<String,Object>();
				for(int j = 1;j<=columnCount;j++) {
					rowMap.put(columnNames.get(j), rs.getObject(j));
				}
				list.add(rowMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	/**
	 * 获取表的字段名 by zhangkeqi
	 * @param conn
	 * @param tableName
	 * @param bieming
	 * @return
	 */
	public static String getTableColumnNames(Connection conn,String tableName,String bieming) {
		ResultSet rs = null;
		Statement stmt = null;
		StringBuilder sb = new StringBuilder();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select first 1 * from "+tableName + " where 1<>1");
			
			@SuppressWarnings("unused")
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			int columnCount = rs.getMetaData().getColumnCount();
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1;i<=columnCount;i++) {
				if(!"".equals(bieming)) {
					sb.append(bieming+".");
				}
				sb.append(rsmd.getColumnName(i));
				if(i != columnCount) {
					sb.append(",");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
