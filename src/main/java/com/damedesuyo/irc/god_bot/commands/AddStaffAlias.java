package com.damedesuyo.irc.god_bot.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.Privilege;
import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;
/**
 * Adds a new Alias against a staff member.
 * @author Chrolo
 *
 */
public class AddStaffAlias implements BotCommand{

	@Override
	public String getHelp() {
		return "Adds a new alias to the given user. Use the format: "+this.getShortHelp();
	}

	@Override
	public String getShortHelp() {
		return "<command> <Nick already on Database> <alias to add>";
	}

	@Override
	public void execute(MessageEvent event, String argString) 
	{
		String args[] = argString.split(" ");
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
		boolean isVerified = event.getUser().isVerified();
		if(isVerified && ( args[0] == event.getUser().getNick() || user.hasPrivilege(Privilege.ADD_STAFF)))	//if they're verified AND it's the registered user OR someone with ADD_STAFF privilege.
		{
			UserDatabase userDatabase = UserDatabase.getSharedInstance();
			
			//Get the new user's info:
			Map<String,Object> info = new HashMap<String,Object>();
			
			
			//Validate and Add the user data:
			if(argString.length()==0 || args[0].length()==0 || args[1] == null || args[1].length()==0)
			{
				event.respondChannel(getShortHelp());
				return;
			}
			
			if(userDatabase.isExistingUser(args[1]))
			{	
				event.respondChannel("I've already got "+StaffMember.deHighlightUsername(args[1])+" listed as alias of "+userDatabase.getStaffMember(args[1]).getNonHLUsername() );
				return;
			}
			
			if (userDatabase.addNewAlias(args[0], args[1]))
			{
				event.respondChannel("I've added "+StaffMember.deHighlightUsername(args[1])+" as an alias of "+StaffMember.deHighlightUsername(args[0])+"!" );
			}
			else
			{
				event.respondChannel("Something went wrong, but I don't know what ;_;");
			}
		}
		else
		{
			if(isVerified)
			{
				event.respondChannel("You can only add aliases to other people if you have permission. Check your privelege.");
			}
			else
			{
				event.respondChannel("You're not verified for that Nickname, so I can't trust you.");
			}
			
		}
		
	}

}
