package io.yope.ethereum.services;

import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.visitor.BlockchainVisitor;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumResource;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.VisitorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static io.yope.ethereum.utils.EthereumUtil.removeLineBreaksFromFile;
import static org.junit.Assert.assertEquals;

@Slf4j
public class ContractServiceTest {

    private static final long ACCOUNT_GAS = 100000;
    private static final String ethereumAddress = "http://ethereum.yope.io";
    private static final String accountAddress = "0x03733b713032e9040d04acd4720bedaa717378df";

    private ContractService contractService;

    BlockchainVisitor visitor;

    private Method create = VisitorFactory.buildMethod(Optional.empty(), Optional.of(new Object[]{5}));
    private Method write = VisitorFactory.buildMethod(Optional.of("set"), Optional.of(new Object[]{10}));
    private Method read = VisitorFactory.buildMethod(Optional.of("get"), Optional.empty());

    @Before
    public void init() throws MalformedURLException {
        EthereumRpc ethereumRpc = new EthereumResource(ethereumAddress).getGethRpc();
        contractService = new ContractService(ethereumRpc, 20000000000L);
        visitor = VisitorFactory.build(
                accountAddress,
                null,
                "sample",
                removeLineBreaksFromFile("sample.sol", ContractServiceTest.class),
                null,
                create);
    }


    @Test
//    @Ignore
    public <T> void testCreate() throws Exception, ExceededGasException {
        Future<Receipt> createReceipt = contractService.create(visitor, ACCOUNT_GAS);
        visitor.setMethod(read);
        int res = read(createReceipt);
        assertEquals(5, res);
        visitor.setMethod(write);
        Future<Receipt> writeReceipt = write(createReceipt);
        visitor.setMethod(read);
        res = read(writeReceipt);
        assertEquals(10, res);
    }

    private int read(Future<Receipt> receipt) throws Exception {
        String contractAddress = waitFor(receipt);
        BigInteger result = contractService.<BigInteger>run(contractAddress, visitor);
        log.info("result: {}", result);
        return  result.intValue();
    }

    private Future<Receipt> write(Future<Receipt> receipt) throws Exception, ExceededGasException {
        String contractAddress = waitFor(receipt);
         return contractService.modify(contractAddress, visitor, ACCOUNT_GAS);
    }


    private String waitFor(Future<Receipt> receipt) throws InterruptedException, ExecutionException {
        while(!receipt.isDone()) {
            Thread.sleep(1000);
        }
        return receipt.get().getContractAddress();
    }


}