package gt.graph;

import gt.entities.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UsersRepository extends Neo4jRepository<User, String> {

}
