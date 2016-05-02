package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;

import java.util.Map;
import java.util.concurrent.Future;

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
    Map<Receipt.Type, Future<Receipt>> createContracts(BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod;

    /**
     * Modify a contract stored into the blockchain.
     * @param visitor
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    Future<Receipt> modifyContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException;

    /**
     * Run a contract stored into the blockchain.
     * @param visitor
     * @param <T>
     * @return
     * @throws NoSuchContractMethod
     */
    <T> T runContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod;

    /**
     * Get account from Ethereum with its balance.
     * @param address
     * @return
     */
    Account getAccount(String address);

    /**
     * Create an account in Ethereum and unlock it.
     * @param passphrase
     * @return
     */
    Account createAccount(String passphrase);

    /**
     * Unlock an already existent account.
     * This method is already called during account creation. It is necessary just in case the Ethereum node is restarted.
     * @param account
     * @return
     */
    boolean unlockAccount(Account account);
}
