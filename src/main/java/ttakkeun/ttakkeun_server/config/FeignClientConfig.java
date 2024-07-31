package ttakkeun.ttakkeun_server.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ttakkeun.ttakkeun_server.TtakkeunServerApplication;

@Configuration
@EnableFeignClients(basePackageClasses = TtakkeunServerApplication.class)
public class FeignClientConfig {}
