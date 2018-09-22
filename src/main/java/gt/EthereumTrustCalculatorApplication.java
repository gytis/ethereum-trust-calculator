package gt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
@EnableConfigurationProperties(CalculatorProperties.class)
public class EthereumTrustCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EthereumTrustCalculatorApplication.class, args);
    }

    @Bean(destroyMethod = "shutdown")
    public Web3j web3j(CalculatorProperties properties) {
        HttpService httpService = new HttpService(properties.getNodeUrl());
        return Web3j.build(httpService);
    }

}