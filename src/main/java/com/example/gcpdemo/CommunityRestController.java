package com.example.gcpdemo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommunityRestController {

	private final CommunityService service;

	public CommunityRestController(CommunityService service) {
		this.service = service;
	}

	@GetMapping("/api/communities")
	public List<Community> contacts() {
		return this.service.getCommunities();
	}

}
