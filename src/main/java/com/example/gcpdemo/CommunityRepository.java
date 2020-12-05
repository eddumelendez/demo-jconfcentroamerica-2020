package com.example.gcpdemo;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityRepository {

	private final JdbcTemplate jdbcTemplate;

	public CommunityRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Community> findAll() {
		return this.jdbcTemplate.query("select id, alias, name from communities",
				(rs, i) -> new Community(rs.getLong("id"), rs.getString("alias"), rs.getString("name")));
	}

}
