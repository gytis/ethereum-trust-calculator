package gt.graph;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Component;

@Component
public class TrustCalculator {

    // TODO add depth limit to properties
    private static final String TRUST_QUERY =
            "MATCH (from:User {address: {from}}) WITH from MATCH (to:User {address: {to}}), p=shortestPath((from)-[:TRANSFER*..50]->(to)) RETURN length(p)";

    private final Session session;

    public TrustCalculator(Session session) {
        this.session = session;
    }

    public int getTrustLevel(String from, String to) {
        if (from == null || to == null) {
            return -1;
        }
        if (from.equals(to)) {
            return 0;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("to", to);

        Integer trustLevel = session.queryForObject(Integer.class, TRUST_QUERY, parameters);

        if (trustLevel == null) {
            return -1;
        }

        return trustLevel;
    }

}
