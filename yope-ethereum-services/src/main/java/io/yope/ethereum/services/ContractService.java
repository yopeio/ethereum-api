package io.yope.ethereum.services;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthSmartContract;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.NoSuchContractMethod;
import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.ContractData;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.concurrent.*;

import static io.yope.ethereum.utils.EthereumUtil.adapt;
import static io.yope.ethereum.utils.EthereumUtil.decryptQuantity;

@AllArgsConstructor
@Slf4j
public class ContractService {

    /*
    timeout in milliseconds of receipt waiting time.
     */
    private static final long TIMEOUT = 1000;

    private EthereumRpc ethereumRpc;

    private long gasPrice;


    /**
     * Creates a contract into Ethereum. It returns a map of futures receipts.
     * @param visitor
     * @param accountGas
     * @return
     * @throws ExceededGasException
     * @throws NoSuchContractMethod
     */
    public Future<Receipt> create(final BlockchainVisitor visitor, final long accountGas)
            throws ExceededGasException, NoSuchContractMethod {
        String content = getContent(visitor);
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(content
                );
        ContractData contract = compiled.getContractData().get(visitor.getName());
        String code = contract.getCode();
        String subCode = code.substring(2, code.length());

        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(
                EthTransaction.builder().data(subCode).from(visitor.getAccount().getAddress()).build()
        ));

        checkGas(visitor.getAccount().getAddress(), accountGas, gas);

        String txHash = ethereumRpc.eth_sendTransaction(
                EthTransaction.builder().data(subCode).from(visitor.getAccount().getAddress()).gas(gas).gasPrice(gasPrice).build());
        return getFutureReceipt(txHash, null, Receipt.Type.CREATE);
    }

    /**
     * Send a transaction from an account to another.
     * @param from
     * @param to
     * @param amount
     * @return
     */
    public Future<Receipt> sendTransaction(final String from, final String to, long amount) {
        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(EthTransaction.builder().from(from).to(to).value(amount).build()));
        String txHash = ethereumRpc.eth_sendTransaction(
                EthTransaction.builder().from(from).to(to).gas(gas).gasPrice(gasPrice).value(amount).build());
        return getFutureReceipt(txHash, null, Receipt.Type.CREATE);
    }

    /**
     * Modify the contract state, through storage update, into Ethereum. It returns a future receipt.
     * @param contractAddress
     * @param visitor
     * @param accountGas
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    public Future<Receipt> modify(final String contractAddress, final BlockchainVisitor visitor, final long accountGas) throws NoSuchContractMethod, ExceededGasException {
//        addMethods(visitor);
        EthSmartContract smartContract = getSmartContract(contractAddress, visitor);
        String modMethodHash = callModMethod(smartContract, visitor.getMethod().getName(), visitor.getAccount().getAddress(), accountGas, visitor.getMethod().getArgs());
        return getFutureReceipt(modMethodHash, contractAddress, Receipt.Type.MODIFY);
    }

    /**
     * Run a contract method, registered into Ethereum. It returns a generic value.
     * @param contractAddress
     * @param visitor
     * @param <T>
     * @return
     * @throws NoSuchContractMethod
     */
    public<T> T run(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod {
        EthSmartContract smartContract = getSmartContract(contractAddress, visitor);
        return callConstantMethod(smartContract, visitor.getMethod().getName(), visitor.getMethod().getArgs());
    }

    private String getContent(BlockchainVisitor visitor) {
        Object[] args = visitor.getCreateArgs();
        return MessageFormat.format( adapt(visitor.getContent(), args.length), args);
    }


    private void checkGas(final String accountAddress, final long accountGas, final long gas) throws ExceededGasException {
        if (accountGas < gas) {
            throw new ExceededGasException("gas exceeded for account " + accountAddress);
        }
    }

    private<T> T callConstantMethod(final EthSmartContract smartContract, final String method, final Object... args) throws NoSuchContractMethod {
        EthCall ethCall = smartContract.callConstantMethod(method, args);
        ethCall.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        String callMethod = ethereumRpc.eth_call(ethCall.getCall());
        return (T)ethCall.decodeOutput(callMethod)[0];
    }

    private String callModMethod(final EthSmartContract smartContract,final String method, final String accountAddress, final long accountGas, Object... args)
            throws NoSuchContractMethod, ExceededGasException {
        com.cegeka.tetherj.EthTransaction ethTransaction = smartContract.callModMethod(method, args);
        ethTransaction.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        EthTransaction.Builder builder = EthTransaction.builder()
                .data(CryptoUtil.byteToHex((ethTransaction.getData())))
                .gasPrice(gasPrice)
                .from(accountAddress)
                .to(ethTransaction.getTo());
        EthTransaction tx = builder.build();
        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(tx));
        checkGas(accountAddress, accountGas, gas);
        return ethereumRpc.eth_sendTransaction(builder.gas(gas).build());
    }

    private EthSmartContract getSmartContract(final String contractAddress, final BlockchainVisitor visitor) {
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        getContent(visitor)
                );
        ContractData contract = compiled.getContractData().get(visitor.getName());
        EthSmartContractFactory factory = new EthSmartContractFactory(contract);
        return factory.getContract(contractAddress);
    }

    private Future<Receipt> getFutureReceipt(final String txHash, final String contractAddress, final Receipt.Type type) {
        ExecutorService threadpool = Executors.newSingleThreadExecutor();
        ReceiptTask task = new ReceiptTask(txHash, ethereumRpc, contractAddress, type);
        CompletionService<Receipt> completionService = new ExecutorCompletionService(threadpool);
        Future<Receipt> future = completionService.submit(task);
        threadpool.shutdown();
        try {
            threadpool.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Recepit task interrupted " +
                    "Transaction hash {}, contract address {}, type {} ",
                    txHash, contractAddress, type);
        } finally {
            if (!threadpool.isTerminated()) {
                log.warn("cancel non-finished tasks. " +
                        "Transaction hash {}, contract address {}, type {} ",
                        txHash, contractAddress, type);
            }
            threadpool.shutdownNow();
            log.warn("forced shutdown finished " +
                            "Transaction hash {}, contract address {}, type {} ",
                            txHash, contractAddress, type);
        }

        return future;
    }

    /**
     * Receipt asynchronous task.
     */
    private static class ReceiptTask implements Callable {

        private String txHash;
        private EthereumRpc ethereumRpc;
        private String contractAddress;
        private Receipt.Type type;

        public ReceiptTask(final String txHash, final EthereumRpc ethereumRpc, final String contractAddress, final Receipt.Type type) {
            this.txHash = txHash;
            this.ethereumRpc = ethereumRpc;
            this.contractAddress = contractAddress;
            this.type = type;
        }

        @Override
        public Receipt call() {
            while(ethereumRpc.eth_getTransactionReceipt(txHash) == null) {
                try {
                    Thread.sleep(TIMEOUT);
                } catch (InterruptedException e) {
                }
            }
            Receipt receipt = ethereumRpc.eth_getTransactionReceipt(txHash);
            receipt.setType(type);
            if (contractAddress != null) {
                receipt.setContractAddress(contractAddress);
            }
            log.debug("receipt: {}", receipt);
            return receipt;
        }

    }
}
