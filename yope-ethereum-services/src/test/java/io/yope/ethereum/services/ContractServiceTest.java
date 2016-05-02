package io.yope.ethereum.services;

import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumResource;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.BlockchainVisitor;
import io.yope.ethereum.visitor.SimpleVisitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class ContractServiceTest {

    private static final int VARIABLES = 10;
    private ContractService contractService;
    private String ethereumAddress = "http://ethereum.yope.io";

    @Before
    public void init() throws MalformedURLException {

        EthereumRpc ethereumRpc = new EthereumResource(ethereumAddress).getGethRpc();
        contractService = new ContractService(ethereumRpc, 0);
    }


    @Test
    @Ignore
    public void testCreate() throws Exception, ExceededGasException {
        Object[] modifyArgs = {"5", "6"};
        String content = "contract simple { uint storedData = {0} ; function set(uint x) { storedData = x; } function get() constant returns (uint retVal) { return storedData; } }";
        BlockchainVisitor visitor = SimpleVisitor.builder().solidityContract(content).build();
        visitor.addMethod(Method.builder().type(Method.Type.MODIFY).args(modifyArgs).name("set").build());
        Map<Receipt.Type, Future<Receipt>> receipts = contractService.create(visitor, 1);
        assertEquals(receipts.size(), 1);
    }

    @Test
    public void testModify() throws Exception {

    }

    @Test
    public void testRun() throws Exception {

    }


}