package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateContractRequest extends  ContractRequest {
    private BlockchainVisitor visitor;

    public CreateContractRequest(String accountAddress, String solidityContract) {
        super(accountAddress, solidityContract);
    }

    public CreateContractRequest(BlockchainVisitor visitor) {
        super(visitor.getAccountAddress(), visitor.getContractContent());
    }
}
