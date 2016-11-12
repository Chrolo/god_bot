package com.damedesuyo.irc.god_bot.dictionary;

import java.util.Map;
/**
 * This is a class to help with simple string substitutions.
 * Each instance of 'Dictionary' should represent a different context,
 * with a DictionaryType relevant to that context.
 * @author Chrolo
 *
 */
public class Dictionary {
	
	public Map<String,String> dictionary;
	private DictionaryType type;
	
	//--------------------------------------
	@Override
	public String toString(){
		return "{dictionary: \"" + dictionary + "\", type: \"" + type + "\"}";
	}
}
