package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yope.ethereum.utils.EthereumUtil;
import lombok.*;

import java.io.Serializable;

@Builder(builderClassName="Builder", toBuilder=true)
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class Receipt implements Serializable {
    @Getter
    private String transactionHash;
    @Getter
    private String contractAddress;
    @Getter
    private String blockHash;
    private String transactionIndex;
    private String blockNumber;
    private String cumulativeGasUsed;
    private String gasUsed;


    public long getTransactionIndex() {
        return EthereumUtil.decryptQuantity(transactionIndex);
    }

    public long getBlockNumber() {
        return EthereumUtil.decryptQuantity(blockNumber);
    }

    public long getCumulativeGasUsed() {
        return EthereumUtil.decryptQuantity(cumulativeGasUsed);
    }

    public long getGasUsed() {
        return EthereumUtil.decryptQuantity(gasUsed);
    }


}
