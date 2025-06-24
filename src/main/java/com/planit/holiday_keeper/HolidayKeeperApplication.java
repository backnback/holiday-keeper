package com.planit.holiday_keeper;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "Holiday Keeper API",
				description = "공휴일 관리 시스템 API 문서",
				version = "1.0.0",
				contact = @Contact(
						name = "River Lee",
						email = "garam8796@gmail.com"
				)
		),
		servers = {
				@Server(url = "http://localhost:8090", description = "개발 서버"),
				@Server(url = "https://api.backnback.site", description = "운영 서버")
		}
)
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
public class HolidayKeeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidayKeeperApplication.class, args);
	}

}
