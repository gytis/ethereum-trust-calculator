package gt.graph;

import java.util.HashMap;
import java.util.Map;

import gt.entities.User;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Trust level calculator using Neo4j shortest path algorithm in a meantime filtering out blocked users.
 */
@Component
public class TrustCalculator {

    /*
    It is recommended to have a node limit for the shortest path search. It's set to 50 here.
    Also, this query should include `WHERE NOT from.isBlocked AND NOT to.isBlocked` condition.
    However, it was producing different results on different Neo4j versions. Thus isValidAddress method implementation.
     */
    private static final String TRUST_QUERY = "MATCH (from:User {address: {from}}) WITH from " +
            "MATCH (to:User {address: {to}}), p=shortestPath((from)-[:TRANSFER*..50]->(to)) " +
            "WHERE ALL(x IN NODES(p)[1..-1] WHERE NOT x.blocked) " +
            "RETURN length(p)";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustCalculator.class);

    private final Session session;

    private final UsersRepository usersRepository;

    public TrustCalculator(Session session, UsersRepository usersRepository) {
        this.session = session;
        this.usersRepository = usersRepository;
    }

    public int getTrustLevel(String from, String to) {
        if (!isValidAddress(from) || !isValidAddress(to)) {
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

    private boolean isValidAddress(String address) {
        if (address == null) {
            return false;
        }

        User user = usersRepository.findByAddress(address);
        return user != null && !user.isBlocked();
    }

}
