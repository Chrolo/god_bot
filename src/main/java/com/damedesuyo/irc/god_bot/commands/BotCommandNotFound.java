package com.damedesuyo.irc.god_bot.commands;

public class BotCommandNotFound extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BotCommandNotFound(String message)
	{
		super(message);
	}
}
