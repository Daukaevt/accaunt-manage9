package com.wixsite.mupbam1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication

@EnableDiscoveryClient
public class AccauntManag9ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccauntManag9ApiGatewayApplication.class, args);
	}

}
