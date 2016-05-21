package io.yope.ethereum.visitor.sample;

import com.google.common.collect.Maps;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.visitor.BlockchainVisitor;
import io.yope.ethereum.visitor.VisitorFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
public class SampleVisitor extends BlockchainVisitor {

    public SampleVisitor() {
        addMethod(VisitorFactory.buildMethod(Method.Type.CREATE, Optional.empty(), Optional.ofNullable(createArgs)));
        addMethod(VisitorFactory.buildMethod(Method.Type.MODIFY, Optional.ofNullable(modifyMethod), Optional.ofNullable(modifyArgs)));
        addMethod(VisitorFactory.buildMethod(Method.Type.RUN, Optional.ofNullable(runMethod), Optional.ofNullable(runArgs)));
    }

    private String modifyMethod;
    private String runMethod;
    private String content;
    private String name;
    private Account account;
    private Object[] createArgs;
    private Object[] modifyArgs;
    private Object[] runArgs;

    @Override
    public void addMethod(Method method) {
        if (getMethods() == null) {
            setMethods(Maps.newHashMap());
        }
        getMethods().put(method.getType(), method);
    }
}
