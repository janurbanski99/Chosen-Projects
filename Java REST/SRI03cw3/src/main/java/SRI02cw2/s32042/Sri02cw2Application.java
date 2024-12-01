package SRI02cw2.s32042;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Sri02cw2Application {

	public static void main(String[] args) {
		SpringApplication.run(Sri02cw2Application.class, args);
	}

	@Bean
	public ModelMapper
	modelMapper() { return new
			ModelMapper();
	}
}
