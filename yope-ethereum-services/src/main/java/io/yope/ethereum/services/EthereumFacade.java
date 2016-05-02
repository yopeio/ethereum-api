package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@AllArgsConstructor
public class EthereumFacade implements BlockchainFacade {

    private ContractService contractService;

    private AccountService accountService;

    @Override
    public Map<Receipt.Type, Future<Receipt>> createContracts(final BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod {
        return contractService.create(visitor, getAccount(visitor.getAccountAddress()).getBalance());
    }

    @Override
    public Future<Receipt> modifyContract(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException {
        return contractService.modify(contractAddress, visitor, getAccount(visitor.getAccountAddress()).getBalance());
    }

    @Override
    public<T> T runContract(final String contractAddress, final BlockchainVisitor visitor)
            throws NoSuchContractMethod {
        return contractService.run(contractAddress, visitor);
    }

    @Override
    public Account getAccount(final String address) {
        return accountService.getAccount(address);
    }

    @Override
    public Account createAccount(final String passphrase) {
        return accountService.createAccount(passphrase);
    }

    @Override
    public boolean unlockAccount(final Account account) {
        return accountService.unlockAccount(account);
    }

}
