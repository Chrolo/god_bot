package com.damedesuyo.irc.god_bot.database_def;

import java.util.Map;

public class DatabaseDefinition {
	public Map<String, DatabaseTableDefinition> tables;
	
	//overrides:
		@Override public String toString()
		{
			return tables.toString();
		}
}
