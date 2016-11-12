package com.damedesuyo.irc.god_bot.commands;

import java.util.HashMap;
import java.util.Set;


public class BotCommands {
	
	private HashMap<String,Class<? extends BotCommand>> commands;
	//--------------------------------------------------------------------------------
	//Constructors:
	public BotCommands()
	{
		commands = new HashMap<String, Class<? extends BotCommand>>();
		//create a class of each possible command:
		commands.put("setTimezone", SetTimezone.class);
		commands.put("timefor", TimeFor.class);
		commands.put("addUser", AddUser.class);
		commands.put("addAlias", AddStaffAlias.class);
		commands.put("removeUser", RemoveUser.class);
		
		commands.put("whoami?", EchoUserFacts.class);
		commands.put("1337", CaptureChannelUserInfo.class);
		commands.put("help", Help.class);
		commands.put("whocan", WhoCan.class);
	}
	
	//-------------------------------------------------------------------------------
	//Querying all commands
	public Set<String> getSetOfCommands()
	{
		return commands.keySet();
	}
	
	public int commandCount()
	{
		return commands.size();
	}
	
	//-------------------------------------------------------------------------------
	//Using a specific command:	
	public BotCommand getCommandClassInstance(String command) throws BotCommandNotFound 
	{
		Class<? extends BotCommand> commandClass = commands.get(command);

		BotCommand commandClassInst = null;
		try 
		{
			commandClassInst = commandClass.newInstance();
		}
		catch (InstantiationException e)
		{
			System.out.println("[BotCommands.getCommandClassInstance] Command"+command+" Threw InstantiationException:\n"+e.getMessage());
			throw new BotCommandNotFound(command);
		}
		catch (IllegalAccessException e)
		{
			System.out.println("[BotCommands.getCommandClassInstance] Command"+command+" Threw IllegalAccessException:\n"+e.getMessage());
			throw new BotCommandNotFound(command);
		}
		
		return commandClassInst;
	}
	

}
