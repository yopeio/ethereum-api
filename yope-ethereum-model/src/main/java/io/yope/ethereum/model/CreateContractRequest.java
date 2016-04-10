package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateContractRequest extends  ContractRequest {
    public CreateContractRequest(String accountAddress, String solidityContract) {
        super(accountAddress, solidityContract);
    }
}
