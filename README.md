# God Bot
_or not_

## What the hell is this?
The beginnings of an IRC bot. It's based off [PIRCbotx](https://github.com/TheLQ/pircbotx/) and maybe someday it'll be useful to have in fansub channels.

## How can I use it?
Clone and build this. You'll need Maven.

You'll also need to put your config into 'config/settings.ini'. See the example for, you know, an example.

You'll also need to pre-initialise the mysql server. Maybe i'll get round to automating this, but for now check out the 'config/databaseDefinitions.json'

## X doesn't work / I've spotted a huge security flaw / git gud
Please file an issue on github. If I don't respond within 3 days, highlight me on IRC until I kickban you.


## Goals
 - Staff Management System
	- [ ] Allow staff to easily find out the time for other staff members, without having to know/remember their locations.
	- Different staff privilege levels:
		- [ ] Adding/Removing staff
		- [ ] seeing alternate contact info
	- [ ] Checking what projects a staff member is assigned to
	- [ ] Getting alternate contact details for a staff member
 - Project Management
  - [ ] Getting a list of current projects
  - [ ] Getting a list of past projects
  - For a given project:
	 - [ ] See current progress
	 - [ ] See who's stalling it
	 - [ ] See who's running it
	 - [ ] Find an episode torrent
 - General use
  - [ ] Create a way to initialise the database
	 - I've actually got one I wrote in php for this project, but I don't know if it's "production ready"
  - [ ] Track username changes in the channel.
  - [ ] Identify users to correctly set permission levels
 - Misc
	- [ ] Unit tests...
