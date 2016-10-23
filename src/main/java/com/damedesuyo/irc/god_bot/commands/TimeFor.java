package com.damedesuyo.irc.god_bot.commands;

import java.util.Arrays;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;

@SuppressWarnings("unused")
public class TimeFor implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		return "Gets the local time of the given staff member. Can be interspersed with '#' to prevent highlighting.";
	}

	@Override
	public void execute(MessageEvent event, String argString) {
		String args[] = argString.split(" ");
		String queriedMember = args[0];
		//Strip out the fillerCharacters:
		queriedMember = queriedMember.replace("#","");
		
		//Create user class:
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(queriedMember);
		//user.printStaffDataToConsole();
		
		if(user == null)
		{
			event.respondChannel("I don't have '"+StaffMember.deHighlightUsername(queriedMember)+"' listed in my database.");
		}
		else
		{
			String userTime = user.getStaffLocalTimeAsStringWithFormat("HH:mm:ss");
			if(userTime != null)
				event.respondChannel(StaffMember.deHighlightUsername(queriedMember)+"'s current time is: "+user.getStaffLocalTimeAsStringWithFormat("HH:mm:ss"));
			else
				event.respondChannel(StaffMember.deHighlightUsername(queriedMember)+" hasn't told me their timezone.");
		}
		//*/
	}

}
