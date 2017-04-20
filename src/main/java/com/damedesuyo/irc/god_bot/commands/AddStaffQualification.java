package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.Privilege;
import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;
import com.damedesuyo.irc.god_bot.dictionary.AppDictionary;
/**
 * Adds a new Alias against a staff member.
 * @author Chrolo
 *
 */
public class AddStaffQualification implements BotCommand{

	@Override
	public String getHelp() {
		return "Adds a new qualfication to the given user. Use the format: "+this.getShortHelp();
	}

	@Override
	public String getShortHelp() {
		return "<command> <Staff name> <qualification to add>";
	}

	@Override
	public void execute(MessageEvent event, String argString) 
	{
		String args[] = argString.split(" ");
		StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
		boolean isVerified = event.getUser().isVerified();
		if(isVerified && user.hasPrivilege(Privilege.ADD_QUALIFICATION))	//if they're verified AND it's the registered user OR someone with ADD_STAFF privilege.
		{
			UserDatabase userDatabase = UserDatabase.getSharedInstance();			
			
			//Validate and Add the user data:
			if(argString.length()==0 || args[0].length()==0 || args[1] == null || args[1].length()==0)
			{
				event.respondChannel(getShortHelp());
				return;
			}
			
			//TODO [Enhancement] Validate that qualification exists
			if(!userDatabase.isExistingUser(args[0]))
			{	
				event.respondChannel(StaffMember.deHighlightUsername(args[0])+" doesn't exist on my database" );
				return;
			}
			
			if(userDatabase.addQualificationToStaffMemeber(args[0], AppDictionary.getSharedInstance().getSubstitutionAgainstContext(args[1], "Qualifications")))
			{
				event.respondChannel(StaffMember.deHighlightUsername(args[0])+" is now has the qualification: "+args[1] );
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
				event.respondChannel("You don't have permission to do that. Check your privelege.");
			}
			else
			{
				event.respondChannel("You're not verified for that Nickname, so I can't trust you.");
			}
			
		}
		
	}

}
