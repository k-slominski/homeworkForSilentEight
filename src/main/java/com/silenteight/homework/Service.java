package com.silenteight.homework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

@org.springframework.stereotype.Service

public class Service
{
	static final String RESULT_WHEN_MALE = " MALE ";
	static final String RESULT_WHEN_FEMALE = " FEMALE ";
	static final String RESULT_WHEN_INCONCLUSIVE = " INCONCLUSIVE ";

	static private final String WOMEN_DATABASE_FILE_NAME = "women_utf8.txt";
	static private final String MEN_DATABASE_FILE_NAME = "men_utf8.txt";

	static private File womenNamesDatabase;
	static private File menNamesDatabase;

	Service() throws Exception
	{
		setDataSource();
	}

	static void setDataSource() throws URISyntaxException
	{
		var resourceURLmen = Service.class.getResource("/" + MEN_DATABASE_FILE_NAME);
		var resourceURLwomen = Service.class.getResource("/" + WOMEN_DATABASE_FILE_NAME);

		menNamesDatabase = Paths.get(resourceURLmen.toURI()).toFile();
		womenNamesDatabase = Paths.get(resourceURLwomen.toURI()).toFile();
	}

	static void changeDataSource(File newMenNamesDatabase, File newWomenNamesDatabase)
	{
		menNamesDatabase = newMenNamesDatabase;
		womenNamesDatabase = newWomenNamesDatabase;
	}

	static String guessGenderByFirstName(String name) throws Exception
	{
		if (name == null) return RESULT_WHEN_INCONCLUSIVE;

		if (name.indexOf(" ") > 0) name = name.substring(0, name.indexOf(" "));

		return searchingFirstName(name, RESULT_WHEN_MALE, menNamesDatabase).equals(RESULT_WHEN_MALE) ? RESULT_WHEN_MALE
				: searchingFirstName(name, RESULT_WHEN_FEMALE, womenNamesDatabase);

	}

	static String searchingFirstName(String name, String genderOfName, File databaseFile) throws Exception
	{
		String result = "";
		try {
			var reader = new BufferedReader(new FileReader(databaseFile));
			var line = reader.readLine().replaceFirst("^\uFEFF", "");
			result = name.equalsIgnoreCase(line) ? genderOfName : RESULT_WHEN_INCONCLUSIVE;
			while ((line = reader.readLine()) != null && !result.equalsIgnoreCase(genderOfName)) {
				if (name.equalsIgnoreCase(line)) {
					result = genderOfName;
					break;
				}
			}
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	static String guessGenderByRuleOfMajority(String name) throws Exception
	{
		if (name == null) return RESULT_WHEN_INCONCLUSIVE;

		var namesList = new LinkedList<>(Arrays.asList(name.split(" ")));

		var numberOfNames = namesList.size();

		var namesStillToFind = namesToFind(namesList, menNamesDatabase);

		var menCounter = numberOfNames - namesStillToFind.size();

		if (menCounter * 2 > numberOfNames) return RESULT_WHEN_MALE;

		var womenCounter = namesStillToFind.size() - namesToFind(namesStillToFind, womenNamesDatabase).size();

		return menCounter > womenCounter ? RESULT_WHEN_MALE : menCounter < womenCounter ? RESULT_WHEN_FEMALE :
				RESULT_WHEN_INCONCLUSIVE;

	}

	static List<String> namesToFind(List<String> names, File databaseFile) throws Exception
	{
		var iterator = names.listIterator();
		String temp;

		try (var reader = new BufferedReader(new FileReader(databaseFile))) {
			var line = reader.readLine().replaceFirst("^\uFEFF", "");
			while (iterator.hasNext()) {
				temp = iterator.next();
				if (temp.equalsIgnoreCase(line)) {
					names.remove(temp);
					iterator = names.listIterator();
				}
			}
			while ((line = reader.readLine()) != null) {
				iterator = names.listIterator();
				while (iterator.hasNext()) {
					temp = iterator.next();
					if (temp.equalsIgnoreCase(line)) {
						names.remove(temp);
						iterator = names.listIterator();
					}
				}
			}
		}
		return names;
	}

	Set<String> readDatabase(String gender) throws Exception
	{
		Set<String> result = new HashSet<>();
		if (gender == null) return result;

		switch (gender) {
			case "male": {
				result = readFile(menNamesDatabase);
				break;
			}
			case "female": {
				result = readFile(womenNamesDatabase);
				break;
			}
		}
		return result;
	}

	Set<String> readFile(File databaseFile) throws Exception
	{
		Set<String> setOfNames = new HashSet<>();
		try (var reader = new BufferedReader(new FileReader(databaseFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				setOfNames.add(line);
			}
			reader.close();
			return setOfNames;
		}
	}

	String guessGender(String name, String method) throws Exception
	{
		return method.equalsIgnoreCase("first-name") ? guessGenderByFirstName(name)
				: method.equalsIgnoreCase("majority-rule") ? guessGenderByRuleOfMajority(name)
				: "Unknown parameter";
	}


}
