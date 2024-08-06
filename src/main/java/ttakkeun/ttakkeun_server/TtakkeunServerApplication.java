package ttakkeun.ttakkeun_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "ttakkeun.ttakkeun_server.repository")
public class TtakkeunServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtakkeunServerApplication.class, args);
	}

}
