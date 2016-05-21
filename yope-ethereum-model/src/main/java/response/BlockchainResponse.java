package response;

import io.yope.ethereum.model.Receipt;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Ethereum API response.
 */
public class BlockchainResponse {

    /**
     * Collection of receipts returned from the blockchain.
     */
    private Collection<Future<Receipt>> receipts;
}
