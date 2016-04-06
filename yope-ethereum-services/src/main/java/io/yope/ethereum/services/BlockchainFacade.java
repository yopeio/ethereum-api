package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.ContractRequest;
import io.yope.ethereum.model.Receipt;

import java.util.Map;

/**
 * Facade for blockchain management.
 */
public interface BlockchainFacade {
    /**
     * Write contracts into the blockchain.
     * @param request
     * @return
     * @throws ExceededGasException
     */
    Map<String, Receipt> createContracts(ContractRequest request) throws ExceededGasException;

    /**
     * Modify a stored contract.
     * @param contractAddress
     * @param request
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    Receipt modifyContract(String contractAddress, ContractRequest request) throws NoSuchContractMethod, ExceededGasException;

    /**
     * Run a specific contract.
     * @param contractAddress
     * @param request
     * @param <T>
     * @return
     * @throws NoSuchContractMethod
     */
    <T> T runContract(String contractAddress, ContractRequest request) throws NoSuchContractMethod;

    /**
     * Get account balance.
     * @param address
     * @return
     */
    long getAccountBalance(String address);
}
