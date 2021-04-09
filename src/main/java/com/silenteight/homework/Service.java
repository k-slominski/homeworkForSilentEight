package com.silenteight.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@org.springframework.stereotype.Service

public class Service
{
	static final String RESULT_WHEN_MALE = " MALE ";
	static final String RESULT_WHEN_FEMALE = " FEMALE ";
	static final String RESULT_WHEN_INCONCLUSIVE = " INCONCLUSIVE ";

	static final Logger log = LoggerFactory.getLogger(Service.class);

	static final private String WOMEN_DATABASE_FILE_NAME = "women_utf8.txt";
	static final private String MEN_DATABASE_FILE_NAME = "men_utf8.txt";

	static private File womenNamesDatabase;
	static private File menNamesDatabase;
	static private Integer controlVariable = 0;
	static private InputStream womenStream;
	static private InputStream menStream;

	static void changeDataSource(File newMenNamesDatabase, File newWomenNamesDatabase)
	{
		menNamesDatabase = newMenNamesDatabase;
		womenNamesDatabase = newWomenNamesDatabase;
		try {
			menStream = new FileInputStream(newMenNamesDatabase);
			womenStream = new FileInputStream(newWomenNamesDatabase);
			controlVariable = 1;
		} catch(IOException e) {
			log.error(e.getMessage());
		}
	}

	static String searchingFirstName(String name, String genderOfName, InputStream inputStream)
	{
		String result = "";

		try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			var line = reader.readLine().replaceFirst("^\uFEFF", "");
			result = name.equalsIgnoreCase(line) ? genderOfName : RESULT_WHEN_INCONCLUSIVE;
			while ((line = reader.readLine()) != null && !result.equalsIgnoreCase(genderOfName)) {
				if (name.equalsIgnoreCase(line)) {
					result = genderOfName;
					break;
				}
			}
		} catch(IOException e) {
			log.error(e.getMessage());
		}

		return result;
	}

	static List<String> namesToFind(List<String> names, InputStream inputStream)
	{
		var iterator = names.listIterator();
		String temp;

		try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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
		} catch(IOException e) {
			log.error(e.getMessage());
		}

		return names;
	}

	void setDataSource()
	{
		if (controlVariable == 0) {
			menStream = getClass().getClassLoader().getResourceAsStream(MEN_DATABASE_FILE_NAME);
			womenStream = getClass().getClassLoader().getResourceAsStream(WOMEN_DATABASE_FILE_NAME);
		} else
			changeDataSource(menNamesDatabase, womenNamesDatabase);
	}

	String guessGender(String name, String method)
	{
		return method.equalsIgnoreCase("first-name") ? guessGenderByFirstName(name)
				: method.equalsIgnoreCase("majority-rule") ? guessGenderByRuleOfMajority(name)
				: "Unknown parameter";
	}

	String guessGenderByFirstName(String name)
	{
		if (name == null) return "null value";

		if (name.indexOf(" ") > 0) name = name.substring(0, name.indexOf(" "));

		setDataSource();

		return searchingFirstName(name, RESULT_WHEN_MALE, menStream).equals(RESULT_WHEN_MALE) ? RESULT_WHEN_MALE
				: searchingFirstName(name, RESULT_WHEN_FEMALE, womenStream);

	}

	String guessGenderByRuleOfMajority(String name)
	{
		if (name == null) return "null value";

		var namesList = new LinkedList<>(Arrays.asList(name.split(" ")));

		var numberOfNames = namesList.size();

		setDataSource();

		var namesStillToFind = namesToFind(namesList, menStream);

		var menCounter = numberOfNames - namesStillToFind.size();

		if (menCounter * 2 > numberOfNames) return RESULT_WHEN_MALE;

		var womenCounter = namesStillToFind.size() - namesToFind(namesStillToFind, womenStream).size();

		return menCounter > womenCounter ? RESULT_WHEN_MALE : menCounter < womenCounter ? RESULT_WHEN_FEMALE :
				RESULT_WHEN_INCONCLUSIVE;

	}

	Set<String> readDatabase(String gender)
	{
		Set<String> result = new HashSet<>();
		if (gender == null) return result;

		setDataSource();

		switch (gender) {
			case "male": {
				result = readFile(menStream);
				break;
			}
			case "female": {
				result = readFile(womenStream);
				break;
			}
		}
		return result;
	}

	Set<String> readFile(InputStream inputStream)
	{
		Set<String> setOfNames = new HashSet<>();

		try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				setOfNames.add(line);
			}
		} catch(IOException e) {
			log.error(e.getMessage());
		}
		return setOfNames;

	}

}
