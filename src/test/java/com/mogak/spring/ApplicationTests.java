package com.mogak.spring;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.profiles.active=local")
class ApplicationTests {

	@Test
	void contextLoads() {

	}

}
