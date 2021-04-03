package com.silenteight.homework;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.Test;
import com.silenteight.homework.Service;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServiceTests
{
	static final String MEN_MOCK_DATABASE_ADRESS = "/men_ut8_test.txt";
	static final String WOMEN_MOCK_DATABASE_ADRESS = "/women_ut8_test.txt";
	static final File MEN_MOCK_DATABASE = new File ("src/main/resources"+MEN_MOCK_DATABASE_ADRESS);
	static final File WOMEN_MOCK_DATABASE = new File ("src/main/resources"+WOMEN_MOCK_DATABASE_ADRESS);
	static final String MALE_NAME = "MALENAME";
	static final String FEMALE_NAME = "FEMALENAME";
	static final String INCONCLUSIVE_NAME = "INCONCLUSIVE";
	static final Service SERVICE = new Service();

	@BeforeAll
	public static void createMockDatabase() throws IOException
	{
		try {
			FileWriter menWriter = new FileWriter(MEN_MOCK_DATABASE);
			FileWriter womenWriter = new FileWriter(WOMEN_MOCK_DATABASE);
			for(int i=0;i<20;i++)
				{
					menWriter.write(MALE_NAME + i + System.lineSeparator());
					womenWriter.write(FEMALE_NAME + i + System.lineSeparator());
				}
			womenWriter.close();
			menWriter.close();
		}catch (IOException e)
		{

		}
		finally
			{

			}
		SERVICE.changeDataSource(MEN_MOCK_DATABASE_ADRESS,WOMEN_MOCK_DATABASE_ADRESS);
	}

	@Test
	public void whenFirstNameIsMale_GuessGenderByFirstName_GivesMale() throws IOException
	{
		assertEquals(" MALE ",SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(MALE_NAME,FEMALE_NAME,FEMALE_NAME)));
	}
	@Test
	public void whenFirstNameIsFemale_GuessGenderByFirstName_GivesFemale() throws IOException
	{
		assertEquals(" FEMALE ",SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(FEMALE_NAME,MALE_NAME,MALE_NAME)));
	}

	@Test
	public void whenFirstNameIsInconclusive_GuessGenderByFirstName_GivesInconclusive() throws IOException
	{
		assertEquals(" INCONCLUSIVE ",SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,MALE_NAME,FEMALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreMale_GuessGenderByRuleOfMajority_GivesMale() throws IOException
	{
		assertEquals(" MALE ",SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME,MALE_NAME,MALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreFemale_GuessGenderByRuleOfMajority_GivesFemale() throws IOException
	{
		assertEquals(" FEMALE ",SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME,MALE_NAME,FEMALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesGenderWithHasMoreOccurences() throws IOException
	{
		assertEquals(" MALE ",SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,INCONCLUSIVE_NAME,MALE_NAME)));
	}

	@Test
	public void whenAllNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesInconclusive() throws IOException
	{
		assertEquals(" INCONCLUSIVE ",SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,INCONCLUSIVE_NAME,INCONCLUSIVE_NAME)));
	}

	@Test
	public void whenOccurencesOfNamesAreEqual_GuessGenderByRuleOfMajority_GivesProbablyGenderByFirstName() throws IOException
	{
		assertEquals(" PROBABLY MALE ",SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(MALE_NAME,INCONCLUSIVE_NAME,FEMALE_NAME)));
	}

	@Test
	public void whenNamesAreDuplicated_GuessGenderByRuleOfMajority_GivesGenderWithMoreOccurences() throws IOException
	{
		assertEquals(" FEMALE ",SERVICE.guessGenderByRuleOfMajority(createNameFiveMemberedWithGivenGendersWithDuplicates(MALE_NAME,FEMALE_NAME,FEMALE_NAME)));
	}


	public String createNameThreeMemberedWithGivenGenders(String genderOfFirstName,String genderOfSecondName, String genderOfThirdName)
	{
		return genderOfFirstName+(int)(Math.random()*19)+" "+genderOfSecondName+(int)(Math.random()*19)+" "+genderOfThirdName+(int)(Math.random()*19);
	}

	public String createNameFiveMemberedWithGivenGendersWithDuplicates(String genderOfFirstName,String genderOfSecondName,String genderOfThirdName)
	{
		String firstName = genderOfFirstName+(int)(Math.random()*19);
		String secondName = genderOfSecondName+(int)(Math.random()*19);
		String thirdName = genderOfThirdName+(int)(Math.random()*19);
		return firstName+" "+thirdName+" "+secondName+" "+secondName +" "+firstName;
	}
	@AfterAll
	public static void deleteMockDatabase()
	{
		MEN_MOCK_DATABASE.delete();
		WOMEN_MOCK_DATABASE.delete();
	}
}
