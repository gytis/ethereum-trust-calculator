package gt.blockchain;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Observer;

public abstract class AbstractTransactionObserver implements Observer<Transaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionObserver.class);

    private final UsersRepository usersRepository;

    public AbstractTransactionObserver(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void onCompleted() {
        // Ignore
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.warn(throwable.getMessage(), throwable);
    }

    protected void saveTransfer(String from, String to) {
        if (from == null || to == null || from.equals(to)) {
            return;
        }

        User userFrom = getOrCreateUser(from);
        User userTo = getOrCreateUser(to);

        if (userFrom.getTransfers().contains(userTo)) {
            return;
        }
        userFrom.addTransfer(userTo);

        usersRepository.save(userFrom);
        usersRepository.save(userTo);

        LOGGER.debug("Added '{}' to '{}' transfers", userTo, userFrom);
    }

    private User getOrCreateUser(String address) {
        User user = usersRepository.findByAddress(address);
        if (user != null) {
            return user;
        }

        return new User(address);
    }

}
