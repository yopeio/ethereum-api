package io.yope.ethereum.visitor;


public abstract class BlockchainVisitor {

    public abstract String getContractKey();

    public abstract String getContractFile();

    public abstract String getContractContent();

    public abstract String getNewMethod();

    public abstract String getRetrieveMethod();

    public abstract String getAccountAddress();

    public abstract Object[] getArgs();

    public abstract String getContractAddress();
}
