package org.zerock.b01;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.zerock.b01.service.MemberInitService;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class B01Application {

	public static void main(String[] args) {
		SpringApplication.run(B01Application.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(MemberInitService memberInitService) {
		return args -> {
			memberInitService.initTestUser();
		};
	}
}
