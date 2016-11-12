package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.Privilege;
import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;
/**
 * Removes all data about user from the database. Cleans out both staff and staffAliases tables.
 * @author Chrolo
 *
 */
public class RemoveUser implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		return "<command> <username to delete>";
	}

	@Override
	public void execute(MessageEvent event, String argString) 
	{		
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
		boolean isVerified = event.getUser().isVerified();
		if(isVerified &&user.hasPrivilege(Privilege.REMOVE_STAFF))
		{

			UserDatabase userDatabase = UserDatabase.getSharedInstance();
			
			//Get the info for the person to be deleted:
			String args[] = argString.split(" ");
			userDatabase.getStaffMember(args[0]);		
			
			if(userDatabase.deleteUser(args[0]))
			{
				event.respondChannel("I've erased all traces of '"+StaffMember.deHighlightUsername(args[0])+"' ¬_¬" );
			}
			else
			{
				event.respondChannel("Something went wrong ;_;");
			}

		}
		else
		{
			if(isVerified)
			{
				event.respondChannel("I'm afraid I can't let you do that Dave");
			}
			else
			{
				event.respondChannel("You're not verified for that Nickname, so I can't trust you.");
			}
			
		}
	}

}
