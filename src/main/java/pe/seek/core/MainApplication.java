package pe.seek.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "core-customer",
		version = "1.0",
		description = "Core Customer WebFlux API",
		contact = @Contact(
				name = "Arturo Diaz",
				email = "diazartiagacarlosarturo@gmail.com"
		)
))
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
