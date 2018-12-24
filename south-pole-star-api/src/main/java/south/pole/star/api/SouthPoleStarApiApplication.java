package south.pole.star.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import south.pole.star.api.spring.annotation.EnableSouthStar;

@EnableSouthStar
@SpringBootApplication
public class SouthPoleStarApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SouthPoleStarApiApplication.class, args);
    }

}

