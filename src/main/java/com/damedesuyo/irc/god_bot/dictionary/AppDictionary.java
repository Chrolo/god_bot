package com.damedesuyo.irc.god_bot.dictionary;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class acts as a method by which the application can check for synonyms
 * for words, such as TS => Typesetter, etc
 * 
 * @author Chrolo
 *
 */
public class AppDictionary {

	// private Map<String, Dictionary> globalDictionary;
	public Map<String, Dictionary> globalDictionary;
	private boolean dictionaryLock;

	// --------------------------------------------------------------------------------
	// Get Shared instance:
	private static class SingletonHelper {
		private static final AppDictionary INSTANCE = new AppDictionary();
	}

	/**
	 * Use this method to acquire the shared instance of Userdatabase
	 * 
	 * @return the shared instance of UserDatabase
	 */
	public static AppDictionary getSharedInstance() {
		return SingletonHelper.INSTANCE;
	}

	// --------------------------------------------------------------------------------
	// Constructors:
	private AppDictionary() {
		// Load the 'config/Dictionary' into 'globalDictionary'
		this.globalDictionary = new HashMap<String, Dictionary>();
		this.refresh();
	}

	// ---------------------------------------------------------------------------------
	// Class refresh
	public void refresh() {
		try (Reader dictionary = new FileReader("config/dictionary.json")) {
			this.refresh(dictionary);
		} catch (FileNotFoundException e) {
			System.err.println("[AppDictionary] Errored in loading config/dictionary.json from disk.");
			System.err.println("Error Type: " + e.getClass());
			System.err.println("Error Message: " + e.getMessage());
			System.err.println("Strack Trace: " + e.getStackTrace());
		} catch (IOException e) {
			System.err.println("[AppDictionary] Errored in loading the dictionary from disk.");
			System.err.println("Error Type: " + e.getClass());
			System.err.println("Error Message: " + e.getMessage());
			System.err.println("Strack Trace: " + e.getStackTrace());
		}

	}

	public void refresh(Reader JsonFileReader) {
		if (!this.dictionaryLock) {
			try {
				Gson gson = new Gson();
				Type globalDictionaryType = new TypeToken<Map<String, Dictionary>>() {}.getType();
				this.globalDictionary = gson.fromJson(JsonFileReader, globalDictionaryType);
			} catch (Exception e) {
				System.err.println("[AppDictionary] Errored in loading the dictionary from disk.");
				System.err.println("Error Type: " + e.getClass());
				System.err.println("Error Message: " + e.getMessage());
				System.err.println("Strack Trace: " + e.getStackTrace());
			} finally {
				this.dictionaryLock = false;
			}
		} else {
			System.out.println("[AppDictionary] Refresh called, but one was already in progress.");
		}
	}
	
	public void refresh(String JsonString) {
		if (!this.dictionaryLock) {
			try {
				Gson gson = new Gson();
				Type globalDictionaryType = new TypeToken<Map<String, Dictionary>>() {}.getType();
				this.globalDictionary = gson.fromJson(JsonString, globalDictionaryType);
			} catch (Exception e) {
				System.err.println("[AppDictionary] Errored in loading the dictionary from disk.");
				System.err.println("Error Type: " + e.getClass());
				System.err.println("Error Message: " + e.getMessage());
				System.err.println("Strack Trace: " + e.getStackTrace());
			} finally {
				this.dictionaryLock = false;
			}
		} else {
			System.out.println("[AppDictionary] Refresh called, but one was already in progress.");
		}
	}

	// ----------------------------------------------------------------------------------
	// Dictionary Access:
	/**
	 * 
	 * @param str
	 *            The string which you want a substitution for
	 * @return the substituted string, or the original string if no substitution
	 *         was found.
	 */
	public String getSubstitutionFromAllDictionaries(String str) {
		// *
		for (Dictionary dict : this.globalDictionary.values()) {
			if (dict.dictionary.containsKey(str)) {
				return dict.dictionary.get(str);
			}
		}
		return str;
	}

	/**
	 * 
	 * @param str
	 *            The string which you want a substitution for
	 * @param context
	 *            The context of the substitution (which dictionary to look in).
	 * @return the substituted string, or the original string if no substitution
	 *         was found.
	 */
	public String getSubstitutionAgainstContext(String str, String context) {
		Dictionary dict;
		if ((dict = globalDictionary.get(context)) != null) {
			if (dict.dictionary.containsKey(str)) {
				return dict.dictionary.get(str);
			}
		}
		return str;
	}
}
