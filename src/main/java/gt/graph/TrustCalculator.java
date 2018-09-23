package gt.graph;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrustCalculator {

    // TODO add depth limit to properties
    private static final String TRUST_QUERY = "MATCH (from:User {address: {from}}) WITH from " +
            "MATCH (to:User {address: {to}}), p=shortestPath((from)-[:TRANSFER*..50]->(to)) " +
            "WHERE ALL(x IN NODES(p)[1..-1] WHERE NOT x.blocked) " +
            "RETURN length(p)";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustCalculator.class);

    private final Session session;

    public TrustCalculator(Session session) {
        this.session = session;
    }

    public int getTrustLevel(String from, String to) {
        if (from == null || to == null) {
            LOGGER.debug("Trust level from '{}' to '{}' is '-1'", from, to);
            return -1;
        }
        if (from.equals(to)) {
            LOGGER.debug("Trust level from '{}' to '{}' is '0'", from, to);
            return 0;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("to", to);

        Integer trustLevel = session.queryForObject(Integer.class, TRUST_QUERY, parameters);

        if (trustLevel == null) {
            LOGGER.debug("Trust level from '{}' to '{}' is '-1'", from, to);
            return -1;
        }

        LOGGER.debug("Trust level from '{}' to '{}' is '{}'", from, to, trustLevel);
        return trustLevel;
    }

}
