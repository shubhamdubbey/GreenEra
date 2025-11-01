package com.green_era.gardener_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.green_era.gardener_service.feign")
public class GardenerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GardenerServiceApplication.class, args);
	}

}
