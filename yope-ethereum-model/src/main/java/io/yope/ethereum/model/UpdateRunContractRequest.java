package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRunContractRequest extends  ContractRequest {
    private String contractKey;
    private String contractAddress;
    private String method;
    private Object[] args;
    private BlockchainVisitor visitor;

    public UpdateRunContractRequest(String accountAddress, String solidityContract, String contractKey, String contractAddress, String method, Object... args) {
        super(accountAddress, solidityContract);
        this.args = args;
        this.contractKey = contractKey;
        this.method = method;
        this.contractAddress = contractAddress;
    }

    public UpdateRunContractRequest(BlockchainVisitor visitor) {
        super(visitor.getAccountAddress(), visitor.getContractContent());
        this.args = visitor.getArgs();
        this.contractKey = visitor.getContractKey();
        this.method = visitor.getRetrieveMethod();
        this.contractAddress = visitor.getContractAddress();
    }

}

