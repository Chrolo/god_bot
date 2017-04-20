package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.hooks.events.MessageEvent;

public class Help implements BotCommand {

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(MessageEvent event, String argString) {
		//Get an instance of BotCommands
		BotCommands commandLib = new BotCommands();
		
		//Print all help:
		for(String command : commandLib.getSetOfCommands())
		{
			try {
				BotCommand temp = commandLib.getCommandClassInstance(command);
				event.respondChannel("I'm going to PM you my commands");
				event.respondPrivateMessage(command+":\t\t"+temp.getShortHelp());
			} catch (BotCommandNotFound e) {
				System.err.println("[Help Command] Somehow a command found from the list somehow didn't exist...\n"+e.getMessage());
			}
			
		}

	}

}
