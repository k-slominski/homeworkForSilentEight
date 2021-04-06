package com.silenteight.homework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

@org.springframework.stereotype.Service

public class Service
{
	static final String RESULT_WHEN_MALE = " MALE ";
	static final String RESULT_WHEN_FEMALE = " FEMALE ";
	static final String RESULT_WHEN_INCONCLUSIVE = " INCONCLUSIVE ";
	static final String RESULT_WHEN_PROBABLY_MALE = " PROBABLY MALE ";
	static final String RESULT_WHEN_PROBABLY_FEMALE = " PROBABLY FEMALE ";

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
		URL resourceURLmen = Service.class.getResource("/" + MEN_DATABASE_FILE_NAME);
		URL resourceURLwomen = Service.class.getResource("/" + WOMEN_DATABASE_FILE_NAME);

		menNamesDatabase = Paths.get(resourceURLmen.toURI()).toFile();
		womenNamesDatabase = Paths.get(resourceURLwomen.toURI()).toFile();
	}

	static void changeDataSource(File newMenNamesDatabase, File newWomenNamesDatabase)
	{
		menNamesDatabase = newMenNamesDatabase;
		womenNamesDatabase = newWomenNamesDatabase;
	}

	static public String guessGenderByFirstName(String name) throws Exception
	{
		name = name.substring(0, name.indexOf(" "));
		String result = "";

		try (BufferedReader readerMen = new BufferedReader(new FileReader(menNamesDatabase));
			 BufferedReader readerWomen = new BufferedReader(new FileReader(womenNamesDatabase))) {

			String menLine = readerMen.readLine().replaceFirst("^\uFEFF", "");
			String womenLine = readerWomen.readLine().replaceFirst("^\uFEFF", "");

			result = name.equalsIgnoreCase(menLine) ? RESULT_WHEN_MALE : name.equalsIgnoreCase(womenLine) ?
					RESULT_WHEN_FEMALE : RESULT_WHEN_INCONCLUSIVE;

			while ((menLine = readerMen.readLine()) != null && !result.equalsIgnoreCase(RESULT_WHEN_MALE)) {
				if (name.equalsIgnoreCase(menLine)) {
					result = RESULT_WHEN_MALE;
					break;
				}
			}
			while ((womenLine = readerWomen.readLine()) != null && !result.equalsIgnoreCase(RESULT_WHEN_FEMALE)) {
				if (name.equalsIgnoreCase(womenLine)) {
					result = RESULT_WHEN_FEMALE;
					break;
				}
			}

		} catch(NullPointerException nle) {
			nle.printStackTrace();
		}
		return result;
	}

	static String guessGenderByRuleOfMajority(String name) throws Exception
	{
		List<String> namesList = new LinkedList<>(Arrays.asList(name.split(" ")));
		ListIterator<String> iterator = namesList.listIterator();

		String temp;
		String inCaseOfDraw = RESULT_WHEN_INCONCLUSIVE;
		String firstName = namesList.get(0);

		int menCounter = 0;
		int womenCounter = 0;

		try (BufferedReader readerMen = new BufferedReader(new FileReader(menNamesDatabase));
			 BufferedReader readerWomen = new BufferedReader(new FileReader(womenNamesDatabase))) {

			String menLine = readerMen.readLine().replaceFirst("^\uFEFF", "");
			String womenLine = readerWomen.readLine().replaceFirst("^\uFEFF", "");

			while (iterator.hasNext()) {
				temp = iterator.next();
				if (temp.equalsIgnoreCase(menLine)) {
					inCaseOfDraw = firstName.equalsIgnoreCase(temp) ? RESULT_WHEN_PROBABLY_MALE : inCaseOfDraw;
					menCounter++;
					namesList.remove(temp);
					iterator = namesList.listIterator();
				} else {
					if (temp.equalsIgnoreCase(womenLine)) {
						inCaseOfDraw = firstName.equalsIgnoreCase(temp) ? RESULT_WHEN_FEMALE : inCaseOfDraw;
						womenCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
					}
				}
			}
			while ((menLine = readerMen.readLine()) != null) {
				iterator = namesList.listIterator();
				while (iterator.hasNext()) {
					temp = iterator.next();
					if (temp.equalsIgnoreCase(menLine)) {
						menCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
						inCaseOfDraw = firstName.equalsIgnoreCase(temp) ? RESULT_WHEN_PROBABLY_MALE : inCaseOfDraw;
					}
				}
			}
			while ((womenLine = readerWomen.readLine()) != null) {
				iterator = namesList.listIterator();
				while (iterator.hasNext()) {
					temp = iterator.next();
					if (temp.equalsIgnoreCase(womenLine)) {
						womenCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
						inCaseOfDraw = firstName.equalsIgnoreCase(temp) ? RESULT_WHEN_PROBABLY_FEMALE : inCaseOfDraw;
					}
				}
			}
		}

		return menCounter > womenCounter ? RESULT_WHEN_MALE : menCounter < womenCounter ? RESULT_WHEN_FEMALE :
				inCaseOfDraw;
	}

	Set<String> readFile() throws Exception
	{
		Set<String> result = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(menNamesDatabase))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		}
		return result;

	}
}
