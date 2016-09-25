package com.damedesuyo.irc.god_bot.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class EchoUserFacts implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		return "Tells you what the Bot knows about you.";
	}

	@Override
	public void execute(MessageEvent event, String argString) {	
		
		String infoFound = userInfoToString(event.getUser());
		
		//Output to local console:
		System.out.println("User info:\n"+infoFound.replace("5","").replaceAll("1", ""));
		
		//Output to channel:
		//event.respondChannel("What I know about you is :\n"+infoFound);
		event.respondChannel("What I know about you is :");
		String[] lines = infoFound.split("\n");
		for(String line : lines)
		{
			event.respondChannel(line);
		}
		
	}
	
	public static HashMap<String,String> userInfo(User user)
	{
		HashMap<String,String> userInfo = new HashMap<String,String>();
		userInfo.put("Nick", user.getNick());
		userInfo.put("Ident", user.getIdent());
		userInfo.put("Real Name", user.getRealName());
//		userInfo.put("user to string", user.toString());
		userInfo.put("Has Identified?", String.valueOf(user.isVerified()));
		userInfo.put("Login", user.getLogin());
		userInfo.put("Hostmask", user.getHostmask());
		System.out.println("[userInfo] user:"+user.toString());
		
		
		return userInfo;
	}
	
	public static String hashmapToString(HashMap<String,String> hashmap)
	{
		String str = "";
		boolean comma = false;
		
		for (Entry<String, String> entry : hashmap.entrySet())
		{
		    if(comma){str=str.concat(",\n");};
		    comma = true;
			str = str.concat("5"+entry.getKey()+"1:\t "+entry.getValue());
		}
		
		return str;
	}
	
	public static String userInfoToString(User user)
	{
		return hashmapToString(userInfo(user));
	}
}
