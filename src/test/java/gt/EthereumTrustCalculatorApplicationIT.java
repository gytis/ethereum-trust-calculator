package gt;

import java.util.Arrays;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EthereumTrustCalculatorApplicationIT {

    @Autowired
    private UsersRepository usersRepository;

    @Value("${local.server.port}")
    private int port;

    private String baseUri;

    @Before
    public void before() {
        baseUri = String.format("http://localhost:%d", port);

        User userA = new User("a");
        User userB = new User("b");
        User userC = new User("c");

        userA.addTransfer(userB);
        userB.addTransfer(userC);

        usersRepository.saveAll(Arrays.asList(userA, userB, userC));
    }

    @After
    public void after() {
        usersRepository.deleteAll();
    }

    @Test
    public void shouldGetTrustLevelBetweenAdjacentUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "a")
                .queryParam("to", "b")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("1"));
    }

    @Test
    public void shouldGetTrustLevelBetweenRelatedUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "a")
                .queryParam("to", "c")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("2"));
    }

    @Test
    public void shouldNotGetTrustLevelBetweenUnrelatedUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "c")
                .queryParam("to", "a")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("-1"));
    }

    @Test
    public void shouldNotGetTrustLevelBetweenNonexistentUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "x")
                .queryParam("to", "z")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("-1"));
    }

    @Test
    public void shouldNotGetTrustLevelThroughBlockedUser() {
        given()
                .baseUri(baseUri)
                .put("/users/b/block")
                .then()
                .statusCode(204);

        given()
                .baseUri(baseUri)
                .queryParam("from", "a")
                .queryParam("to", "c")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("-1"));
    }

    @Test
    public void shouldNotGetTrustLevelIfOriginIsBlocked() {
        given()
                .baseUri(baseUri)
                .put("/users/a/block")
                .then()
                .statusCode(204);

        given()
                .baseUri(baseUri)
                .queryParam("from", "a")
                .queryParam("to", "c")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("-1"));
    }

    @Test
    public void shouldSkipBlockedUsers() {
        User userA = usersRepository.findByAddress("a");
        User userC = usersRepository.findByAddress("c");
        User userD = new User("d");
        User userE = new User("e");
        userE.setBlocked(true);

        userC.addTransfer(userD);
        userA.addTransfer(userE);
        userE.addTransfer(userD);

        usersRepository.saveAll(Arrays.asList(userA, userC, userD, userE));

        given()
                .baseUri(baseUri)
                .queryParam("from", "a")
                .queryParam("to", "d")
                .get("/trust")
                .then()
                .statusCode(200)
                .body(is("3"));
    }

}
