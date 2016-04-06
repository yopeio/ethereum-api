package io.yope.ethereum.configuration;

import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.services.AccountService;
import io.yope.ethereum.services.ContractService;
import io.yope.ethereum.services.EthereumFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static io.yope.ethereum.utils.EthereumUtil.decryptQuantity;

@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "io.yope.ethereum.services"
})
public class ServiceConfiguration {

    @Bean
    public EthereumFacade facade(final ContractService contractService, final AccountService accountService) {
        return new EthereumFacade(contractService, accountService);
    }

    @Bean
    public ContractService contractService(EthereumRpc ethereumRpc) {
        long gasPrice = decryptQuantity(ethereumRpc.eth_gasPrice());
        return new ContractService(ethereumRpc, gasPrice);
    }

    @Bean
    public AccountService accountService(EthereumRpc ethereumRpc) {
        return new AccountService(ethereumRpc);
    }

}
