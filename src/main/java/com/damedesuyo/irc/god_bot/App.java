package com.damedesuyo.irc.god_bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import com.damedesuyo.irc.god_bot.commands.BotCommand;
import com.damedesuyo.irc.god_bot.commands.BotCommandNotFound;
import com.damedesuyo.irc.god_bot.commands.BotCommands;
import com.damedesuyo.irc.god_bot.database_def.DatabaseDefinition;
import com.damedesuyo.irc.god_bot.dictionary.AppDictionary;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class App  extends ListenerAdapter
{
	//App Settings:
	private static String botCommandIdentifier;
	static BotCommands botCommands;
	//Static Class instances:
	static PircBotX bot;
	
	public void onMessage(MessageEvent event) 
	{
		if(event.getMessage().startsWith(botCommandIdentifier))
		{
			
			
			//Cut out the command identifier:
			String modMessage = event.getMessage().substring(botCommandIdentifier.length(),event.getMessage().length());
			String command_args[] = modMessage.split(" ");
			String command = command_args[0];
			
			//logging:
			System.out.println("'"+event.getUser().getNick()+"' used the command '"+modMessage+"'");
			
			//check new Commands API
			if(botCommands.getSetOfCommands().contains(command))
			{
				try 
				{
					BotCommand commandInst = botCommands.getCommandClassInstance(command);
					//Get arg string:
					String argString = modMessage.substring(command.length(),modMessage.length()).trim();	
					commandInst.execute(event, argString);
				} 
				catch (BotCommandNotFound e)
				{
					e.printStackTrace();
				}
				catch(Exception e)
				{
					event.respondChannel("something went wrong ;_;");
					System.out.println("Command '"+command+"' failed with uncaught exception:");
					e.printStackTrace();
				}
				return; //don't bother with the rest.
			}
			
			//Now look through Old APIs to respond:
			//------------------------------------------------------------------------
			else if(modMessage.startsWith("ShutDown"))
			{
				StaffMember user = UserDatabase.getSharedInstance().getStaffMember(event.getUser().getNick());
				boolean isVerified = event.getUser().isVerified();
				if(isVerified && user.hasPrivilege(Privilege.SHUTDOWN_BOT))
				{
					event.respondChannel("Oyasumi!");
					bot.close();
				}
				else
					event.respondChannel("You're not my REAL father! Screw you!");
			}
			//------------------------------------------------------------------------
			else
			{
				event.respondChannel("I saw you put '"+botCommandIdentifier+"', but I didn't understand what you said after that... ;_;");
			}
		}
		else if(event.getMessage().startsWith("fabulous?"))
		{
			event.respondChannel("05F04A07B08U03L02O06U13S");
		}
		else if(event.getMessage().startsWith("who is fabulous?"))
		{
			//select random user in channel:
			ImmutableSortedSet<String> users = event.getChannel().getUsersNicks();
			Random dice = new Random();
			int rand = dice.nextInt(users.asList().size());
			String user = users.asList().get(rand);
			event.respondChannel(StaffMember.deHighlightUsername(user)+" is 05F04A07B08U03L02O06U13S");
		}
	}

	public static void main(String[] args) throws Exception {

		//-----------------------------------------------------------------------
		//--Sys Init --//
		Wini ini = new Wini(new File("config/settings.ini"));
		
		botCommands = new BotCommands();
		botCommandIdentifier = ini.get("bot","botCommandIdentifier",String.class);
		
		//Print Banner?
		String bannerPath = ini.get("appSettings","banner",String.class);
		if(bannerPath != null)
		{
			try (Stream<String> bannerStream = Files.lines(Paths.get(bannerPath))) {

				bannerStream.forEach(System.out::println);

			} catch (IOException e) {
				System.err.println("Banner specified @ '"+bannerPath+"' but could not be loaded by system");
			}
		}
		
		Gson gson = new Gson();
		//Read JSON from file:
		DatabaseDefinition dbDef = new DatabaseDefinition();
		Reader dbDefFile = new FileReader("config/databaseDefinitions.json");
		dbDef = gson.fromJson(dbDefFile, dbDef.getClass());
		
		
		//Configure what we want our bot to do
		Configuration configuration = new Configuration.Builder()
				.setName(ini.get("bot","name",String.class))
				.setLogin(ini.get("bot","name",String.class))
				.setNickservPassword(ini.get("bot","password",String.class))
				.addServer(ini.get("bot","server",String.class))
				.addAutoJoinChannel(ini.get("bot","mainChannel",String.class))
				.setRealName(ini.get("bot","name",String.class))
				
				.addListener(new App())
				.buildConfiguration();
		
		//--------------------------------------------------------
		//Running stuff:

		//*
         //Create our bot with the configuration
         bot = new PircBotX(configuration);
         //Connect to the server
         bot.startBot();  
        //*/
	}
	

	//Problem Cases:
	
	public void onNickAlreadyInUse(NickAlreadyInUseEvent event)
	{
		System.out.println("Aparently that nicknames already in use. I'll try to kick them!");
		System.out.println("..or atleast I would if Chrolo knew how");
		event.respond(event.getUsedNick()+"_");
		
	}
	
	
}
