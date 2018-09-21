package gt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

    private final TrustCalculator trustCalculator;

    public CalculatorController(TrustCalculator trustCalculator) {
        this.trustCalculator = trustCalculator;
    }

    @GetMapping
    public int getTrustLevel(@RequestParam("from") String from, @RequestParam("to") String to) {
        return trustCalculator.getTrustLevel(from, to);
    }

}

