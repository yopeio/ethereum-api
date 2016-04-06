package io.yope.ethereum.rest.resources;

import io.yope.ethereum.services.EthereumFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountResource {

    @Autowired
    private EthereumFacade facade;

    @RequestMapping(value = "/{account}/balance", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    EthereumResponse<Long> getBalance(@PathVariable final String account) {
        return new EthereumResponse<Long>(facade.getAccountBalance(account), 200, "OK");
    }
    
}
