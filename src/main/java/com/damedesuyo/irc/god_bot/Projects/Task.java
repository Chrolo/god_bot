package com.damedesuyo.irc.god_bot.Projects;

import java.util.AbstractSet;
import java.util.Set;
import java.util.TreeSet;

public class Task {
	public Task[] subtasks;
	public int taskId;
	public boolean done = false;
	
	
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
	
}
