package com.damedesuyo.irc.god_bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.damedesuyo.irc.god_bot.database_def.DatabaseDefinition;
import com.damedesuyo.irc.god_bot.database_def.DatabaseTableDefinition;
import com.google.gson.Gson;

/**
 *	This class provides functionality to retrieve and edit information about
 *	users present on the database 
 * 
 * 
 *
 */

public class UserDatabase 
{
	//class variables:
	private mysqlConnector read_connection;
	private mysqlConnector write_connection;
	
	private DatabaseTableDefinition StaffTableStructure;
	private DatabaseTableDefinition StaffAliasTableStructure;
	
	//--------------------------------------------------------------------------------
	//Get Shared instance:
	private static class SingletonHelper
	{
		private static final UserDatabase INSTANCE = new UserDatabase();

		
	}

	/**
	 * Use this method to acquire the shared instance of Userdatabase
	 * @return the shared instance of UserDatabase
	 */
	public static UserDatabase getSharedInstance()
	{
		return SingletonHelper.INSTANCE;
	}
	
	//--------------------------------------------------------------------------------
	//Constructors:
	private UserDatabase()
	{
		//Get mysql connection:
		try 
		{
			Wini ini = new Wini(new File("config/settings.ini"));
			
			//Setup Read connection:
			String databaseHost =	ini.get("database","hostname",String.class);
			String databaseName =	ini.get("database","database",String.class);
			String mysqlUser =		ini.get("database","user",String.class);
			String mysqlPassword =	ini.get("database","password",String.class);
			
			read_connection = new mysqlConnector(databaseHost,databaseName,mysqlUser,mysqlPassword);
			
			//Setup write connection
			String write_mysqlUser =		ini.get("database","updateUser",String.class);
			String write_mysqlPassword =	ini.get("database","updatePassword",String.class);
			
			write_connection = new mysqlConnector(databaseHost,databaseName,write_mysqlUser,write_mysqlPassword);
			
		} 
		catch (InvalidFileFormatException e) 
		{
			System.out.println("[UserDatabase] settings.ini was not a valid ini file.");
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			System.out.println("[UserDatabase] config/settings.ini could not be found.");
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			System.out.println("[UserDatabase] SQL exception encountered when trying to setup connections.");
			e.printStackTrace();
		}
		
		//Get the table definitions:
		Gson gson = new Gson();
		DatabaseDefinition dbDef = new DatabaseDefinition();
		
		try
		{
			Reader dbDefFile = new FileReader("config/databaseDefinitions.json");
			dbDef = gson.fromJson(dbDefFile, dbDef.getClass());
			this.StaffTableStructure = dbDef.tables.get("staff");
			this.StaffAliasTableStructure = dbDef.tables.get("staffAliases");
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("[UserDatabase] Couldn't read \"config/databaseDefinitions.json\". File is required for Database Definitions.");
		}

	}
	
	
	//--------------------------------------------------------------------------------
	//Public
	
	//	--Username validity
	/**
	 * @return ArrayList&lt;String&gt; containing each valid username/alias currently listed on database 
	 */
	public ArrayList<String> getValidUsers()
	{
		final PreparedStatement getAliasesStatement;
		ArrayList<Map<String,Object>> result = null;
		try 
		{
			getAliasesStatement = read_connection.connection.prepareStatement("SELECT alias FROM staffAliases;");
			result = mysqlConnector.convertResultSetToArrayList(getAliasesStatement.executeQuery());
		}
		catch (SQLException e) 
		{
			System.out.println("[UserDatabase.java:getValidUsers]"+e.getMessage());
		}
		
		//System.out.println(result);
		
		if(result == null)
			return null;
		
		ArrayList<String> names = new ArrayList<String>();
		
		for(Map<String,Object> row : result)
		{
			names.add((String) row.get("alias"));
		}
		
		return names;
	}
	
	public boolean isExistingUser(String user)
	{
		return this.getValidUsers().contains(user);
	}
	
	//-----------------------------------------------------------
	//Add new user to database:
	// This will be a series of different constructors: you could send just a user name, or a whole user's detailss
	public boolean addStaffToDatabase(Map<String, Object> userData) throws SQLException
	{
		//TODO: validate the userData before adding to DB.
		//TODO: validate that "userName" exists and is a string.
		
		//Insert onto staff table
		this.insertUserInfo(userData);
		//Also add the initial alias to the database:
		return this.cloneUsernameToAliasTable((String)userData.get("userName"));	
	}
	
	public boolean addStaffToDatabase(String userName) throws SQLException
	{
		Map<String, Object> userData = new HashMap<String,Object>(5);
		userData.put("userName", userName);
		
		return this.addStaffToDatabase(userData);
	}
	
	public boolean addStaffToDatabase(String userName, String timezone) throws SQLException
	{
		//TODO: validate the username before adding to DB.
		Map<String, Object> userData = new HashMap<String,Object>(5);
		userData.put("userName", userName);
		userData.put("timezone", timezone);
		
		return this.addStaffToDatabase(userData);
	}
	
	
	//----------------------------------------------------------
	
	public StaffMember getStaffMember(String alias)
	{
		final PreparedStatement getStaffStatement;
		ArrayList<Map<String,Object>> result = null;
		try 
		{
			getStaffStatement = read_connection.connection.prepareStatement("SELECT * FROM staff INNER JOIN staffAliases on staffAliases.userID = staff.id WHERE staffAliases.alias = ? ;");
			getStaffStatement.setString(1, alias);
			result = mysqlConnector.convertResultSetToArrayList(getStaffStatement.executeQuery());
		}
		catch (SQLException e) 
		{
			System.out.println("[UserDatabase.java:getStaffMember]"+e.getMessage());
		}
		
		//If error, return null now.
		if(result == null || result.size() == 0 )
		{
			System.err.println("[getStaffMember] Found no results for '"+alias+"'");
			return null;
		}
		else if(result.size()>1)
		{	//should this be an error?
			System.err.println("[getStaffMember] Saw "+result.size()+ " possible people matching "+alias+". But will return 1st.");
			return new StaffMember(result.get(0));
		}
		else
			return new StaffMember(result.get(0));
		
	}
	
	//Add new username to database:
	public boolean cloneUsernameToAliasTable(String userName)
	{
		try 
		{
			PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("INSERT INTO staffAliases (alias, userID) (SELECT userName, id FROM staff WHERE userName= ? );");
			preparedStatement.setString(1, userName);
			preparedStatement.executeUpdate();
			int updates = preparedStatement.getUpdateCount();
			if(updates == 1)
			{
				return true;
			}
			else
			{
				System.err.println("[UserDatabase.java:cloneUsernameToAliasTable] Expected updateCount ==1 , got "+ updates);
				return false;
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean addNewAlias(String oldAlias, String newAlias)
	{
		//TODO: Update to use userID tag
		
		//Check that the new name isn't already listed:
		if( (isExistingUser(newAlias)) || (!isExistingUser(oldAlias)) )
		{
			System.out.println("[UserDataBase.java] Alias will not be added as either '"+newAlias+"' was already listed, or '"+oldAlias+"' was not found");
			return false;
		}
		
		//Prepare Sql statement:
		try {
			PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("INSERT INTO staffAliases (alias, userID) SELECT ?, userID from staffAliases WHERE alias=?;");
			preparedStatement.setString(1, newAlias);
			preparedStatement.setString(2, oldAlias);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("[UserDatabase.java:addNewAlias]." + e.getMessage());
			return false;
		}
		return true;
		
	}
	
	public boolean updateUsersTimezone(String user, String timezone)
	{
		//check user exists:
		if(!isExistingUser(user))
		{
			System.out.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		
		return updateUserInfo(user, "timezone", timezone);
	}
	
	public boolean updateUsersTimezone(int userID, String timezone)
	{	
		return updateUserInfo(userID, "timezone", timezone);
	}
	
	public boolean updateUsersMisc(String user, String newMisc)
	{
		//check user exists:
		if(!isExistingUser(user))
		{
			System.out.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		
		return updateUserInfo(user, "misc", newMisc);
		
		
	}
	
	/**
	 * Inserts a new user into the database
	 * @param userInfo
	 * @return
	 * @throws SQLException 
	 */
	public boolean insertUserInfo(Map<String,Object> userInfo) throws SQLException
	{
		String tablekeys= "";
		String params = "";
		ArrayList<Object> values = new ArrayList<Object>();
		//figure out the fields to be added:

		for (Map.Entry<String,Object> entry : userInfo.entrySet()) {
			//check the key exists:
			String key = entry.getKey();
			if(this.StaffTableStructure.columns.containsKey(key))
		    {
				values.add(entry.getValue());
			    tablekeys = tablekeys +", "+ key;
			    params = params + ", ?";
		    }
			else
			{
				System.err.println("[UserDatabase]InsertUserInfo: '"+key+"' is not a valid field.");
			}
		}
		//*/
		//trim excess ', '.
		tablekeys = tablekeys.substring(2);
		params = params.substring(2);
		
		PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("INSERT INTO staff ("+tablekeys+") VALUES ("+params+");");
		for(int i=0; i < values.size();i++)
		{
			Object value = values.get(i);
			if( value instanceof Integer)
			{
				preparedStatement.setInt(i+1, (int)value);
			}
			else
			{
				preparedStatement.setString(i+1, (String)value);
			}

		}
		preparedStatement.executeUpdate();
		int updates = preparedStatement.getUpdateCount();
		if(updates == 1)
		{
			return true;
		}
		else
		{
			System.err.println("[UserDatabase.java:insertUserInfo] Expected updateCount ==1 , got "+ updates);
			return false;
		}
	}
	
	//-----------------------------------------------------------------------------
	//Private methods:
	private boolean updateUserInfo(String user, String key, String value)
	{
		//TODO Change this to use UserID
		
		//check user exists:
		if(!isExistingUser(user))
		{
			System.err.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		
		//Update the user:
		try {
			
			PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("UPDATE staff INNER JOIN staffAliases ON staffAliases.userID=staff.id SET staff."+key+"= ? WHERE staffAliases.alias=?;");
			preparedStatement.setString(1, value);
			preparedStatement.setString(2, user);
			preparedStatement.executeUpdate();
			int updates = preparedStatement.getUpdateCount();
			if(updates != 1)
			{
				System.err.println("[UserDatabase.java:updateUserInfo] Expected updateCount ==1 , got "+ updates);
				return false;
			}
		} catch (SQLException e) {
			System.err.println("[UserDatabase.java:updateUserInfo] SQL Error: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean updateUserInfo(int userID, String key, String value)
	{	
		//check user exists:
		/* Don't know how to implement this for ID
		if(!isExistingUser(user))
		{
			System.out.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		//*/
		
		//Update the user:
		try {
			PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("UPDATE staff SET "+key+"= ? WHERE id=?;");
			preparedStatement.setString(1, value);
			preparedStatement.setInt(2, userID);
			preparedStatement.executeUpdate();
			int updates = preparedStatement.getUpdateCount();
			if(updates != 1)
			{
				System.err.println("[UserDatabase.java:updateUserInfo] Expected updateCount ==1 , got "+ updates);
				return false;
			}
		} catch (SQLException e) {
			System.err.println("[UserDatabase.java:updateUserInfo] SQL Error: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	
}
