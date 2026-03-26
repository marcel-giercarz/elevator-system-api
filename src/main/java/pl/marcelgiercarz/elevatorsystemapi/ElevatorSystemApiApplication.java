package pl.marcelgiercarz.elevatorsystemapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Elevator System API",
                description = "API documentation"
        )
)
@SpringBootApplication
public class ElevatorSystemApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElevatorSystemApiApplication.class, args);
    }

}
