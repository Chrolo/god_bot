package com.damedesuyo.irc.god_bot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class StaffMember {

	// Database Tag:
	private int databaseID;

	// IRC identifiers:
	private String username;


	private String hostname;

	// Fansubbing description:
	private ArrayList<String> jobTitles;

	// Privileges:
	private ArrayList<Privilege> privileges;
	
	//Physicalities:
	private String timeZoneStr="";	//Timezone in 'tz' format
	
	//-------------------------------------------------------------
	//Constructors
	
	public StaffMember(Map<String,Object> userData)
	{
		//Constructor using data from Database:
		this.username = (String) userData.get("userName");
		this.databaseID = (int) userData.get("id");

		if (userData.get("timezone") != null)
			this.timeZoneStr = (String) userData.get("timezone");

		// set privileges:
		this.privileges = new ArrayList<Privilege>();
		if(userData.get("priv_addStaff") != null && userData.get("priv_addStaff").equals("Y"))
		{
			this.privileges.add(Privilege.ADD_STAFF);
		}
		
		if(userData.get("priv_removeStaff") != null && userData.get("priv_removeStaff").equals("Y"))
		{
			this.privileges.add(Privilege.REMOVE_STAFF);
		}
		
		if(userData.get("priv_shutdownBot") != null && userData.get("priv_shutdownBot").equals("Y"))
		{
			this.privileges.add(Privilege.SHUTDOWN_BOT);
		}
	}

	// ------------------------------------------------------------------

	// TODO [DEBUG] remove this debug function
	public void printStaffDataToConsole() {
		String printOut = "";
		printOut = printOut + "databaseID: " + this.databaseID + ", ";

		// IRC identifiers:
		printOut = printOut + "username: " + this.username + ", ";
		printOut = printOut + "hostname: " + this.hostname + ", ";

		// Fansubbing description:
		printOut = printOut + "priveledges: " + this.privileges + ", ";
		// TODO [Enhancement] print jobTitles

		// Physicalities:
		printOut = printOut + "timeZoneStr: " + this.timeZoneStr + ", ";

		System.out.println("Userdata Printout:{" + printOut + "}");
	}

	// -----------------------

	public String getStaffLocalTimeAsStringWithFormat(String dateFormatString)// throws
																				// Exception
	{
		if (this.timeZoneStr == null) {
			// throw new Exception("User Has no Timezone set."); //lets not
			// throw exceptions around willy-nilly
			return null;
		} else if (this.timeZoneStr.isEmpty()) {
			// throw new Exception("User Has no Timezone set.");
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
			sdf.setTimeZone(TimeZone.getTimeZone(this.timeZoneStr));
			return sdf.format(new Date());
		}
	}

	public void setTimezone(String timezone) throws Exception {
		if (!StaffMember.validateTimezone(timezone))
			throw new Exception("Invalid Timezone");
		UserDatabase.getSharedInstance().updateUsersTimezone(this.databaseID, timezone);
	}

	public boolean hasPrivilege(Privilege required) {
		return this.privileges.contains(required);
	}

	// ----------------------------------------------------------------------------
	// Getters for private variables:
	public int databaseID() {
		return databaseID;
	}

	public String username() {
		return username;
	}
	
	public String getNonHLUsername() {
		return StaffMember.deHighlightUsername(username);
	}
	
	// ----------------------------------------------------------------------------
	// Public Statics:
	public static Boolean validateTimezone(String timeZoneID) {
		for (String acceptableID : TimeZone.getAvailableIDs()) {
			if (timeZoneID.equals(acceptableID))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param userName
	 * @return A string that won't highlight that user.
	 */
	public static String deHighlightUsername(String userName) {
		// inserts a zero-width space character after the first character of the
		// name
		return userName.substring(0, 1) + "​" + userName.substring(1);
	}

}
