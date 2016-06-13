package io.yope.ethereum.rpc;

import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.TransactionCall;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Filter;
import io.yope.ethereum.model.Receipt;

public interface ContractRpc {

  CompileOutput eth_compileSolidity(String sourceCode);

  String eth_sendTransaction(EthTransaction tx);

  Receipt eth_getTransactionReceipt(String txHash);

  String eth_call(TransactionCall call);

  String eth_newFilter(Filter filter);

  String eth_estimateGas(EthTransaction tx);

  String eth_gasPrice();
}
