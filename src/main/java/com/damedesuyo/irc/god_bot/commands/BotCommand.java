package com.damedesuyo.irc.god_bot.commands;

import org.pircbotx.hooks.events.MessageEvent;

/**
 * An interface for all bot commands to follow.
 * It greatly simplifies the creation of a dynamic 'help' command.
 * 
 * An individual command may implement extra variables that need to be set before calling execute,
 * but ideally should be able to operate with just the event and argString
 * @author Chrolo
 *
 */
public interface BotCommand {
	/**
	 * 
	 * @return The help string for this command
	 */
	String getHelp();
	
	/**
	 * 
	 * @return A short (try to be one line) help string
	 */
	String getShortHelp();
	
	/**
	 * 
	 * @param event The PircBot Message event related to the command call
	 * @param args The string of args following the command call.
	 */
	void execute(MessageEvent event, String argString);
}
