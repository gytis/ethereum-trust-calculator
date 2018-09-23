package gt.blockchain;

import gt.graph.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * Observer listening for all complete transactions and storing both transfer parties to the database.
 */
public class GenericTransactionObserver extends AbstractTransactionObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTransactionObserver.class);

    public GenericTransactionObserver(UsersRepository usersRepository) {
        super(usersRepository);
    }

    @Override
    public void onNext(Transaction transaction) {
        LOGGER.debug("handling transaction '{}'", transaction.getHash());
        saveTransfer(transaction.getFrom(), transaction.getTo());
    }

}
