package gt.blockchain;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Observer;

public class GenericTransactionObserver implements Observer<Transaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTransactionObserver.class);

    private final UsersRepository usersRepository;

    public GenericTransactionObserver(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void onNext(Transaction transaction) {
        String addressFrom = transaction.getFrom();
        String addressTo = transaction.getTo();

        if (addressFrom == null || addressTo == null || addressFrom.equals(addressTo)) {
            return;
        }

        User userFrom = getOrCreateUser(addressFrom);
        User userTo = getOrCreateUser(addressTo);

        userFrom.addTransfer(userTo);

        usersRepository.save(userFrom);
        usersRepository.save(userTo);

        LOGGER.debug("Added '{}' to '{}' transfers", userTo, userFrom);
    }

    @Override
    public void onCompleted() {
        LOGGER.debug("completed");
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.warn(throwable.getMessage(), throwable);
    }

    private User getOrCreateUser(String address) {
        User user = usersRepository.findByAddress(address);
        if (user != null) {
            return user;
        }

        return new User(address);
    }
}
