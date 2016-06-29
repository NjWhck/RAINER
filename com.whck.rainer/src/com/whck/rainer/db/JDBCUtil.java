package com.whck.rainer.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private static String driver;
	private static String url;
	private static String user;
	private static String password;

	static {
		Properties pro = new Properties();
		try {
			pro.load(JDBCUtil.class.getResourceAsStream("/db.properties"));
		} catch (IOException e) {
			System.out.println("读取db.properties文件失败");
			e.printStackTrace();
		}
		driver = pro.getProperty("driver");
		url = pro.getProperty("url");
		user = pro.getProperty("username");
		password = pro.getProperty("password");

	}

	public Connection getConnection() {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			System.out.println("加载数据库驱动失败");
			e.printStackTrace();
		}
		try {
			System.out.println("url:"+url+" ,user:"+user+",password:"+password);
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("连接数据库失败");
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConn() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
    public int execUpdate(PreparedStatement pstmt){
        try {
            int affectedRows = pstmt.executeUpdate();
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public ResultSet execQuery(PreparedStatement pstmt){
        try {
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
