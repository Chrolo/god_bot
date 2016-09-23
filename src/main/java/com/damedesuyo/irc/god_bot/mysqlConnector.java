package com.damedesuyo.irc.god_bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mysqlConnector{
	
	public Connection connection;
	private String url;
	private String username;
	private String password;

	//--------------------------------------------------------------------------------
	//Constructors:
	public mysqlConnector(String hostname, int port, String database,String username_s, String password_s) throws SQLException
	{
		url = "jdbc:mysql://"+hostname+":"+port+"/"+database;
		username = username_s;
		password = password_s;
		
		attemptConnection();
	}
	public mysqlConnector(String hostname, String database,String username_s, String password_s) throws SQLException
	{	//Default to port 3306
		this(hostname,3306,database,username_s,password_s);
	}
	
	
	public mysqlConnector(String hostname, String username_s, String password_s) throws SQLException
	{
		this(hostname,"",username_s,password_s);
	}
	
	//-------------------------------------------------------------------------------------
	// Public Methods
	public ResultSet queryDatabase(String query)
	{
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet;
		}
		catch (SQLException e)
		{
			System.out.println("[DB query error] "+e.getMessage());
			return null;
		}
	}
	
	public int updateDatabase(String query)
	{
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			int res = preparedStatement.executeUpdate();
			return res;
		}
		catch (SQLException e)
		{
			System.out.println("[DB query error] "+e.getMessage());
			return -1;
		}
	}
	//-----------------
	public static ArrayList<Map<String,Object>> convertResultSetToArrayList(ResultSet resultSet)
	{
		ArrayList<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		if(resultSet == null)
			return results;
		
		
		try{
			resultSet.beforeFirst();	//Rewind results
			
			while(resultSet.next())
			{
				//Get column keys:
				ResultSetMetaData metaData = resultSet.getMetaData();
				Map<String,Object> row = new HashMap<String,Object>(50);
				
				for( int i = 1 ; i <= metaData.getColumnCount(); i ++)
				{
					row.put(metaData.getColumnName(i), resultSet.getObject(i));
				}
				results.add(row);
			}
		}
		catch (SQLException e)
		{
			System.out.println("[ConvertResultToArrayList] SQL Exception caught. Probably got given an emtpy set. Ignoring.\nerror:"+e.getMessage());
		}
		return results;
	}
	
	
	
	

	//-------------------------------------------------------------------------------------
	//Private methods:
	private void attemptConnection() throws SQLException
	{
		System.out.println("[MysqlConnector] Attempting Connection on '"+this.url+"'");
		try
		{ 
			connection = DriverManager.getConnection(url, username, password);
		} 
		catch (SQLException e)
		{
		    throw new SQLException("Cannot connect the database!", e);
		}
	
	}
}
