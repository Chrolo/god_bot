package com.damedesuyo.irc.god_bot.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.Privilege;
import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;
/**
 * Adds a new Staff member to the database.
 * Currently this will only add a username. Expansions will be made to allow more complete userinfo to be added in one command.
 * @author Chrolo
 *
 */
public class AddUser implements BotCommand{

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		return "<command> <username to add>";
	}

	@Override
	public void execute(MessageEvent event, String argString) 
	{
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
		boolean isVerified = event.getUser().isVerified();
		if(isVerified && user.hasPrivilege(Privilege.ADD_STAFF))
		{

			UserDatabase userDatabase = UserDatabase.getSharedInstance();
			
			//Get the new user's info:
			Map<String,Object> info = new HashMap<String,Object>();
			String args[] = argString.split(" ");
			
			//Validate and Add the user data:
			if(argString.length()==0 || args[0].length()==0)
			{
				event.respondChannel(getShortHelp());
				return;
			}
			
			if(userDatabase.isExistingUser(args[0]))
			{	
				event.respondChannel("I've already got "+args[0]+" listed." );
				return;
			}

			
			info.put("userName", args[0]);
			
			
			try {
				int res = userDatabase.addStaffToDatabase(args[0]);
				if(res==0)
				{
					event.respondChannel("I've added "+args[0]+" to the staff. Welcome "+args[0]+"!" );
				}
				else if(res == -2)
				{
					event.respondChannel("I've already got "+args[0]+" listed." );
				}
				else
				{
					event.respondChannel("Something went wrong, but I don't know what ;_;");
				}
			} catch (SQLException e) {
				event.respondChannel("Well shit, something fucked up on the database ;_;");
				e.printStackTrace();
			}
		}
		else
		{
			if(isVerified)
			{
				event.respondChannel("I'm afraid I can't let you do that Dave (You don't have Staff adding priviledge)");
			}
			else
			{
				event.respondChannel("You're not verified for that Nickname, so I can't trust you.");
			}
			
		}
		
	}

}
