package crud;

import crud.entities.User;
import crud.impl.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

/**
 * Created by pozar on 23.06.2017.
 */

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    //    Add test data
    @Bean
    public CommandLineRunner loadData(final UserService service) {
        Random random = new Random();
        return (args) -> {
            for (int i = 0; i < 1000; i++) {
                service.saveOrUpdate(new User("name" + i % 100, random.nextInt(100), random.nextBoolean()));
            }
        };
    }
}
