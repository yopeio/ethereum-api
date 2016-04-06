package io.yope.ethereum.rpc;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthSmartContract;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.NoSuchContractMethod;
import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.ContractData;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Filter;
import io.yope.ethereum.model.Receipt;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.stream.Collectors;

@Slf4j
@Ignore
public class HelloWorldTest {

    private static final String GAS_PRICE = "0x174876e800";
    private static final String CONTRACT_ADDRESS = "0x67050ff6c97acc5440c8a511b362105d71ed7dbd";
    private static final String CONTRACT_ADDRESS_2 = "0x6273f3acc92b47d0e931ce7fd6f11d983e2c720b";
    private EthereumRpc ethereumRpc;
    private static final String ETHEREUM_ADDR = "http://ethereum.yope.io";
    private static final String ACCOUNT = "0x03733b713032e9040d04acd4720bedaa717378df";
    private long gasPrice;

    @Before
    public void init() throws MalformedURLException {
        ethereumRpc = new EthereumResource(ETHEREUM_ADDR).getGethRpc();
        gasPrice =  decryptQuantity(ethereumRpc.eth_gasPrice());
    }

    @Test
    public void testDeployAndRun() throws JsonProcessingException, NoSuchContractMethod {

        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        removeLineBreaks("SimpleStorage.sol")
                );
        for (String contractKey : compiled.getContractData().keySet()) {
            ContractData contract = compiled.getContractData().get(contractKey);
            String code = contract.getCode();
            String subCode = code.substring(2, code.length());

            long gas = decryptQuantity(ethereumRpc.eth_estimateGas(
                    EthTransaction.builder().data(subCode).from(ACCOUNT).build()
            ));

            String txHash = ethereumRpc.eth_sendTransaction(
                    EthTransaction.builder().data(subCode).from(ACCOUNT).gas(gas).gasPrice(gasPrice).build());
            Receipt receipt = getReceipt(txHash);

            String filter = ethereumRpc.eth_newFilter(Filter.builder().address(contract.getCode()).build());
            EthSmartContractFactory factory = new EthSmartContractFactory(contract);
            EthSmartContract smartContract = factory.getContract(receipt.getContractAddress());
            String modMethodHash = callModMethod(smartContract, "set", BigInteger.valueOf(53));
            Receipt modMethodRecepit = getReceipt(modMethodHash);
            String res = callConstantMethod(smartContract, "get");
            log.info(res);
        }
    }

    @Test
    public void testRun() throws NoSuchContractMethod {
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        removeLineBreaks("SimpleStorage.sol")
                );
        ContractData contract = compiled.getContractData().get("SimpleStorage");
        EthSmartContractFactory factory = new EthSmartContractFactory(contract);
        EthSmartContract smartContract = factory.getContract(CONTRACT_ADDRESS_2);
        String res = callConstantMethod(smartContract, "get");
        log.info(res);
    }

    public static Long decryptQuantity(String quantity) {
        BigInteger latestBalance = new BigInteger(
                "00" + quantity.substring(2), 16);
        return latestBalance.longValue();
    }


    private Receipt getReceipt(String txHash) {
        while(ethereumRpc.eth_getTransactionReceipt(txHash) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        return ethereumRpc.eth_getTransactionReceipt(txHash);
    }

    private String callConstantMethod(EthSmartContract smartContract, String method) throws NoSuchContractMethod {
        EthCall ethCall = smartContract.callConstantMethod(method);
        ethCall.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        String callMethod = ethereumRpc.eth_call(ethCall.getCall());
        String res = ethCall.decodeOutput(callMethod)[0].toString();
        return res;
    }

    private String callModMethod(EthSmartContract smartContract, String method, Object... args) throws NoSuchContractMethod {
        com.cegeka.tetherj.EthTransaction ethTransaction = smartContract.callModMethod(method, args);
        ethTransaction.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        EthTransaction.Builder builder = EthTransaction.builder()
                .data(CryptoUtil.byteToHex((ethTransaction.getData())))
                .gasPrice(gasPrice)
                .from(ACCOUNT)
                .to(ethTransaction.getTo());
        EthTransaction tx = builder.build();
        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(tx));
        return ethereumRpc.eth_sendTransaction(builder.gas(gas).build());
    }

    private static String removeLineBreaks(final String file) {
        InputStream stream = HelloWorldTest.class.getClass().getResourceAsStream(file);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        String collect = buffer.lines().collect(Collectors.joining("\n"));
        return collect.replace("\n", "").replace("\r", "");
    }


}
