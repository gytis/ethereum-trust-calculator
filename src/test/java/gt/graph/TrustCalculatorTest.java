package gt.graph;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.neo4j.ogm.session.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TrustCalculatorTest {

    @Mock
    private Session mockSession;

    private TrustCalculator trustCalculator;

    @Before
    public void before() {
        trustCalculator = new TrustCalculator(mockSession);
    }

    @Test
    public void shouldGetTrustLevelBetweenEqualAddresses() {
        int trustLevel = trustCalculator.getTrustLevel("test1", "test1");

        assertThat(trustLevel).isEqualTo(0);
        verify(mockSession, times(0)).queryForObject(eq(Integer.class), anyString(), anyMap());
    }

    @Test
    public void shouldGetTrustLevelBetweenConnectedAddresses() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("from", "test1");
        parameters.put("to", "test2");

        given(mockSession.queryForObject(eq(Integer.class), anyString(), eq(parameters))).willReturn(1);

        int trustLevel = trustCalculator.getTrustLevel("test1", "test2");

        assertThat(trustLevel).isEqualTo(1);
        verify(mockSession).queryForObject(eq(Integer.class), anyString(), anyMap());
    }

    @Test
    public void shouldGetTrustLevelBetweenUnconnectedAddresses() {
        int trustLevel = trustCalculator.getTrustLevel("test1", "test2");

        assertThat(trustLevel).isEqualTo(-1);
        verify(mockSession).queryForObject(eq(Integer.class), anyString(), anyMap());
    }

    @Test
    public void shouldGetTrustLevelBetweenInvalidAddresses() {
        int trustLevel = trustCalculator.getTrustLevel(null, null);

        assertThat(trustLevel).isEqualTo(-1);
        verify(mockSession, times(0)).queryForObject(eq(Integer.class), anyString(), anyMap());
    }

}