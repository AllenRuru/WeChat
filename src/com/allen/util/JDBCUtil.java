package com.allen.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtil {

	private static ThreadLocal<Connection> local = new ThreadLocal<Connection>();
	private static Properties p = new Properties();
	private static int connectionType;
	private static int initialSize;
	private static int initialPoolSize;
	private static int maxPoolSize;
	private static int minPoolSize;
	private static int maxStatements;
	private static String driverclass;
	private static String url;
	private static String username;
	private static String password;
	
	private static BasicDataSource bds;
	private static ComboPooledDataSource cds;
	
	static {
		String path = "/db.properties";
		try {
			InputStream fis = JDBCUtil.class.getResourceAsStream(path);
			p.load(fis);
			fis.close();
			//获取连接的类型
			connectionType = Integer.parseInt(p.getProperty("connectionType"));
			//DBCP的配置
			initialSize = Integer.parseInt(p.getProperty("initialSize"));
			//C3P0的配置
			initialPoolSize = Integer.parseInt(p.getProperty("initialPoolSize"));
			maxPoolSize = Integer.parseInt(p.getProperty("maxPoolSize"));
			minPoolSize = Integer.parseInt(p.getProperty("minPoolSize"));
			maxStatements = Integer.parseInt(p.getProperty("maxStatements"));
			
			driverclass = p.getProperty("driverclass");
			url = p.getProperty("url");
			username = p.getProperty("username");
			password = p.getProperty("password");
			
			switch(connectionType){
			case 1:
				Class.forName(driverclass);
				break;
			case 2:
				bds = new BasicDataSource();
				bds.setDriverClassName(driverclass);
				bds.setUrl(url);
				bds.setUsername(username);
				bds.setPassword(password);
				bds.setInitialSize(initialSize);
				break;
			case 3:
				cds = new ComboPooledDataSource();
				cds.setDataSourceName(driverclass);
				cds.setJdbcUrl(url);
				cds.setUser(username);
				cds.setPassword(password);
				cds.setInitialPoolSize(initialPoolSize);
				cds.setMaxPoolSize(maxPoolSize);
				cds.setMinPoolSize(minPoolSize);
				cds.setMaxStatements(maxStatements);
				break;
			default:
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Connection getConnection() {
		Connection con = null;
		try {
			switch(connectionType){
			case 1:
				con = local.get();
				if (con == null) {
					try {
						con = DriverManager.getConnection(url, username, password);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					local.set(con);
				}
				break;
			case 2:
				con = bds.getConnection();
				break;
			case 3:
				con = cds.getConnection();
				break;
			default:
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void close(ResultSet rs, Statement stm, Connection con) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stm != null) {
			try {
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				switch(connectionType){
				case 1:
					local.remove();
					con.close();
					break;
				case 2:
					bds.close();
					break;
				case 3:
					cds.close();
					break;
				default:
					break;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Connection con = getConnection();
		System.out.println(con);
		close(null, null, con);
	}
}
