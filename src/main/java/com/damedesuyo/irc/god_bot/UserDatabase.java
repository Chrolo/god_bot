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
	// User Queries
	
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
			System.err.println("[UserDatabase.java:getValidUsers]"+e.getMessage());
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
	//----------------------------------------------------------//
	//			Retrieve Data
	//----------------------------------------------------------//
	
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
			System.err.println("[UserDatabase.java:getStaffMember]"+e.getMessage());
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
		
	//-----------------------------------------------------------
	//Add new user to database:
	/**
	 * 
	 * @param userData Data about the user. Must include userName.
	 * @return error code:	0	: success
	 * 						-1	: Missing Critical info
	 * 						-2	: User Already existed
	 * 						-3	: SQL exception occurred
	 * 						-4	: SQL insert into the tables failed.
	 * 						-5	: userName was not a string
	 * @throws SQLException
	 */
	public int addStaffToDatabase(Map<String, Object> userData) throws SQLException
	{
		//Make sure the critical parts have been set:
		if(!userData.containsKey("userName"))
		{
			return -1;
		}
		if(!userData.get("userName").getClass().equals(String.class))
		{
			System.out.println("[UserDatabase.java:addNewUser] User '"+userData.get("userName")+"' was type:"+userData.get("userName").getClass().toString()+", not a String.");
			return -5;
		}
		
		//Check user doesn't already exist:
		if(isExistingUser((String)userData.get("userName")))
		{
			System.out.println("[UserDatabase.java:addNewUser] User '"+userData.get("userName")+"' already existed.");
			return -2;
		}
		
		try
		{
			 if(p_insertUserInfo(userData)&&cloneUsernameToAliasTable((String)userData.get("userName")))
			 {
				 return 0;
			 }
			 
		} 
		catch (SQLException e) 
		{
			System.err.println("[UserDatabase.java:addNewUser] SQL Exception: "+e.getMessage());
			return -3;
		}
		
		return -4;
		
	}
	
	public int addStaffToDatabase(String userName) throws SQLException
	{
		Map<String, Object> userData = new HashMap<String,Object>(5);
		userData.put("userName", userName);
		
		return this.addStaffToDatabase(userData);
	}
	
	public int addStaffToDatabase(String userName, String timezone) throws SQLException
	{
		//TODO: validate the username before adding to DB.
		Map<String, Object> userData = new HashMap<String,Object>(5);
		userData.put("userName", userName);
		userData.put("timezone", timezone);
		
		return this.addStaffToDatabase(userData);
	}
	
	
	
	
	//Add new username to database:
	private boolean cloneUsernameToAliasTable(String userName)
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
			System.err.println("[UserDatabase.java:addNewAlias]." + e.getMessage());
			return false;
		}
		return true;
		
	}
	
	//---------------------------------------------------------------------------
	//updateUsersTimezone
	
	public boolean updateUsersTimezone(String user, String timezone)
	{
		//check user exists:
		if(!isExistingUser(user))
		{
			System.out.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		
		return p_updateUserInfo(user, "timezone", timezone);
	}
	
	public boolean updateUsersTimezone(int userID, String timezone)
	{	
		return p_updateUserInfo(userID, "timezone", timezone);
	}
	
	//------------------------------------------------------------------------------
	// updateUsersMisc
	public boolean updateUsersMisc(String user, String newMisc)
	{
		//check user exists:
		if(!isExistingUser(user))
		{
			System.out.println("[UserDataBase.java] Cannot update '"+user+"' as they do not exist.");
			return false;
		}
		
		return p_updateUserInfo(user, "misc", newMisc);
		
		
	}
	
	//--------------------------------------------------------------
	//Delete a user : Public Methods
	public boolean deleteUser(String user)
	{
		//Check user exists:
		if(!isExistingUser(user))
		{
			System.err.println("[UserDataBase.java] Cannot delete '"+user+"' as they do not exist.");
			return false;
		}
		//get userID:
		StaffMember user_c = getStaffMember(user);
		return p_deleteUser(user_c.databaseID);
	}
	//-------------------------------------------------------------------------
	// Insert Data : Private Method
	
	/**
	 * Inserts a new user into the database
	 * @param userInfo
	 * @return
	 * @throws SQLException 
	 */
	private boolean p_insertUserInfo(Map<String,Object> userInfo) throws SQLException
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
	//Update UserInfo: Private methods
	private boolean p_updateUserInfo(String user, String key, String value)
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
	
	private boolean p_updateUserInfo(int userID, String key, String value)
	{	
		//check user exists?
		
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
	
	//--------------------------------------------------------------
	//Delete a user : Private method
	private boolean p_deleteUser(int userID)
	{
		try {
			//Delete from the staff Listing
			PreparedStatement preparedStatement = this.write_connection.connection.prepareStatement("DELETE FROM staff WHERE id=?;");
			preparedStatement.setInt(1, userID);
			preparedStatement.executeUpdate();
			int updates1 = preparedStatement.getUpdateCount();
			if(updates1<1)
			{
				System.err.println("[UserDatabase.java:deleteUser] Tried to delete user entry for "+userID+". Expected updateCount >0 , got "+ updates1);
			}
			//And now any associated Alias listings:
			preparedStatement = this.write_connection.connection.prepareStatement("DELETE FROM staffAliases WHERE userID=?;");
			preparedStatement.setInt(1, userID);
			preparedStatement.executeUpdate();
			int updates2 = preparedStatement.getUpdateCount();
			if(updates2<1)
			{
				System.err.println("[UserDatabase.java:deleteUser] Tried to delete aliases for "+userID+". Expected updateCount >0 , got "+ updates2);	
			}
			if(updates1<1||updates2<1)
			{
				return false;
			}
			
		} catch (SQLException e) {
			System.err.println("[UserDatabase.java:updateUserInfo] SQL Error: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	
}
