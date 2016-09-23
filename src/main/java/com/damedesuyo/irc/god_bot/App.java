package com.damedesuyo.irc.god_bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import com.damedesuyo.irc.god_bot.commands.BotCommands_ModifyStaff;
import com.damedesuyo.irc.god_bot.database_def.DatabaseDefinition;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;

public class App  extends ListenerAdapter
{
	//App Settings:
	private static String botCommandIdentifier;

	//Static Class instances:
	static PircBotX bot;
	
	public void onMessage(MessageEvent event) 
	{
		if(event.getMessage().startsWith(botCommandIdentifier))
		{
			//Cut out the command identifier:
			System.out.println("Command Seen: '"+event.getMessage()+"'");
			String modMessage = event.getMessage().substring(botCommandIdentifier.length(),event.getMessage().length());
			System.out.println("ModMessage: "+modMessage);
			//event.respondChannel("Hello world!");
			
			//Now look through APIs to respond:
			if (modMessage.startsWith("hello"))
			{
				System.out.println("Saw a hello message");
				String user_d = StaffMember.deHighlightUsername(event.getUser().getNick());
				
				event.respondChannel("Hello "+user_d +"!");
			}
			//-------------------------------------------------------------------------
			else if(modMessage.startsWith("timefor"))
			{	//Asking what the timezone for staff member is:
				String args[] = modMessage.split(" ");
				
				//Create user class:
				StaffMember user = UserDatabase.getSharedInstance().getStaffMember((String)args[1]);
				//user.printStaffDataToConsole();
				
				if(user == null)
				{
					event.respondChannel("I don't have '"+StaffMember.deHighlightUsername((String)args[1])+"' listed in my database.");
				}
				else
				{
					String userTime = user.getStaffLocalTimeAsStringWithFormat("HH:mm:ss");
					if(userTime != null)
						event.respondChannel(StaffMember.deHighlightUsername(user.username)+"'s current time is: "+user.getStaffLocalTimeAsStringWithFormat("HH:mm:ss"));
					else
						event.respondChannel(StaffMember.deHighlightUsername(user.username)+" hasn't told me their timezone.");
				}
				
			}
			//-------------------------------------------------------------------------
			else if(modMessage.startsWith("setMyTimezone"))
			{
				//TODO : [Security] Make sure only identified users can set their timezones
				String args[] = modMessage.split(" ");
				String newtimeZone = args[1];
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
						event.respondChannel("I don't have '"+StaffMember.deHighlightUsername(event.getUser().getNick())+"' listed in my database.");
					}
				}
				
			}
			//------------------------------------------------------------------------
			else if(modMessage.startsWith("AddStaff"))
			{
				BotCommands_ModifyStaff.AddStaff(event, modMessage);
			}
			//------------------------------------------------------------------------
			else if(modMessage.startsWith("ShutDown"))
			{
				if(event.getUser().getNick().equals("Chrolo"))	//Only the best security practises here kiddo!
					bot.close();
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
			System.out.println(users.toString());
			System.out.println("Users.aslist().get(0) is :"+users.asList().get(0));
			System.out.println("List was "+users.asList().size()+" in size");
			Random dice = new Random();
			int rand = dice.nextInt(users.asList().size());
			System.out.println("Random dice rolled:"+rand);
			String user = users.asList().get(rand);
			event.respondChannel(StaffMember.deHighlightUsername(user)+" is 05F04A07B08U03L02O06U13S");
		}
	}

	public static void main(String[] args) throws Exception {

		//-----------------------------------------------------------------------
		//--Sys Init --//
		Wini ini = new Wini(new File("config/settings.ini"));
		
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
		
		/*
		//Test App
		
		//Print out list of users
		UserDatabase userDatabase = UserDatabase.getSharedInstance();
		System.out.println("Existing usernames are"+userDatabase.getValidUsers());
		
		
		//Get Chrolo:
		StaffMember chrolo_u = userDatabase.getStaffMember("Chrolo");
		chrolo_u.printStaffDataToConsole();
		//and print his timezone:
		System.out.println("Time for "+chrolo_u.username+" is "+chrolo_u.getStaffLocalTimeAsStringWithFormat("HH:mm:ss"));
		
		//Get Begna
		StaffMember begna_u = userDatabase.getStaffMember("begna112");
		begna_u.printStaffDataToConsole();
		//and print his timezone:
		System.out.println("Time for "+begna_u.username+" is "+begna_u.getStaffLocalTimeAsStringWithFormat("HH:mm:ss"));
		
		//Get *?
		StaffMember star_u = userDatabase.getStaffMember("*");
		if(star_u != null)
		{
			star_u.printStaffDataToConsole();
			//and print his timezone:
			System.out.println("Time for "+star_u.username+" is "+star_u.getStaffLocalTimeAsStringWithFormat("HH:mm:ss"));
		}
		
		//*/
		
		//Test the GSON library:
		Gson gson = new Gson();
		//Create a database table definition:
		/*
		ArrayList<MySqlColumn> columns = new ArrayList<MySqlColumn>();
		columns.add(new MySqlColumn());
		columns.add(new MySqlColumn());
		DatabaseTableDefinition dbTableDef = new DatabaseTableDefinition("TableName", columns);
		
		//Print JSON:
		System.out.println("JSON of Table: "+gson.toJson(dbTableDef));
		*/
		/*
		Map<String, Object> test = new HashMap<String, Object>();
		test = gson.fromJson("{'staff':{'id':{'name':'field', 'type':'int(11)'}, 'nickName':{'type':'string'} }, 'staffAl':{'num':5} }", test.getClass());
		System.out.println("Test became:"+test);
		System.out.println("test[staff] is:"+test.get("staff"));
		//System.out.println("test[staff][type] is:"+test.get("staff").type);
		*/
		
		//Read JSON from file:
		DatabaseDefinition dbDef = new DatabaseDefinition();
		Reader dbDefFile = new FileReader("config/databaseDefinitions.json");
		dbDef = gson.fromJson(dbDefFile, dbDef.getClass());
		System.out.println("After Reading JSON, dbTableDef is: "+dbDef);
		System.out.println("After Reading JSON, dbTableDef.tables[staff] is: "+dbDef.tables.get("staff"));
		
		//Configure what we want our bot to do
		Configuration configuration = new Configuration.Builder()
				.setName(ini.get("bot","name",String.class))
				.addServer(ini.get("bot","server",String.class))
				.addAutoJoinChannel(ini.get("bot","mainChannel",String.class))
				.addListener(new App())
				.buildConfiguration();

		/*
		botCommandIdentifier = "++";
		/*/
		botCommandIdentifier = ini.get("bot","botCommandIdentifier",String.class);
		//*/

		//--------------------------------------------------------
		//Running stuff:

		//*
         //Create our bot with the configuration
         bot = new PircBotX(configuration);
         //Connect to the server
         bot.startBot();
        //*/
	}
	
	
}
