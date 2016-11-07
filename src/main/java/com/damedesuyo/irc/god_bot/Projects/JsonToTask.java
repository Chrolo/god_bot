package com.damedesuyo.irc.god_bot.Projects;

/**
 * This was gonna get too messy for the Task class, so I split it out into it's own class.
 * @author Chrolo
 *
 */
public class JsonToTask {
	
	/**
	 * This function is used to look for the end of a JSON string element.
	 * @param str The string to be processed, minus the first "
	 * @return the integer index just before the finishing quote.
	 */
	public static int findStringEndIndex(String str)
	{
		int endIndex = 0;
		boolean flag = true;
		
		for(endIndex=0 ; endIndex<str.length() && flag; endIndex++)
		{
			if(str.charAt(endIndex) == '"')
			{
				flag = false;
			}
			else if( str.charAt(endIndex) =='\\')
			{
				endIndex++;
			}
		}
		
		if(!flag)
			endIndex--;
		
		return endIndex;
	}
	
	/**
	 * This function is used to look for the end of a JSON array element.
	 * @param str The string to be processed, minus the first [
	 * @return the integer index just before the finishing ']'.
	 */
	public static int findArrayEndIndex(String array)
	{
		int endIndex = 0;
		boolean flag = true;
		
		for(endIndex=0 ; endIndex<array.length() && flag; endIndex++)
		{
			if(array.charAt(endIndex) == ']')
			{
				flag = false;
			}
			else if( array.charAt(endIndex) =='"')
			{
				endIndex += JsonToTask.findStringEndIndex(array.substring(endIndex+1, array.length())) + 1;
			}
			else if( array.charAt(endIndex) =='[')
			{
				endIndex += JsonToTask.findArrayEndIndex(array.substring(endIndex+1, array.length())) + 1;
			}
		}
		
		if(!flag)
			endIndex--;
		
		return endIndex;
	}
	
	/**
	 * This function is used to look for the end of a JSON array element.
	 * @param str The string to be processed, minus the first [
	 * @return the integer index just before the finishing ']'.
	 */
	public static int findObjectEndIndex(String object)
	{
		int endIndex = 0;
		boolean flag = true;
		
		for(endIndex=0 ; endIndex<object.length() && flag; endIndex++)
		{
			if(object.charAt(endIndex) == '}')
			{
				flag = false;
			}
			else if( object.charAt(endIndex) =='"')
			{
				endIndex += JsonToTask.findStringEndIndex(object.substring(endIndex+1, object.length())) + 1;
			}
			else if( object.charAt(endIndex) =='{')
			{
				endIndex += JsonToTask.findObjectEndIndex(object.substring(endIndex+1, object.length())) + 1;
			}
		}
		
		if(!flag)
			endIndex--;
		
		return endIndex;
	}
}
