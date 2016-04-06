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
import io.yope.ethereum.model.ContractRequest;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Filter;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumRpc;
import lombok.AllArgsConstructor;

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


    public Map<String, Receipt> create(final String solidity, final String accountAddress, final long accountGas)
            throws ExceededGasException {
        Map<String, Receipt> contracts = Maps.newHashMap();
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        removeLineBreaks(solidity)
                );
        for (String contractKey : compiled.getContractData().keySet()) {
            ContractData contract = compiled.getContractData().get(contractKey);
            String code = contract.getCode();
            String subCode = code.substring(2, code.length());

            long gas = decryptQuantity(ethereumRpc.eth_estimateGas(
                    EthTransaction.builder().data(subCode).from(accountAddress).build()
            ));

            checkGas(accountAddress, accountGas, gas);

            String txHash = ethereumRpc.eth_sendTransaction(
                    EthTransaction.builder().data(subCode).from(accountAddress).gas(gas).gasPrice(gasPrice).build());
            Receipt receipt = getReceipt(txHash);

            String filter = ethereumRpc.eth_newFilter(Filter.builder().address(contract.getCode()).build());
            EthSmartContractFactory factory = new EthSmartContractFactory(contract);
            EthSmartContract smartContract = factory.getContract(receipt.getContractAddress());
            contracts.put(contractKey, receipt);
        }
        return contracts;
    }

    private void checkGas(String accountAddress, long accountGas, long gas) throws ExceededGasException {
        if (accountGas < gas) {
            throw new ExceededGasException("gas exceeded for account " + accountAddress);
        }
    }

    public Receipt modify(final String contractAddress, final ContractRequest request, long accountGas) throws NoSuchContractMethod, ExceededGasException {
        EthSmartContract smartContract = getSmartContract(request.getSolidityContract(), request.getContractKey(), contractAddress);
        String modMethodHash = callModMethod(smartContract, request.getMethod(), request.getAccountAddress(), accountGas, request.getArgs());
        return getReceipt(modMethodHash);
    }

    public<T> T run(final String contractAddress, ContractRequest request) throws NoSuchContractMethod {
        EthSmartContract smartContract = getSmartContract(request.getSolidityContract(), request.getContractKey(), contractAddress);
        return callConstantMethod(smartContract, request.getMethod(), request.getArgs());
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

    private Receipt getReceipt(String txHash) {
        while(ethereumRpc.eth_getTransactionReceipt(txHash) == null) {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
            }
        }
        return ethereumRpc.eth_getTransactionReceipt(txHash);
    }


}
