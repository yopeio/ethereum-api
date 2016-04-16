package io.yope.ethereum.services;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthSmartContract;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.NoSuchContractMethod;
import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.ContractData;
import com.google.common.collect.Maps;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.yope.ethereum.utils.EthereumUtil.decryptQuantity;
import static io.yope.ethereum.utils.EthereumUtil.removeLineBreaks;

@AllArgsConstructor
public class ContractService {

    /*
    timeout in milliseconds.
     */
    private static final long TIMEOUT = 500;

    private EthereumRpc ethereumRpc;

    private long gasPrice;


    public Map<String, Receipt> create(final BlockchainVisitor visitor, final long accountGas)
            throws ExceededGasException {
        Map<String, Receipt> contracts = Maps.newHashMap();
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(visitor.getContractContent()
                );
        for (String contractKey : compiled.getContractData().keySet()) {
            ContractData contract = compiled.getContractData().get(contractKey);
            String code = contract.getCode();
            String subCode = code.substring(2, code.length());

            long gas = decryptQuantity(ethereumRpc.eth_estimateGas(
                    EthTransaction.builder().data(subCode).from(visitor.getAccountAddress()).build()
            ));

            checkGas(visitor.getAccountAddress(), accountGas, gas);

            String txHash = ethereumRpc.eth_sendTransaction(
                    EthTransaction.builder().data(subCode).from(visitor.getAccountAddress()).gas(gas).gasPrice(gasPrice).build());
            Receipt receipt = getReceipt(txHash, null);

//            String filter = ethereumRpc.eth_newFilter(Filter.builder().address(contract.getCode()).build());
            EthSmartContractFactory factory = new EthSmartContractFactory(contract);
//            EthSmartContract smartContract = factory.getContract(receipt.getContractAddress());
            contracts.put(contractKey, receipt);
        }
        return contracts;
    }

    public Receipt modify(final String contractAddress, final BlockchainVisitor visitor, long accountGas) throws NoSuchContractMethod, ExceededGasException {
        EthSmartContract smartContract = getSmartContract(visitor.getContractContent(),visitor.getContractKey(), contractAddress);
        String modMethodHash = callModMethod(smartContract, visitor.getMethod(Method.Type.MODIFY).getName(), visitor.getAccountAddress(), accountGas, visitor.getMethod(Method.Type.MODIFY).getArgs());
        return getReceipt(modMethodHash, contractAddress);
    }

    public<T> T run(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod {
        EthSmartContract smartContract = getSmartContract(visitor.getContractContent(), visitor.getContractKey(), contractAddress);
        return callConstantMethod(smartContract, visitor.getMethod(Method.Type.RUN).getName(), visitor.getMethod(Method.Type.RUN).getArgs());
    }

    private void checkGas(String accountAddress, long accountGas, long gas) throws ExceededGasException {
        if (accountGas < gas) {
            throw new ExceededGasException("gas exceeded for account " + accountAddress);
        }
    }

    private<T> T callConstantMethod(EthSmartContract smartContract, String method, Object... args) throws NoSuchContractMethod {
        EthCall ethCall = smartContract.callConstantMethod(method, args);
        ethCall.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        String callMethod = ethereumRpc.eth_call(ethCall.getCall());
        return (T)ethCall.decodeOutput(callMethod)[0].toString();
    }

    private String callModMethod(EthSmartContract smartContract, String method, final String accountAddress, final long accountGas, Object... args)
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

    private EthSmartContract getSmartContract(String solidityFile, String contractKey, String contractAddress) {
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        removeLineBreaks(solidityFile)
                );
        ContractData contract = compiled.getContractData().get(contractKey);
        EthSmartContractFactory factory = new EthSmartContractFactory(contract);
        return factory.getContract(contractAddress);
    }

    private Receipt getReceipt(String txHash, String contractAddress) {
        while(ethereumRpc.eth_getTransactionReceipt(txHash) == null) {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
            }
        }
        Receipt receipt = ethereumRpc.eth_getTransactionReceipt(txHash);
        if (StringUtils.isNotBlank(contractAddress)) {
            receipt = receipt.toBuilder().contractAddress(contractAddress).build();
        }
        return receipt;
    }
}
