import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.RepeatedTest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import external.MockAuthenticationService;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class MockAuthenticationServiceTest {
    private MockAuthenticationService authenticationService;
    private JSONArray users;
    private Random random;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        authenticationService = new MockAuthenticationService();
        random = new Random();
        URL dataPath = getClass().getResource("/MockUserDataGroups4.json");
        Objects.requireNonNull(dataPath);
        File dataFile = Paths.get(dataPath.toURI()).toFile();
        users = (JSONArray) new JSONParser().parse(new FileReader(dataFile));
    }

    @DisplayName("Valid User Test")
    @RepeatedTest(5)
    public void validUserTest(){
        JSONObject user = (JSONObject) users.get(random.nextInt(users.size()));
        String username = user.get("username").toString(),
                password = user.get("password").toString();

        assertEquals(user.toJSONString(), authenticationService.login(username, password));
    }

    @DisplayName("Type Error in Username")
    @RepeatedTest(5)
    public void typoInUsernameTest(){
        JSONObject user = (JSONObject) users.get(random.nextInt(users.size()));
        String username = user.get("username").toString() + "#",
                password = user.get("password").toString();

        HashMap<String, String> errorObj = new HashMap<>();
        errorObj.put("error", "Wrong username or password");

        assertEquals(new JSONObject(errorObj).toJSONString(), authenticationService.login(username, password));
    }

    @DisplayName("Type Error in Password")
    @RepeatedTest(5)
    public void typoInPasswordTest(){
        JSONObject user = (JSONObject) users.get(random.nextInt(users.size()));
        String username = user.get("username").toString(),
                password = "/" + user.get("password").toString();

        HashMap<String, String> errorObj = new HashMap<>();
        errorObj.put("error", "Wrong username or password");

        assertEquals(new JSONObject(errorObj).toJSONString(), authenticationService.login(username, password));
    }

    @DisplayName("Invalid User Test")
    @Test
    public void invalidUserTest(){
        String username = "SkeletonUsername",
                password = "I work everywhere";

        HashMap<String, String> errorObj = new HashMap<>();
        errorObj.put("error", "Wrong username or password");

        assertEquals(new JSONObject(errorObj).toJSONString(), authenticationService.login(username, password));
    }

}
