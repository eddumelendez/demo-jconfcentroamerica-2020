package com.example.gcpdemo;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CommunityService {

	private final CommunityRepository repository;

	public CommunityService(CommunityRepository repository) {
		this.repository = repository;
	}

	@Cacheable(cacheNames = "communities")
	public List<Community> getCommunities() {
		return this.repository.findAll();
	}
}
