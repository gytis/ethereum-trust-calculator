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
public class EthereumTrustCalculatorApplicationTests { // TODO should be IT

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
                .get()
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
                .get()
                .then()
                .statusCode(200)
                .body(is("2"));
    }

    @Test
    public void shouldGetTrustLevelBetweenUnrelatedUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "c")
                .queryParam("to", "a")
                .get()
                .then()
                .statusCode(200)
                .body(is("-1"));
    }

    @Test
    public void shouldGetTrustLevelBetweenNonexistentUsers() {
        given()
                .baseUri(baseUri)
                .queryParam("from", "x")
                .queryParam("to", "z")
                .get()
                .then()
                .statusCode(200)
                .body(is("-1"));
    }


}
