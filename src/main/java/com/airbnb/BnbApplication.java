package com.airbnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BnbApplication {

	public static void main(String[] args) {
		SpringApplication.run(BnbApplication.class, args);
	}

	// we can use below class in --> SecurityConfig.java

//	@Bean
//	public PasswordEncoder getPasswordEncoder(){
//		return new BCryptPasswordEncoder();
//	}

	// this is how you can now create your own objects for --> @Autowired
	// by adding this in configuration file now PasswordEncoder knows which object to
	// create when the project is started
}

// this is the starting point of my project (BootStrapping happens here) -->
// it is also a configuration class (pre defined class)
//so loaded automatically in the memory first

// @Bean Annotation

// give this practical answer
// in Spring security i am using passwordEncoder with @Autowired for that the object will
// not be created --> so now which object to create --> i have to configure that -> and
// i will do that by using --> @Bean Annotation in my configuration class
