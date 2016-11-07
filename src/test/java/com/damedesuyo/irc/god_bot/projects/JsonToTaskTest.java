package com.damedesuyo.irc.god_bot.projects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.damedesuyo.irc.god_bot.Projects.JsonToTask;

public class JsonToTaskTest {

	@Test
	public void testFindStringEndIndex() {
		
		String test1 = "test\" end of string";
		int pos1 = 4;
		assertEquals(pos1, JsonToTask.findStringEndIndex(test1));
		
		String test2 = "12345\\\"890\"2";
		int pos2 = 10;
		assertEquals(pos2, JsonToTask.findStringEndIndex(test2));
		
	}
	
	@Test
	public void testfindArrayEndIndex()
	{
		String test = "1,3]";
		int pos = 3;
		String msg = "basic array";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findArrayEndIndex(test));
		
		test = "1,\"456\",9], extra text";
		pos = 9;
		msg = "Array with a string";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findArrayEndIndex(test));
		
		test = "1,\"4]6\",9], misc stuff";
		pos = 9;
		msg = "String with ']' inside";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findArrayEndIndex(test));
		
		
		test = "1,[4,6,8],1], misc stuff";
		pos = 11;
		msg = "nested array";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findArrayEndIndex(test));
		
		test = "1,{4,6,8},1], misc stuff";
		pos = 11;
		msg = "nested object";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findArrayEndIndex(test));
	}
	
	@Test
	public void testfindObjectEndIndex()
	{
		String test = "1,3}";
		int pos = 3;
		String msg = "basic object";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findObjectEndIndex(test));
		
		test = "1,\"456\",9}, extra text";
		pos = 9;
		msg = "Object with a string";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findObjectEndIndex(test));
		
		test = "1,\"4}6\",9}, misc stuff";
		pos = 9;
		msg = "String with '}' inside";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findObjectEndIndex(test));
		
		
		test = "1,{4,6,8},1}, misc stuff";
		pos = 11;
		msg = "nested object";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findObjectEndIndex(test));
		
		test = "1,[4,6,8],1}, misc stuff";
		pos = 11;
		msg = "nested array";
		assertEquals("Failed Test: "+msg, pos, JsonToTask.findObjectEndIndex(test));
	}

}
