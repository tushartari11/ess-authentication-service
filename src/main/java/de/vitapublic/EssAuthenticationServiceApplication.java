package de.vitapublic;

import de.vitapublic.essAuthenticationService.common.RestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EssAuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EssAuthenticationServiceApplication.class, args);
    }

    @Bean
    public RestConfig restConfig() {
        return new RestConfig();
    }
}
