package io.yope.ethereum.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EthereumFacade implements BlockchainFacade {

    private ContractService contractService;

    private AccountService accountService;

    @Override
    public Map<String, Receipt> createContracts(final BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod {
        Map<String, Receipt> receipts = contractService.create(visitor, getAccountBalance(visitor.getAccountAddress()));
        if (log.isDebugEnabled()) {
            for (Receipt receipt : receipts.values()) {
                log.debug("created contract: {}", receipt);
            }
        }
        String contractAddr = receipts.values().iterator().next().getContractAddress();
        if (ArrayUtils.isNotEmpty(visitor.getMethod(Method.Type.MODIFY).getArgs()) ) {
            Receipt receipt = modifyContract(contractAddr, visitor);
            log.debug("updated contract: {}", receipt);
        }
        return receipts;
    }

    @Override
    public Receipt modifyContract(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException {
        return contractService.modify(contractAddress, visitor, getAccountBalance(visitor.getAccountAddress()));
    }

    @Override
    public<T> T runContract(final String contractAddress, final BlockchainVisitor visitor)
            throws NoSuchContractMethod {
        return contractService.run(contractAddress, visitor);
    }

    @Override
    public long getAccountBalance(final String address) {
        return accountService.getBalance(address);
    }

}
