package com.damedesuyo.irc.god_bot.projects;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A rough data structure:
 * {
 * 		//Essentials:
 * 		taskId: int
 * 		subTasks: Task[],
 * 		done: boolean,
 * 
 * 		//Optionals
 * 		staffId: int,
 * }
 * @author Chrolo
 *
 */

public class Task {
	
	//essentials
	private int taskId;
	public Task[] subTasks;
	private boolean done = false;
	
	//optionals
	private int staffId;
	
	
	//-------------------------------
	// Constructors
	// Note: This will usually be set by GSON.
	
	public Task(int taskId, Task[] subTasks, int staffId)
	{
		this.taskId = taskId;
		this.subTasks = subTasks;
		this.staffId = staffId;
	}
	
	public Task(int taskId, Task[] subTasks)
	{
		this.taskId = taskId;
		this.subTasks = subTasks;
	}
	
	public Task(int taskId, boolean done)
	{
		this.taskId = taskId;
		this.done = done;
	}
	
	public Task(int taskId, int staffId)
	{
		this(taskId, false);
		this.staffId = staffId;
	}
	
	public Task(int taskId)
	{
		this(taskId, false); 
	}
	
	//-------------------------------
	// Public Methods
	public boolean areSubtasksComplete()
	{
		if(this.subTasks == null){ return true; }
		boolean flag = true;
		for(Task task : this.subTasks)
		{
			if(task.subTasks != null)
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
		if(this.subTasks == null && this.done == false)
		{
			taskIDs.add(this.taskId);
		}
		if(this.subTasks != null) 
		{
			if(!this.areSubtasksComplete())
			{
				taskIDs.add(this.taskId);
			}
			for(Task task : this.subTasks)
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
		String res = "{ taskId: " + this.taskId ;
		if(this.subTasks == null)
		{
			res = res + ", done: " + this.done;
		}
		else
		{
			res = res + ", done: " + this.areSubtasksComplete();
			res = res + ", subTasks: [";
			boolean flag = false;
			for(Task task : this.subTasks)
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
	
	public boolean equals(Object obj)
	{
		System.out.println("Comparing:\n"+this+"\nto\n"+obj);
		
		//Check it's the proper class:
		if(!obj.getClass().isAssignableFrom(Task.class)){return false;}
			
		//Convert to class and test:
		Task otherTask = (Task)obj;
		if(this.done != otherTask.done){return false;}
		if(this.taskId != otherTask.taskId){return false;}
		
		boolean flag = true;
		if(subTasks != null)
		{
			System.out.println("\tThis sub tasks: "+this.subTasks+"\n\tother.subTasks: "+otherTask.subTasks);
			System.out.println("\tThis sub tasks.length: "+this.subTasks.length+"\n\tother.subTasks.length: "+otherTask.subTasks.length);
			
			if(otherTask.subTasks == null)
			{
				return false;
			}
			
			if(otherTask.subTasks.length != this.subTasks.length)
			{
				return false;
			}
			
			for(int i=0; i < subTasks.length; i++)
			{
				System.out.println("Subtask comparison: "+this.subTasks[i] +" compared to " + otherTask.subTasks[i]);
				flag &= this.subTasks[i].equals(otherTask.subTasks[i]);
			}
		}
		
		return flag;
		
	}

	//------------------------------------
	// Generated Getters/Setters
	public int getTaskId() {
		return this.taskId;
	}

	public boolean isDone() {
		if(this.subTasks == null)
		{
			return done;
		}
		else
		{
			return this.areSubtasksComplete();
		}
	}

	public void setDone(boolean done) {
		if(this.subTasks != null)
		{
			System.err.println("[Task] Attempted to set 'done' on task with subTasks. TaskId:"+this.taskId);
		}
		else
		{
			this.done = done;
		}
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	
}
