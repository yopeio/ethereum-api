package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ContractRequest {
    private String accountAddress;
    private String solidityContract;
}
