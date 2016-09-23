package com.damedesuyo.irc.god_bot;

public class MySqlColumn {
	//String name;	// field name in database
	String desc_s;	// a short description of it (used for Table headers if displayed)
	String desc;	// a more detailed description (tool tip?)
	String type;	// The mysql Type used to declare the field
	String extra;	// any extra commands added to declare the field
	boolean primaryKey;	// is it a primary key?
	
	//overrides:
		@Override public String toString()
		{
			return "{ desc_s= '"+this.desc_s+"', desc='"+this.desc+"', type='"+this.type+"', extra='"+this.extra+"', primaryKey="+this.primaryKey+"}";
		}
}
