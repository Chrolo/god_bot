package com.damedesuyo.irc.god_bot.projects;

import com.google.gson.Gson;

/**
 * This was gonna get too messy for the Task class, so I split it out into it's own class.
 * @author Chrolo
 *
 */
public class JsonToTask {
	
	public static Task convertJsonToTasks(String JSON)
	{
		//Variable prep
		Gson gson = new Gson();
		Task retTask;
		
		//JSON pre-formatting:
			/**
			 * Performing some pre-processing checks to help prevent errors:
			 * 1) change taskID -> taskId
			 * 2) 
			 */
		//taskID -> taskId
		JSON = JSON.replaceAll("taskID", "taskId");
		
		//Process
		retTask = gson.fromJson(JSON, Task.class);
		return retTask;
	}
	
}
