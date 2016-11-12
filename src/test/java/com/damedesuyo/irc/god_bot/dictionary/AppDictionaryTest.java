package com.damedesuyo.irc.god_bot.dictionary;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class AppDictionaryTest {
	
	static AppDictionary testDict;
	static String testDictionaryString = "{\"test\":{\"dictionary\":{\"test1\":\"result\", \"test2\":\"result\"}},\"test2\":{\"dictionary\":{\"globalTest\":\"result2\",\"test1\":\"result2\"}}}";
	
	@BeforeClass
	 public static void setupTestClass(){
		testDict = AppDictionary.getSharedInstance();
		testDict.refresh(testDictionaryString);
	}
	
	@Test
	public void testGetSubstitutionFromAllDictionaries() {
		assertEquals("'globalTest' should get 'result2', as defined in the testJson.","result2",testDict.getSubstitutionFromAllDictionaries("globalTest"));
		
		assertEquals("'NA' doesn't exist, so should return itself","NA",testDict.getSubstitutionFromAllDictionaries("NA"));
	}

	@Test
	public void testGetSubstitutionAgainstContext() {
		assertEquals("'globalTest' in context 'test2' should get 'result2', as defined in the testJson.","result2",testDict.getSubstitutionAgainstContext("globalTest","test2"));
		
		assertEquals("'globalTest' in context 'test' should return 'globalTest', as it's not defined in that context.","globalTest",testDict.getSubstitutionAgainstContext("globalTest","test"));
		
		assertEquals("'NA' doesn't exist, so should return itself","NA",testDict.getSubstitutionFromAllDictionaries("NA"));
	}
}
