package gt.blockchain;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.protocol.core.methods.response.Transaction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GenericTransactionObserverTest {

    @Mock
    private UsersRepository mockUsersRepository;

    @Mock
    private Transaction mockTransaction;

    private GenericTransactionObserver genericTransactionObserver;

    @Before
    public void before() {
        genericTransactionObserver = new GenericTransactionObserver(mockUsersRepository);
    }

    @Test
    public void shouldHandleNewUsers() {
        given(mockTransaction.getFrom()).willReturn("testA");
        given(mockTransaction.getTo()).willReturn("testB");

        genericTransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository).findByAddress("testA");
        verify(mockUsersRepository).findByAddress("testB");
        verify(mockUsersRepository, times(2)).save(any(User.class));
    }

    @Test
    public void shouldHandleExistingUsers() {
        User userA = new User("testA");
        User userB = new User("testB");

        given(mockUsersRepository.findByAddress("testA")).willReturn(userA);
        given(mockUsersRepository.findByAddress("testB")).willReturn(userB);
        given(mockTransaction.getFrom()).willReturn("testA");
        given(mockTransaction.getTo()).willReturn("testB");

        genericTransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository).save(userA);
        verify(mockUsersRepository).save(userB);
    }

    @Test
    public void shouldIgnoreNullAddresses() {
        genericTransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository, times(0)).findByAddress(any(String.class));
        verify(mockUsersRepository, times(0)).save(any(User.class));
    }

    @Test
    public void shouldIgnoreEqualAddresses() {
        given(mockTransaction.getTo()).willReturn("testA");

        genericTransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository, times(0)).findByAddress(any(String.class));
        verify(mockUsersRepository, times(0)).save(any(User.class));
    }

}