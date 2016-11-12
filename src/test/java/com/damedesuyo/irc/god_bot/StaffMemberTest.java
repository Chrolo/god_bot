/**
 * 
 */
package com.damedesuyo.irc.god_bot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import com.google.gson.Gson;

/**
 * @author EpicRael
 *
 */
public class StaffMemberTest {

	StaffMember user_noPrivs;
	Map<String,Object> user_noPrivs_config;
	StaffMember user_allPrivs;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//Create users:
		Map<String,Object> userConfig = new HashMap<String,Object>();
		userConfig.put("userName", "Bob");
		userConfig.put("id", 1);
		userConfig.put("timezone", "Europe/London");
		userConfig.put("priv_addStaff","N");
		userConfig.put("priv_removeStaff","N");
		
		user_noPrivs_config = userConfig;
		
		user_noPrivs = new StaffMember(user_noPrivs_config);
		
		
	}

	/**
	 * Test method for {@link com.damedesuyo.irc.god_bot.StaffMember#getStaffLocalTimeAsStringWithFormat(java.lang.String)}.
	 */
	@Test
	public void testGetStaffLocalTimeAsStringWithFormat() {
		//We'll ask for format: "HH:mm:ss"
		String dateFormatString = "HH:mm:ss";
		//user_noPrivs is set as Europe/London, function should return GMT
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
		sdf.setTimeZone(TimeZone.getTimeZone((String)user_noPrivs_config.get("timezone")));
		String expected =  sdf.format(new Date());
		String calculated = this.user_noPrivs.getStaffLocalTimeAsStringWithFormat(dateFormatString);
		
		Assert.assertEquals("Checking Time of user.",expected, calculated);
		
	}

	/**
	 * Test method for {@link com.damedesuyo.irc.god_bot.StaffMember#setTimezone(java.lang.String)}.
	 */
	@Test
	public void testSetTimezone() {
		//fail("Not yet implemented");
		//
	}

	/**
	 * Test method for {@link com.damedesuyo.irc.god_bot.StaffMember#hasPrivilege(com.damedesuyo.irc.god_bot.Privilege)}.
	 */
	@Test
	public void testHasPrivilege() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.damedesuyo.irc.god_bot.StaffMember#validateTimezone(java.lang.String)}.
	 */
	@Test
	public void testValidateTimezone() {
		Gson gson = new Gson();
		String acceptedTimezones_Json = "['Europe/London', 'GMT']";
		//TODO [Enchancement:test] iterate over JSON parsed array, asserting true for all values.
		
		
		String unacceptableTimezones_Json = "['europe/london','gmt']";
		//TODO [Enchancement:test] iterate over JSON parsed array, asserting false for all values.
		
	}

	/**
	 * Test method for {@link com.damedesuyo.irc.god_bot.StaffMember#deHighlightUsername(java.lang.String)}.
	 */
	@Test
	public void testDeHighlightUsername() {
		//fail("Not yet implemented");
	}

}
