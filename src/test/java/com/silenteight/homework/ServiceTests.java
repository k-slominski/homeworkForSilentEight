package com.silenteight.homework;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServiceTests
{
	static final String MEN_MOCK_DATABASE_NAME = "men_ut8_test.txt";
	static final String WOMEN_MOCK_DATABASE_NAME = "women_ut8_test.txt";
	static final File MEN_MOCK_DATABASE = new File("src/main/resources/" + MEN_MOCK_DATABASE_NAME);
	static final File WOMEN_MOCK_DATABASE = new File("src/main/resources/" + WOMEN_MOCK_DATABASE_NAME);
	static final String MALE_NAME = "MALENAME";
	static final String FEMALE_NAME = "FEMALENAME";
	static final String INCONCLUSIVE_NAME = "INCONCLUSIVE";


	@BeforeAll
	public static void createMockDatabase() throws IOException, Exception
	{
		FileWriter menWriter = new FileWriter(MEN_MOCK_DATABASE);
		FileWriter womenWriter = new FileWriter(WOMEN_MOCK_DATABASE);

		try {

			for (int i = 0; i < 20; i++) {
				menWriter.write(MALE_NAME + i + System.lineSeparator());
				womenWriter.write(FEMALE_NAME + i + System.lineSeparator());
			}

		} catch(IOException e) {

		} finally {
			womenWriter.close();
			menWriter.close();
		}

		setDatabase();
	}

	public static void setDatabase() throws Exception
	{
		Service.changeDataSource(MEN_MOCK_DATABASE, WOMEN_MOCK_DATABASE);
	}

	@AfterAll
	public static void deleteMockDatabase() throws Exception
	{

		MEN_MOCK_DATABASE.delete();
		WOMEN_MOCK_DATABASE.delete();
	}

	@Test
	public void whenFirstNameIsMale_GuessGenderByFirstName_GivesMale() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				Service.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(MALE_NAME, FEMALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenFirstNameIsFemale_GuessGenderByFirstName_GivesFemale() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				Service.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						MALE_NAME)));
	}

	@Test
	public void whenFirstNameIsInconclusive_GuessGenderByFirstName_GivesInconclusive() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_INCONCLUSIVE,
				Service.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME, MALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreMale_GuessGenderByRuleOfMajority_GivesMale() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				Service.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						MALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreFemale_GuessGenderByRuleOfMajority_GivesFemale() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				Service.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesGenderWithHasMoreOccurences()
			throws Exception
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				Service.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,
						INCONCLUSIVE_NAME, MALE_NAME)));
	}

	@Test
	public void whenAllNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesInconclusive() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_INCONCLUSIVE,
				Service.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,
						INCONCLUSIVE_NAME, INCONCLUSIVE_NAME)));
	}

	@Test
	public void whenOccurencesOfNamesAreEqual_GuessGenderByRuleOfMajority_GivesProbablyGenderByFirstName()
			throws Exception
	{
		assertEquals(Service.RESULT_WHEN_PROBABLY_MALE,
				Service.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(MALE_NAME,
						INCONCLUSIVE_NAME, FEMALE_NAME)));
	}

	@Test
	public void whenNamesAreDuplicated_GuessGenderByRuleOfMajority_GivesGenderWithMoreOccurences() throws Exception
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				Service.guessGenderByRuleOfMajority(createNameFiveMemberedWithGivenGendersWithDuplicates(MALE_NAME,
						FEMALE_NAME, FEMALE_NAME)));
	}

	public String createNameThreeMemberedWithGivenGenders(String genderOfFirstName, String genderOfSecondName,
														  String genderOfThirdName)
	{
		return genderOfFirstName + (int) (Math.random() * 19) + " " + genderOfSecondName + (int) (Math.random() * 19) + " " + genderOfThirdName + (int) (Math.random() * 19);
	}

	public String createNameFiveMemberedWithGivenGendersWithDuplicates(String genderOfFirstName,
																	   String genderOfSecondName,
																	   String genderOfThirdName)
	{
		String firstName = genderOfFirstName + (int) (Math.random() * 19);
		String secondName = genderOfSecondName + (int) (Math.random() * 19);
		String thirdName = genderOfThirdName + (int) (Math.random() * 19);
		return firstName + " " + thirdName + " " + secondName + " " + secondName + " " + firstName;
	}
}
