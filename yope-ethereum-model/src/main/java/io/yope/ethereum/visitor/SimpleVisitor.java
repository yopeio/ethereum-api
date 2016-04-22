package io.yope.ethereum.visitor;

import io.yope.ethereum.model.Method;
import lombok.*;
import lombok.experimental.Wither;

import static io.yope.ethereum.utils.EthereumUtil.removeLineBreaks;

/**
 * It is intended to be used only as example.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Wither
public class SimpleVisitor extends BlockchainVisitor {

    private String solidityContract;
    private Object[] modArgs;
    private Object[] runArgs;

    @Override
    public String getContractKey() {
        return "simple";
    }

    @Override
    public String getContractFile() {
        throw new UnsupportedOperationException("not needed");
    }

    @Override
    public String getContractContent() {
        return removeLineBreaks(solidityContract);
    }

    @Override
    public void addMethods() {
        this.addMethod(Method.builder().type(Method.Type.MODIFY).name("set")
                .args(modArgs).build());
        this.addMethod(Method.builder().type(Method.Type.RUN).name("get").args(runArgs).build());
    }


}
