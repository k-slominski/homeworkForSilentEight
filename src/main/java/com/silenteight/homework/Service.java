package com.silenteight.homework;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@org.springframework.stereotype.Service
public class Service
{
	String womenDatabaseFileName= "/women_utf8.txt";
	String menDatabaseFileName = "/men_utf8.txt";
	//File womenFileDatabase = new File(womenDatabaseFileName);
	//File menFileDatabase = new File(menDatabaseFileName);
	//InputStream in_men = getClass().getResourceAsStream(menDatabaseFileName);
//	InputStream in_women = getClass().getResourceAsStream(womenDatabaseFileName);

	void changeDataSource(String newMenFileDatabaseAdress,String newWomenFileDatabaseAdress)
	{
		menDatabaseFileName=newMenFileDatabaseAdress;
		womenDatabaseFileName=newWomenFileDatabaseAdress;
		//in_men = getClass().getResourceAsStream(newMenFileDatabaseAdress);
		//in_women = getClass().getResourceAsStream(newWomenFileDatabaseAdress);
	}
	Set<String> readFile() throws IOException
	{
		Set <String> result = new HashSet<>();
		//InputStream in_men = new FileInputStream(menFileDatabase);

		InputStream in_men = getClass().getResourceAsStream(menDatabaseFileName);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in_men,StandardCharsets.UTF_8))){
			String line;
			while((line = reader.readLine()) != null)
			{
				result.add(line);
			}
		}
		return result;

	}

	public String guessGenderByFirstName(String name)  throws IOException
	{
		String[] namesArray = name.split(" ");
		name = namesArray[0];
		String result ="";
//		InputStream in_men = new FileInputStream(menFileDatabase);
//		InputStream in_women = new FileInputStream(womenFileDatabase);

		InputStream in_men = getClass().getResourceAsStream(menDatabaseFileName);
		InputStream in_women = getClass().getResourceAsStream(womenDatabaseFileName);

		if (in_men == null) System.out.println("null");


		try (BufferedReader readerMen = new BufferedReader(new InputStreamReader(in_men, StandardCharsets.UTF_8));
			 BufferedReader readerWomen = new BufferedReader(new InputStreamReader(in_women, StandardCharsets.UTF_8)))
		{
			String menLine = readerMen.readLine().replaceFirst("^\uFEFF", "");
			String womenLine = readerWomen.readLine().replaceFirst("^\uFEFF", "");
			result = name.equalsIgnoreCase(menLine) ? " MALE " : name.equalsIgnoreCase(womenLine) ? " FEMALE " : " INCONCLUSIVE ";

			while ((menLine = readerMen.readLine()) != null) {
				if (name.equalsIgnoreCase(menLine)) {
					result = " MALE ";
					break;
				}
			}
			while ((womenLine = readerWomen.readLine()) != null) {
				if (name.equalsIgnoreCase(womenLine)) {
					result = " FEMALE ";
					break;
				}
			}

		}catch(NullPointerException nle)
		{
			nle.printStackTrace();
		}
		return result;
	}

	String guessGenderByRuleOfMajority(String name)  throws IOException
	{
		List<String> namesList = new LinkedList<>(Arrays.asList(name.split(" ")));
		ListIterator<String> iterator = namesList.listIterator();
		int menCounter = 0;
		int womenCounter = 0;
		String temp = "";
		String inCaseOfDraw = " INCONCLUSIVE ";
		String firstName = namesList.get(0);
		InputStream in_men = getClass().getResourceAsStream(menDatabaseFileName);
		InputStream in_women = getClass().getResourceAsStream(womenDatabaseFileName);
		try (BufferedReader readerMen = new BufferedReader(new InputStreamReader(in_men, StandardCharsets.UTF_8));
			 BufferedReader readerWomen = new BufferedReader(new InputStreamReader(in_women, StandardCharsets.UTF_8))) {
			String menLine = readerMen.readLine().replaceFirst("^\uFEFF", "");
			String womenLine = readerWomen.readLine().replaceFirst("^\uFEFF", "");

			while(iterator.hasNext()) {
				temp=iterator.next();
				if (temp.equalsIgnoreCase(menLine)) {
					inCaseOfDraw = firstName.equalsIgnoreCase(temp)?" PROBABLY MALE ": inCaseOfDraw;
					menCounter++;
					namesList.remove(temp);
					iterator = namesList.listIterator();
				}
				else {
					if (temp.equalsIgnoreCase(womenLine)) {
						inCaseOfDraw = firstName.equalsIgnoreCase(temp)?" PROBABLY FEMALE ": inCaseOfDraw;
						womenCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
					}
				}
			}
			System.out.println(" first " + menCounter +" "+ womenCounter + " ");


			while ((menLine = readerMen.readLine()) != null && namesList != null) {
				iterator = namesList.listIterator();
				while(iterator.hasNext()) {
					temp = iterator.next();
					if (temp.equalsIgnoreCase(menLine)) {
						menCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
						inCaseOfDraw = firstName.equalsIgnoreCase(temp)?" PROBABLY MALE ": inCaseOfDraw;

					}
				}
			}

			while ((womenLine = readerWomen.readLine()) != null && namesList != null) {
				iterator = namesList.listIterator();
				while(iterator.hasNext()) {
					temp=iterator.next();
					if (temp.equalsIgnoreCase(womenLine)) {
						womenCounter++;
						namesList.remove(temp);
						iterator = namesList.listIterator();
						inCaseOfDraw = firstName.equalsIgnoreCase(temp)?" PROBABLY FEMALE ": inCaseOfDraw;
					}
				}
			}
		}
		System.out.println(" last "+ menCounter +" "+ womenCounter);

		return menCounter > womenCounter ? " MALE " : menCounter < womenCounter ? " FEMALE " : inCaseOfDraw;

	}
}
