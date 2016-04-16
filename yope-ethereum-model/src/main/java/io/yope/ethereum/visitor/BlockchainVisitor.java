package io.yope.ethereum.visitor;


public abstract class BlockchainVisitor {

    private String contractAddress;
    private String accountAddress;

    public abstract String getContractKey();

    public abstract String getContractFile();

    public abstract String getContractContent();

    public abstract String getRunMethod();

    public abstract String getModifyMethod();

    public abstract Object[] getModifyArgs();

    public abstract Object[] getRunArgs();

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
}
