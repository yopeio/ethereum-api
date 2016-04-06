package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.ContractRequest;
import io.yope.ethereum.model.Receipt;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class EthereumFacade implements BlockchainFacade {

    private ContractService contractService;

    private AccountService accountService;

    @Override
    public Map<String, Receipt> createContracts(final ContractRequest request) throws ExceededGasException {
        return contractService.create(request.getSolidityContract(), request.getAccountAddress(), getAccountBalance(request.getAccountAddress()));
    }

    @Override
    public Receipt modifyContract(final String contractAddress, final ContractRequest request) throws NoSuchContractMethod, ExceededGasException {
        return contractService.modify(contractAddress, request, getAccountBalance(request.getAccountAddress()));
    }

    @Override
    public<T> T runContract(final String contractAddress, ContractRequest request)
            throws NoSuchContractMethod {
        return contractService.run(contractAddress, request);
    }

    @Override
    public long getAccountBalance(final String address) {
        return accountService.getBalance(address);
    }

}
