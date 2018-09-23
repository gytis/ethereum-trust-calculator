package gt.controllers;

import gt.graph.TrustCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrustController {

    private final TrustCalculator trustCalculator;

    public TrustController(TrustCalculator trustCalculator) {
        this.trustCalculator = trustCalculator;
    }

    @GetMapping(path = "/trust")
    public int getTrustLevel(@RequestParam String from, @RequestParam String to) {
        return trustCalculator.getTrustLevel(from, to);
    }

}

