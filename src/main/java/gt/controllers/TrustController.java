package gt.controllers;

import gt.graph.TrustCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Trust resource controller exposing functionality of the {@link TrustController}.
 */
@RestController
public class TrustController {

    private final TrustCalculator trustCalculator;

    public TrustController(TrustCalculator trustCalculator) {
        this.trustCalculator = trustCalculator;
    }

    /**
     * Calculate a trust level between two users.
     * @param from address of the start user
     * @param to address of the end user
     * @return Trust level
     */
    @GetMapping(path = "/trust")
    public int getTrustLevel(@RequestParam String from, @RequestParam String to) {
        return trustCalculator.getTrustLevel(from, to);
    }

}

