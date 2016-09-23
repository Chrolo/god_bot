package com.damedesuyo.irc.god_bot.database_def;

import java.util.Map;

import com.damedesuyo.irc.god_bot.MySqlColumn;

public class DatabaseTableDefinition {
	public Map<String, MySqlColumn> columns;	// < -Name of collum-, -collumn data- >	
	
	//overrides:
	@Override public String toString() 
	{
		return columns.toString();
	}
}
