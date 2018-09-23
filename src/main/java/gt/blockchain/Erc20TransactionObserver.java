package gt.blockchain;

import java.io.IOException;
import java.util.List;

import gt.graph.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * Observer listening for all transactions and filtering the ERC20 ones.
 * For all ERC20 transactions it extracts transfer information from the logs and stores them in the database.
 */
public class Erc20TransactionObserver extends AbstractTransactionObserver {

    /**
     * ERC20 logs have must have the following topic (a hash of Transfer(address,address,uint256)).
     */
    private static final String ERC20_TOPIC_HASH = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    private static final Logger LOGGER = LoggerFactory.getLogger(Erc20TransactionObserver.class);

    private final Web3j web3j;

    public Erc20TransactionObserver(Web3j web3j, UsersRepository usersRepository) {
        super(usersRepository);
        this.web3j = web3j;
    }

    @Override
    public void onNext(Transaction transaction) {
        LOGGER.debug("handling transaction '{}'", transaction.getHash());
        try {
            // Get transaction receipt, which contains transaction logs, and verify if it's ERC20
            web3j.ethGetTransactionReceipt(transaction.getHash())
                    .send()
                    .getTransactionReceipt()
                    .ifPresent(this::handleTransactionReceipt);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private void handleTransactionReceipt(TransactionReceipt transactionReceipt) {
        transactionReceipt.getLogs()
                .stream()
                .map(Log::getTopics)
                .filter(this::isErc20TopicsList)
                .map(this::getErc20DestinationAddress)
                .forEach(to -> saveTransfer(transactionReceipt.getFrom(), to));
    }

    private boolean isErc20TopicsList(List<String> topics) {
        // ERC20 transfers have a log with three topics: hash, from address, to address
        return topics != null
                && topics.size() == 3
                && ERC20_TOPIC_HASH.equals(topics.get(0))
                && topics.get(2).length() >= 40;
    }

    private String getErc20DestinationAddress(List<String> topics) {
        String value = topics.get(2);
        // Address in ERC20 topic is prefixed with zeros. Remove them and put back the hash prefix.
        return "0x" + value.substring(value.length() - 40);
    }
}
