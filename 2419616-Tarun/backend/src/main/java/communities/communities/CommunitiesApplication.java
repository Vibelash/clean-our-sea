package communities.communities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunitiesApplication {

	public static void main(String[] args) {
		// Give local development defaults precedence over stale machine env vars.
		System.setProperty("spring.datasource.url", "jdbc:h2:mem:communities;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
		System.setProperty("spring.datasource.username", "sa");
		System.setProperty("spring.datasource.password", "");
		SpringApplication.run(CommunitiesApplication.class, args);
	}

}
