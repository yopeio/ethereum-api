package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder(builderClassName="Builder", toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractRequest {
    private String accountAddress;
    private String contractKey;
    private String solidityContract;
    private String contractAddress;
    private String method;
    private Object[] args = new Object[0];
}
