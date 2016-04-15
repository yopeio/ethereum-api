package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;

import java.util.Map;

/**
 * Facade for blockchain management.
 */
public interface BlockchainFacade {
    /**
     * Write contracts into the blockchain.
     * @param visitor
     * @return
     * @throws ExceededGasException
     */
    Map<String, Receipt> createContracts(BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod;

    /**
     * Modify a contract stored into the blockchain.
     * @param visitor
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    Receipt modifyContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException;

    /**
     * Run a contract stored into the blockchain.
     * @param visitor
     * @param <T>
     * @return
     * @throws NoSuchContractMethod
     */
    <T> T runContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod;

    /**
     * Get account balance.
     * @param address
     * @return
     */
    long getAccountBalance(String address);
}
