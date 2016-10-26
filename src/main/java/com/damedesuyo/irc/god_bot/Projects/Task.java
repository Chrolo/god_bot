package com.damedesuyo.irc.god_bot.Projects;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {
	public Task[] subtasks;
	public int taskId;
	public boolean done = false;
	
	//-------------------------------
	// Static Methods
	public static Task[] createTasksFromJson(String jsonString) throws Exception
	{
		/* //The Gson way...
		Gson gson = new Gson();
		Object temp = new Object();
		temp = gson.fromJson(jsonString, temp.getClass());

		System.out.println(temp);
		System.out.println("Class is: "+temp.getClass().getName());
		
		//Make sure it's an ArrayList:
		if(temp.getClass().getName() != "java.util.ArrayList")
		{
			throw new Exception("The Json didn't create an Array...");
		}
		ArrayList<Object> temp2 = (ArrayList<Object>) temp;
		for(Object obj : temp2)
		{
			System.out.println("Nested is: "+obj);
		}
		
		/*/ //My crazy custom way (recursion baby!)
		Pattern taskRegexBasic = Pattern.compile("\\{\\s*(.*)\\s*\\}");
		Matcher matchedJsonTask = taskRegexBasic.matcher(jsonString);
		if(!matchedJsonTask.matches())
		{
			throw new Exception("The Json didn't create an Array...");
		}
		else
		{
			System.out.println("Found task: |" + matchedJsonTask.group(1)+"|");
		}
		
		//*/
		
		System.out.println("[Task.createTasksFromJson()] not finished!");
		return null;
	}
	
	//-------------------------------
	// Constructors
	public Task(int taskId, Task[] subTasks)
	{
		this.taskId = taskId;
		this.subtasks = subTasks;
	}
	public Task(int taskId)
	{
		this(taskId,null); 
	}
	
	//-------------------------------
	// Public Methods
	public boolean areSubtasksComplete()
	{
		if(this.subtasks == null){ return true; }
		boolean flag = true;
		for(Task task : this.subtasks)
		{
			if(task.subtasks != null)
			{
				flag &= task.areSubtasksComplete();
			} else {
				flag &= task.done;
			}
		}
		return flag;
	}
	
	public Set<Integer> getUnfinishedTaskIds()
	{
		Set<Integer> taskIDs = new TreeSet<Integer>();
		if(this.subtasks == null && this.done == false)
		{
			taskIDs.add(this.taskId);
		}
		if(this.subtasks != null) 
		{
			if(!this.areSubtasksComplete())
			{
				taskIDs.add(this.taskId);
			}
			for(Task task : this.subtasks)
			{
				taskIDs.addAll(task.getUnfinishedTaskIds());
			}
		}
		
		return taskIDs;
	}
	
	//--------------------------
	// Overrides
	/**
	 * @return a JSON like representation of the Task.
	 */
	public String toString()
	{
		String res = "{ taskID: " + this.taskId ;
		if(this.subtasks == null)
		{
			res = res + ", done: " + this.done;
		}
		else
		{
			res = res + ", done: " + this.areSubtasksComplete();
			res = res + ", subTasks: [";
			boolean flag = false;
			for(Task task : this.subtasks)
			{
				if(flag){res += ", ";}
				flag = true;
				res = res + task.toString();
			}
			res += "]";
		}
		
		res = res +" }";
		return res;
	}
	
	
	
}
