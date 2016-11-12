package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;

public class SetTimezone implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		return  "Set your Timezone. Needs to be something like <Continent>/<Town> as seen here: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones";
	}

	@Override
	public void execute(MessageEvent event, String argString) {
		//TODO [Security] Make sure only identified users can set their timezones
		String args[] = argString.split(" ");
		String newtimeZone = args[0];
		
		//check if timezone given is valid:
		if(!StaffMember.validateTimezone(newtimeZone))
			event.respondChannel("Sorry, '"+newtimeZone+"' isn't a valid timeZone. Needs to be something like <Continent>/<Town> as seen here: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones");
		else
		{
			StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
			if(user != null)
			{
				try {
					user.setTimezone(newtimeZone);
				} catch (Exception e) {
					event.respondChannel("Sorry, '"+newtimeZone+"' isn't a valid timeZone. Needs to be something like <Continent>/<Town> as seen here: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones");
				}
				event.respondChannel("So you're in "+newtimeZone+", eh?. I'll remember that!");
			}
			else
			{
				event.respondChannel("I don't have anyone called '"+StaffMember.deHighlightUsername(event.getUser().getNick())+"' listed in my database.");
			}
		}
	}

}
