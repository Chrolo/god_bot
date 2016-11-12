package com.damedesuyo.irc.god_bot.commands;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;

import com.damedesuyo.irc.god_bot.StaffMember;
import com.damedesuyo.irc.god_bot.UserDatabase;
import com.damedesuyo.irc.god_bot.dictionary.AppDictionary;

public class WhoCan implements BotCommand {

	@Override
	public String getHelp() {
		return "Ask for a job like 'typesetter' or 'encoder' and get a list of all known staff hired for that.";
	}

	@Override
	public String getShortHelp() {
		return "Check who is qualified to do something.";
	}

	@Override
	public void execute(MessageEvent event, String argString) {
		String args[] = argString.split(" ");
		if(args[0] != "")
		{
			String qualification = AppDictionary.getSharedInstance().getSubstitutionAgainstContext(args[0], "Qualifications");
			System.out.println("[WhoCan] Searching for: "+qualification+" (originally given: "+args[0]+")");
			
			ArrayList<StaffMember> staff = UserDatabase.getSharedInstance().getMembersQualifiedFor(qualification);
			
			if(staff == null)	//TODO: remove this once i've done entry validation.
			{
				event.respondChannel("Oops, something went wrong ;_; Maybe try another word for the qualification?");
				return;
			}
			
			
			String staffString = ""; boolean flag= false;
			for(StaffMember staff_c : staff)
			{
				if(flag)
					staffString += ", ";
				flag = true;
				staffString += StaffMember.deHighlightUsername(staff_c.username) ;
			}
			event.respondChannel("The '"+args[0]+ "' people are: "+staffString);
		}
		else
		{
			event.respondChannel("You need to tell me what qualification is needed.");
		}
		
	}

}
