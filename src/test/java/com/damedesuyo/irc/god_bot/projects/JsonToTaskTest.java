package com.damedesuyo.irc.god_bot.Projects;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonToTaskTest {

	@Test
	public void testConvertJsonToTasks() {
		
		//check taskID gets converted to taskId
		
		String testStr = "{\"taskID\":1,\"done\":false}";
		Task testTask = new Task(1);
		String testDesc = "taskID needs to be converted to taskId";
		
		assertEquals("Failed"+testDesc,testTask,JsonToTask.convertJsonToTasks(testStr));
		
		
		//Data Checks
		
		testStr = "{\"taskId\":1,\"done\":false}";
		testTask = new Task(1);
		testDesc = "Simple Task";
		
		assertEquals("Failed"+testDesc,testTask,JsonToTask.convertJsonToTasks(testStr));
		
		
		testStr = "{\"taskId\":1,\"subTasks\":[{\"taskId\":2,\"done\":false},{\"taskId\":3,\"done\":false}]}";
		Task two = new Task(2);
		Task three = new Task(3);
		Task[] array = {two, three};
		testTask = new Task(1,array);
		testDesc = "Task with Subtasks";
		assertEquals("Failed"+testDesc,testTask,JsonToTask.convertJsonToTasks(testStr));
	}


}
