package ttakkeun.ttakkeun_server.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import ttakkeun.ttakkeun_server.TtakkeunServerApplication;

@Configuration
@EnableFeignClients(basePackageClasses = TtakkeunServerApplication.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpClientConfiguration.class})
public class FeignClientConfig {}
