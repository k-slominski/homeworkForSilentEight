package com.silenteight.homework;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class Servlet
{
	private final Service service;

	Servlet(Service service)
	{
		this.service = service;
	}

	@ResponseBody
	@RequestMapping("/1")
	public Set<String> writeContentOfTheFile() throws IOException, Exception
	{

		return service.readFile();
	}

	@RequestMapping("/2")
	@ResponseBody
	public String guessingGenderByFirstName(@RequestParam(value = "name") String name) throws Exception
	{
		return "Gender: " + service.guessGenderByFirstName(name);

	}

	@RequestMapping("/3")
	@ResponseBody
	public String guessingGenderByRuleOfMajority(@RequestParam(value = "name") String name) throws Exception
	{
		return "Gender: " + service.guessGenderByRuleOfMajority(name);

	}

}
