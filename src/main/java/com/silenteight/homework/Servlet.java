package com.silenteight.homework;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
class Servlet
{
	private final Service service;

	Servlet(Service service)
	{
		this.service = service;
	}

	@ResponseBody
	@RequestMapping("/write-tokens")
	ResponseEntity<Set<String>> writeContentOfTheFile(@RequestParam(value = "gender") String gender) throws Exception
	{
		return ResponseEntity.ok(service.readDatabase(gender));
	}

	@ResponseBody
	@RequestMapping("/guessing")
	ResponseEntity<String> guessingGender(@RequestParam(value = "name") String name,
										  @RequestParam(value = "method") String method) throws Exception
	{
		return ResponseEntity.ok("Gender: " + service.guessGender(name, method));
	}


}
