package gt.blockchain;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class Erc20TransactionObserverTest {

    private static final List<String> TOPICS = Arrays.asList(
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
            "0x0000000000000000000000000123456789012345678901234567890123456789",
            "0x0000000000000000000000000123456789012345678901234567890123456789"
    );

    @Mock
    private Web3j mockWeb3j;

    @Mock
    private Request mockEthGetTransactionReceiptRequest;

    @Mock
    private EthGetTransactionReceipt mockEthGetTransactionReceipt;

    @Mock
    private TransactionReceipt mockTransactionReceipt;

    @Mock
    private Log mockLog;

    @Mock
    private UsersRepository mockUsersRepository;

    @Mock
    private Transaction mockTransaction;

    private Erc20TransactionObserver erc20TransactionObserver;

    @Before
    public void before() throws IOException {
        given(mockTransaction.getHash()).willReturn("testHash");
        given(mockWeb3j.ethGetTransactionReceipt("testHash"))
                .willReturn(mockEthGetTransactionReceiptRequest);
        given(mockEthGetTransactionReceiptRequest.send()).willReturn(mockEthGetTransactionReceipt);
        given(mockEthGetTransactionReceipt.getTransactionReceipt()).willReturn(Optional.of(mockTransactionReceipt));
        given(mockTransactionReceipt.getLogs()).willReturn(Collections.singletonList(mockLog));
        given(mockLog.getTopics()).willReturn(TOPICS);
        given(mockTransactionReceipt.getFrom()).willReturn("testA");

        erc20TransactionObserver = new Erc20TransactionObserver(mockWeb3j, mockUsersRepository);
    }

    @Test
    public void shouldHandleErc20Transaction() throws IOException {
        erc20TransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository).findByAddress("testA");
        verify(mockUsersRepository).findByAddress("0x0123456789012345678901234567890123456789");
        verify(mockUsersRepository, times(2)).save(any(User.class));
    }

    @Test
    public void shouldIgnoreNonErc20Transactions() {
        given(mockLog.getTopics()).willReturn(Collections.emptyList());

        erc20TransactionObserver.onNext(mockTransaction);

        verify(mockUsersRepository, times(0)).findByAddress(any(String.class));
        verify(mockUsersRepository, times(0)).save(any(User.class));
    }

}