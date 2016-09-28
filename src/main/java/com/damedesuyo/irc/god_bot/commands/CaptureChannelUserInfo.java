package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import com.google.common.collect.ImmutableSortedSet;

public class CaptureChannelUserInfo implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "Super sekrit command";
	}

	@Override
	public void execute(MessageEvent event, String argString) {
		// TODO Auto-generated method stub
		ImmutableSortedSet<User> users = event.getChannel().getUsers();
		
		for(User t_user : users)
		{
			System.out.println(EchoUserFacts.userInfoToString(t_user).replace("5","").replace("1", ""));
		}
		
	}

}
