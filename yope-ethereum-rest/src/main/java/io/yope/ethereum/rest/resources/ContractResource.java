package io.yope.ethereum.rest.resources;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.services.BlockchainFacade;
import io.yope.ethereum.visitor.BlockchainVisitor;
import io.yope.ethereum.visitor.SimpleVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/contracts")
public class ContractResource<T> {

    @Autowired
    private BlockchainFacade facade;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody EthereumResponse< Map<Receipt.Type, Future<Receipt>>> createContracts(@RequestBody final SimpleVisitor visitor) throws ExecutionException, InterruptedException {
        try {
            Map<Receipt.Type, Future<Receipt>> contracts = facade.createContracts(visitor);
            for(Future<Receipt> future : contracts.values()) {
                return new EthereumResponse(future.get(), 200, "OK");
            }
            return null;
        } catch (ExceededGasException e) {
            return new EthereumResponse(null,400, e.getMessage());
        } catch (NoSuchContractMethod e) {
            return new EthereumResponse(null,404, e.getMessage());
        }
    }


    @RequestMapping(value = "/{contractAddress}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public @ResponseBody EthereumResponse<Receipt> modifyContract(@PathVariable final String contractAddress, @RequestBody final SimpleVisitor visitor) {
        try {
            return new EthereumResponse(facade.modifyContract(contractAddress, visitor),200, "OK");
        } catch (ExceededGasException e) {
            return new EthereumResponse(null,400, e.getMessage());
        } catch (NoSuchContractMethod e) {
            return new EthereumResponse(null,404, e.getMessage());
        }
    }

    @RequestMapping(value = "/{contractAddress}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody EthereumResponse<T> runContract(@PathVariable final String contractAddress, @RequestBody final SimpleVisitor visitor) throws NoSuchContractMethod {
        return new EthereumResponse(facade.runContract(contractAddress, visitor),200, "OK");
    }

}
