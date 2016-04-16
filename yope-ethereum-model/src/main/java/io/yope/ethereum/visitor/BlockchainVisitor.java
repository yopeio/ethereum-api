package io.yope.ethereum.visitor;


import com.google.common.collect.Maps;
import io.yope.ethereum.model.Method;

import java.util.Map;

public abstract class BlockchainVisitor {

    private String contractAddress;
    private String accountAddress;
    private Map<Method.Type, Method> methods = Maps.newHashMap();

    public abstract String getContractKey();

    public abstract String getContractFile();

    public abstract String getContractContent();

    public abstract void addMethods();

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public void addMethod(Method method) {
        methods.put(method.getType(), method);
    }

    public Method getMethod(Method.Type type) {
        return methods.get(type);
    }


}
