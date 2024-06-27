package at.fhv.votingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"at.fhv.votingservice", "at.fhv.common"})
public class VotingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingServiceApplication.class, args);
    }

}
