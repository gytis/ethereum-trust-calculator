package gt.blockchain;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import gt.CalculatorProperties;
import gt.graph.UsersRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Observer;
import rx.Subscription;

/**
 * Register {@link GenericTransactionObserver} and {@link Erc20TransactionObserver} and make sure they're unsubscribed
 * once the application is closed.
 */
@Component
public class TransactionObserversManager implements InitializingBean, DisposableBean {

    private final Web3j web3j;

    private final UsersRepository usersRepository;

    private final CalculatorProperties properties;

    private Set<Subscription> subscriptions = new HashSet<>();

    public TransactionObserversManager(Web3j web3j, UsersRepository usersRepository, CalculatorProperties properties) {
        this.web3j = web3j;
        this.usersRepository = usersRepository;
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        subscribe(new GenericTransactionObserver(usersRepository));
        subscribe(new Erc20TransactionObserver(web3j, usersRepository));
    }

    @Override
    public void destroy() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    private void subscribe(Observer<Transaction> observer) {
        DefaultBlockParameter blockParameter =
                DefaultBlockParameter.valueOf(BigInteger.valueOf(properties.getFirstBlockNumber()));
        // We need a separate observable for each observer to make sure that all transactions are sent to it.
        Subscription subscription = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(blockParameter)
                .subscribe(observer);
        subscriptions.add(subscription);
    }
}
