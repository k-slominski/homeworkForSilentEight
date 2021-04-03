package com.silenteight.homework;

import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class Servlet
{
	private final Service service;

	Servlet (Service service)
	{
		this.service=service;
	}

	@ResponseBody
	@RequestMapping("/1")
	public Set<String> writeContentOfTheFile() throws IOException
	{

		return  service.readFile();
	}

	@RequestMapping("/2")
	@ResponseBody
	public String guessingGenderByFirstName (@RequestParam (value = "name") String name) throws IOException
	{
		return "Gender: "+ service.guessGenderByFirstName(name);

	}

	@RequestMapping("/3")
	@ResponseBody
	public String guessingGenderByRuleOfMajority (@RequestParam (value = "name") String name) throws IOException
	{
		return "Gender: "+ service.guessGenderByRuleOfMajority(name);

	}

}
