package io.yope.ethereum.visitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Method;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public abstract class BlockchainVisitor {

    @Getter
    @Setter
    private Map<Method.Type, Method> methods;

    @Setter
    @Getter
    private Account account;

    @Getter
    private String address;

    public abstract String getContent();

    public abstract String getName();

    public abstract void addMethod(Method method);

    public Method getMethod(Method.Type type) {
        return methods.get(type);
    }

}
