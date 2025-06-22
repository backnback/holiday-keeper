package com.planit.holiday_keeper.global.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient() {
    ConnectionProvider provider = ConnectionProvider.builder("api-pool")
        .maxConnections(100)
        .maxIdleTime(Duration.ofSeconds(30))
        .build();

    HttpClient httpClient = HttpClient.create(provider)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .responseTimeout(Duration.ofSeconds(30));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
