package com.demo.chatApp;

import com.demo.chatApp.keyManager.KeyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ChatAppApplication {

	public static void main(String[] args) {
		 SpringApplication.run(ChatAppApplication.class, args);
	}

}
