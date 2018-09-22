package gt.blockchain;

import java.math.BigInteger;

import gt.CalculatorProperties;
import gt.graph.UsersRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Observable;
import rx.Subscription;

@Component
public class TransactionObserversManager implements InitializingBean, DisposableBean {

    private final Web3j web3j;

    private final UsersRepository usersRepository;

    private final CalculatorProperties properties;

    private Subscription subscription;

    public TransactionObserversManager(Web3j web3j, UsersRepository usersRepository, CalculatorProperties properties) {
        this.web3j = web3j;
        this.usersRepository = usersRepository;
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        DefaultBlockParameter blockNumber =
                DefaultBlockParameter.valueOf(BigInteger.valueOf(properties.getFirstBlockNumber()));
        Observable<Transaction> observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(blockNumber);
        GenericTransactionObserver genericTransactionObserver = new GenericTransactionObserver(usersRepository);
        subscription = observable.subscribe(genericTransactionObserver);
    }

    @Override
    public void destroy() {
        subscription.unsubscribe();
    }
}
