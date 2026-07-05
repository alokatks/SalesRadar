package com.carsales.carsales_anaytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CarsalesAnayticsApplication {

	public static void main(String[] args) {

		SpringApplication.run(CarsalesAnayticsApplication.class, args);

		System.out.println("hehhh");
	}
}
