package com.damedesuyo.irc.god_bot.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.Privilege;
import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;

public class BotCommands_ModifyStaff {
	public static void AddStaff(MessageEvent event, String command){
		//TODO: make sure user has identified for nickname
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
		if(user.hasPrivilege(Privilege.ADD_STAFF))
		{
			//event.respondChannel("You'd be allowed to Add staff, but This command isn't ready yet! ;_;");
			//*
			UserDatabase userDatabase = UserDatabase.getSharedInstance();
			
			//Get the new user's info:
			
			Map<String,Object> info = new HashMap<String,Object>();
			String args[] = command.split(" ");
			
			//Validate and Add the user data:
			//TODO: Validate the data:
			info.put("userName", args[1]);
			
			
			try {
				if(userDatabase.addStaffToDatabase(args[1]))
				{
					event.respondChannel("I've added "+args[1]+" to the staff. Welcome "+args[1]+"!" );
				}
				else
				{
					event.respondChannel("Something went wrong, but I don't know what ;_;");
				}
			} catch (SQLException e) {
				event.respondChannel("Well shit, something fucked up on the database ;_;");
				e.printStackTrace();
			}
			
			//*/
		}
		else
		{
			event.respondChannel("I'm afraid I can't let you do that Dave");
		}
		
	}
}
