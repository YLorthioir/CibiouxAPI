package be.ylorth.cibiouxrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CibiouxRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CibiouxRestApplication.class, args);
    }

}
