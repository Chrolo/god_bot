# God Bot
_or not_

## What the hell is this?
The beginnings of an IRC bot. It's based off [PIRCbotx](https://github.com/TheLQ/pircbotx/) and maybe someday it'll be useful to have in fansub channels.

## How can I use it?
Clone and build this. You'll need Maven and Jdk-8
- `mvn compile`
- `mvn exec:java -Dexec.mainClass="com.damedesuyo.irc.god_bot.App"` 

You'll also need to put your config into `config/settings.ini`. See the example for, you know, an example.

You'll also need to pre-initialise the mysql server. Check out the `config/databaseDefinitions.json` and you can use the creation scripts under `/databaseSetupScripts` (requires php)

## X doesn't work / I've spotted a huge security flaw / git gud
Please file an issue on github. If I don't respond within 3 days, highlight me on IRC until I kickban you.
I can usually be found on [Rizon #DameDesuYo](irc://irc.rizon.net/DameDesuYo)


## Goals
 - Staff Management System
	- [x] Allow staff to easily find out the time for other staff members, without having to know/remember their locations.
	- Different staff privilege levels:
		- [x] Adding/Removing staff
		- [ ] seeing alternate contact info
	- [ ] Checking what projects a staff member is assigned to
	- [ ] Getting alternate contact details for a staff member
	- [x] find out staff qualifications (Typesetter, Editor, Encoder, etc)
 - Project Management
  - [ ] Getting a list of current projects
  - [ ] Getting a list of past projects
  - For a given project:
	 - [ ] See current progress
	 - [ ] See who's stalling it
	 - [ ] See who's running it
	 - [ ] Find an episode torrent
 - General use
  - [x] Create a way to initialise the database
  - [ ] Track username changes in the channel.
  - [x] Identify users to correctly set permission levels
  - [ ] 'help' command should only mention commands a user has permission for.
  - [ ] Setup a good logging system
 - Misc
	- [ ] Unit tests...
