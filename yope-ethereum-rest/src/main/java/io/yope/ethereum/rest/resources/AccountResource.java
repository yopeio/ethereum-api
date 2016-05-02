package io.yope.ethereum.rest.resources;

import io.yope.ethereum.model.Account;
import io.yope.ethereum.services.EthereumFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountResource {

    @Autowired
    private EthereumFacade facade;

    @RequestMapping(value = "/{address}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    EthereumResponse<Account> getAccount(@PathVariable final String address) {
        return new EthereumResponse<Account>(facade.getAccount(address), 200, "OK");
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    EthereumResponse<Account> createAccount(@RequestBody final Account account) {
        return new EthereumResponse<Account>(facade.createAccount(account.getPassphrase()), 200, "OK");
    }

    @RequestMapping(value = "/unlock", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    EthereumResponse<Boolean> unlockAccount(@RequestBody final Account account) {
        return new EthereumResponse<Boolean>(facade.unlockAccount(account), 200, "OK");
    }

}
