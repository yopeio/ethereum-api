package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    public UpdateRunContractRequest(String accountAddress, String solidityContract, String contractKey, String contractAddress, String method, Object... args) {
        super(accountAddress, solidityContract);
        this.args = args;
        this.contractKey = contractKey;
        this.method = method;
        this.contractAddress = contractAddress;
    }
}

