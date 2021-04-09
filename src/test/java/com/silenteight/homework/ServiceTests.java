package com.silenteight.homework;

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
	static final File MEN_MOCK_DATABASE = new File("src/test/resources/" + MEN_MOCK_DATABASE_NAME);
	static final File WOMEN_MOCK_DATABASE = new File("src/test/resources/" + WOMEN_MOCK_DATABASE_NAME);
	static final String MALE_NAME = "MALENAME";
	static final String FEMALE_NAME = "FEMALENAME";
	static final String INCONCLUSIVE_NAME = "INCONCLUSIVE";
	static final Service SERVICE = new Service();


	@BeforeAll
	public static void createMockDatabase()
	{
		try (FileWriter menWriter = new FileWriter(MEN_MOCK_DATABASE);
			 FileWriter womenWriter = new FileWriter(WOMEN_MOCK_DATABASE)) {
			for (int i = 0; i < 20; i++) {
				menWriter.write(MALE_NAME + i + System.lineSeparator());
				womenWriter.write(FEMALE_NAME + i + System.lineSeparator());
			}
			menWriter.close();
			womenWriter.close();

		} catch(IOException e) {
			e.getMessage();
		}
		setDatabase();
	}

	public static void setDatabase()
	{
		Service.changeDataSource(MEN_MOCK_DATABASE, WOMEN_MOCK_DATABASE);
	}

	@Test
	public void whenFirstNameIsMale_GuessGenderByFirstName_GivesMale()
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(MALE_NAME, FEMALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenFirstNameIsFemale_GuessGenderByFirstName_GivesFemale()
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						MALE_NAME)));
	}

	@Test
	public void whenFirstNameIsInconclusive_GuessGenderByFirstName_GivesInconclusive()
	{
		assertEquals(Service.RESULT_WHEN_INCONCLUSIVE,
				SERVICE.guessGenderByFirstName(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME, MALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenNamesAreNull_GuessGenderByFirstName_GivesInconclusive()
	{
		assertEquals("null value", SERVICE.guessGenderByFirstName(null));
	}

	@Test
	public void whenNameIsSingular_GuessGenderByFirstName_GivesGenderOfName()
	{
		assertEquals(Service.RESULT_WHEN_MALE, SERVICE.guessGenderByFirstName(MALE_NAME + (int) (Math.random() * 19)));
	}


	@Test
	public void whenMajorityNamesAreMale_GuessGenderByRuleOfMajority_GivesMale()
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						MALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreFemale_GuessGenderByRuleOfMajority_GivesFemale()
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(FEMALE_NAME, MALE_NAME,
						FEMALE_NAME)));
	}

	@Test
	public void whenMajorityNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesGenderWithHasMoreOccurences()
	{
		assertEquals(Service.RESULT_WHEN_MALE,
				SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,
						INCONCLUSIVE_NAME, MALE_NAME)));
	}

	@Test
	public void whenAllNamesAreInconclusive_GuessGenderByRuleOfMajority_GivesInconclusive()
	{
		assertEquals(Service.RESULT_WHEN_INCONCLUSIVE,
				SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(INCONCLUSIVE_NAME,
						INCONCLUSIVE_NAME, INCONCLUSIVE_NAME)));
	}

	@Test
	public void whenOccurencesOfNamesAreEqual_GuessGenderByRuleOfMajority_GivesInconclusive()
	{
		assertEquals(Service.RESULT_WHEN_INCONCLUSIVE,
				SERVICE.guessGenderByRuleOfMajority(createNameThreeMemberedWithGivenGenders(MALE_NAME,
						INCONCLUSIVE_NAME, FEMALE_NAME)));
	}

	@Test
	public void whenNamesAreNull_GuessGenderByRuleOfMajority_GivesInconclusive()
	{
		assertEquals("null value", SERVICE.guessGenderByRuleOfMajority(null));
	}

	@Test
	public void whenNameIsSingular_GuessGenderByRuleOfMajority_GivesGenderOfName()

	{
		assertEquals(Service.RESULT_WHEN_MALE,
				SERVICE.guessGenderByRuleOfMajority(MALE_NAME + (int) (Math.random() * 19)));
	}

	@Test
	public void whenNamesAreDuplicated_GuessGenderByRuleOfMajority_GivesGenderWithMoreOccurences()
	{
		assertEquals(Service.RESULT_WHEN_FEMALE,
				SERVICE.guessGenderByRuleOfMajority(
						createNameThreeMemberedWithGivenGendersWithDuplicatedSecondName(MALE_NAME, FEMALE_NAME)));
	}

	public String createNameThreeMemberedWithGivenGenders(String genderOfFirstName, String genderOfSecondName,
														  String genderOfThirdName)
	{
		return genderOfFirstName + (int) (Math.random() * 19) + " " + genderOfSecondName + (int) (Math.random() * 19)
				+ " " + genderOfThirdName + (int) (Math.random() * 19);
	}

	public String createNameThreeMemberedWithGivenGendersWithDuplicatedSecondName(String genderOfFirstName,
																				  String genderOfSecondName)
	{
		String firstName = genderOfFirstName + (int) (Math.random() * 19);
		String secondName = genderOfSecondName + (int) (Math.random() * 19);

		return firstName + " " + secondName + " " + secondName;
	}
}
