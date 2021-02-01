package it.unipv.ingsw.l20PollingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class L20PollingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(L20PollingSystemApplication.class, args);
	}

}
