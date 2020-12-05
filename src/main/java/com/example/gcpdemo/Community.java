package com.example.gcpdemo;

public class Community {

	private Long id;

	private String alias;

	private String name;

	public Community() {
	}

	public Community(Long id, String alias, String name) {
		this.id = id;
		this.alias = alias;
		this.name = name;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
