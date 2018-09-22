package gt.controllers;

import gt.graph.TrustCalculator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CalculatorControllerTest {

    @Mock
    private TrustCalculator mockTrustCalculator;

    private CalculatorController calculatorController;

    @Before
    public void before() {
        calculatorController = new CalculatorController(mockTrustCalculator);
    }

    @Test
    public void shouldGetTrustLevel() {
        given(mockTrustCalculator.getTrustLevel("test1", "test2")).willReturn(10);

        int trustLevel = calculatorController.getTrustLevel("test1", "test2");

        assertThat(trustLevel).isEqualTo(10);
    }

}