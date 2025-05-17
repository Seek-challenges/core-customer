package pe.seek.core.customer;

import org.springframework.boot.SpringApplication;
import pe.seek.core.MainApplication;

public class TestCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.from(MainApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
