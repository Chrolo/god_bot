package com.damedesuyo.irc.god_bot.projects;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.damedesuyo.irc.god_bot.Projects.Task;

public class TaskTest {

	@Test
	public void test_areSubtasksComplete() {
		Task task1 = new Task(1);
		Task task2 = new Task(2);
		Task task3 = new Task(3);
		Task[] taskArray = { task1, task2, task3 };
		Task taskAll = new Task(4, taskArray);
		
		assertFalse("At default, tasks are not done.",taskAll.areSubtasksComplete());
		
		taskAll.subtasks[0].done = true;
		taskAll.subtasks[1].done = true;
		
		assertFalse("With just one outstanding, stil false.",taskAll.areSubtasksComplete());
		
		taskAll.subtasks[2].done = true;
		assertTrue("Once all sub tasks are complete, task is complete",taskAll.areSubtasksComplete());
	}
	
	@Test
	public void test_areSubtasksComplete_nested() {
		Task task1 = new Task(1);
		Task task2 = new Task(2);
		Task[] taskArray = { task1, task2 };
		Task task3 = new Task(3);
		Task task4 = new Task(4, taskArray);
		Task[] taskArray2 = { task3, task4 };
		Task taskAll = new Task(5, taskArray2);
		
		assertFalse("At default, tasks are not done.",taskAll.areSubtasksComplete());
		
		taskAll.subtasks[0].done = true;
		taskAll.subtasks[1].subtasks[0].done = true;
		
		assertFalse("With just one nest subtask outstanding, stil false.",taskAll.areSubtasksComplete());
		
		taskAll.subtasks[1].subtasks[1].done = true;
		assertTrue("Once all sub tasks are complete, task is complete",taskAll.areSubtasksComplete());
	}
	
	@Test
	public void test_getUnfinishedTaskIds_nested() {
		Task task1 = new Task(1);
		Task task2 = new Task(2);
		Task[] taskArray = { task1, task2 };
		Task task3 = new Task(3);
		Task task4 = new Task(4, taskArray);
		Task[] taskArray2 = { task3, task4 };
		Task taskAll = new Task(5, taskArray2);
		
		Set<Integer> expectedTaskIDs = new TreeSet<Integer>();
		expectedTaskIDs.add(1);
		expectedTaskIDs.add(2);
		expectedTaskIDs.add(3);
		expectedTaskIDs.add(4);
		expectedTaskIDs.add(5);
		assertEquals("At default, all tasks are not done.",expectedTaskIDs, taskAll.getUnfinishedTaskIds());
		
		taskAll.subtasks[1].subtasks[0].done = true;	//Task 1
		taskAll.subtasks[0].done = true;	//Task 3
		expectedTaskIDs.remove(1);
		expectedTaskIDs.remove(3);
		assertEquals("After Task 1 and 3 are done. Task 2, 4 and 5 are still outstanding.", expectedTaskIDs, taskAll.getUnfinishedTaskIds());
		
		taskAll.subtasks[1].subtasks[1].done = true;	//Task 2
		assertEquals("Once task 2 is complete, 4 is also complete, as is 5",0, taskAll.getUnfinishedTaskIds().size());
	}
	
	
	@Test
	public void test_createTasksFromJson() {
		String testStr = "{taskID:2, subTasks:[{taskID:3, done:false}, {taskID:4, done: false}]}";
		String testStr2 = "{    taskID:2, subTasks:[{taskID:3, done:false}, {taskID:4, done: false}]    }";
		String testStr3 = "{taskID:1, done:false}";
		try {
			Task.createTasksFromJson(testStr);
			Task.createTasksFromJson(testStr2);
			Task.createTasksFromJson(testStr3);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
