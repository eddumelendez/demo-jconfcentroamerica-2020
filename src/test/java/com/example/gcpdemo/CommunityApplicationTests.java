package com.example.gcpdemo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CommunityApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql").withTag("8.0.22"))
			.withDatabaseName("demodb");

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:5.0.6");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.redis.host", redis::getContainerIpAddress);
		registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
	}

	@Test
	void contextLoads() {
		RestAssured.port = this.port;

		assertThat(this.redisTemplate.keys("*")).hasSize(0);

		when().get("/api/communities").then().body("size()", equalTo(7));

		assertThat(this.redisTemplate.keys("*")).hasSize(1);
		assertThat(this.redisTemplate.hasKey("communities::SimpleKey []")).isTrue();
	}

}
