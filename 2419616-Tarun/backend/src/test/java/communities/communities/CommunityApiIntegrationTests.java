package communities.communities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class CommunityApiIntegrationTests {

    @LocalServerPort
    private int port;

        private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createJoinAndPostFlowWorks() throws Exception {
        String createPayload = objectMapper.writeValueAsString(Map.of(
                "name", "API Test Community",
                "description", "Created from integration test",
                "category", "events"
        ));

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(url("/api/communities")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createPayload))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        JsonNode createdCommunity = objectMapper.readTree(createResponse.body());
        long communityId = createdCommunity.get("id").asLong();
        assertTrue(communityId > 0);
        assertEquals(1, createdCommunity.get("members").asInt());

        HttpRequest joinRequest = HttpRequest.newBuilder()
                .uri(URI.create(url("/api/communities/" + communityId + "/join")))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> joinResponse = httpClient.send(joinRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, joinResponse.statusCode());

        JsonNode joinedCommunity = objectMapper.readTree(joinResponse.body());
        assertEquals(2, joinedCommunity.get("members").asInt());

        String postPayload = objectMapper.writeValueAsString(Map.of(
                "author", "IntegrationTester",
                "text", "Hello from integration test"
        ));

        HttpRequest createPostRequest = HttpRequest.newBuilder()
                .uri(URI.create(url("/api/communities/" + communityId + "/posts")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postPayload))
                .build();

        HttpResponse<String> createPostResponse = httpClient.send(createPostRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createPostResponse.statusCode());

        JsonNode createdPost = objectMapper.readTree(createPostResponse.body());
        assertEquals("IntegrationTester", createdPost.get("author").asText());
        assertEquals("Hello from integration test", createdPost.get("text").asText());
        assertEquals(communityId, createdPost.get("communityId").asLong());

        HttpRequest listPostsRequest = HttpRequest.newBuilder()
                .uri(URI.create(url("/api/communities/" + communityId + "/posts")))
                .GET()
                .build();

        HttpResponse<String> listPostsResponse = httpClient.send(listPostsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listPostsResponse.statusCode());

        JsonNode posts = objectMapper.readTree(listPostsResponse.body());
        assertTrue(posts.isArray());
        assertTrue(posts.size() > 0);
        assertEquals("Hello from integration test", posts.get(0).get("text").asText());
    }
}
