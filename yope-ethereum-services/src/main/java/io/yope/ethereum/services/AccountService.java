package io.yope.ethereum.services;

import io.yope.ethereum.rpc.EthereumRpc;
import lombok.AllArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
public class AccountService {

    private EthereumRpc ethereumRpc;

    /**
     * account balance in szabo. (1 ether = 1.000.000 szabo)
     * @param address
     * @return
     */
    public long getBalance(final String address) {
        String balance = ethereumRpc.eth_getBalance(address, "latest");
        BigInteger latestBalance = new BigInteger(
                "00" + balance.substring(2), 16);
        return latestBalance.divide(BigInteger.valueOf(1000000000000L)).longValue();
    }
}
