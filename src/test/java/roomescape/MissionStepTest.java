package roomescape;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

    @Value("${jwt.secret}")
    private String secretKey;
    @Test
    void 일단계() {
        Map<String, String> params = new HashMap<>();
        params.put("email", "admin@email.com");
        params.put("password", "password");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        String token = response.headers().get("Set-Cookie").getValue().split(";")[0].split("=")[1];

        assertThat(token).isNotBlank();
    }


    @Test
    void 이단계() {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", "admin@email.com");
        loginParams.put("password", "password");

        ExtractableResponse<Response> loginResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then().statusCode(200)
                .extract();

        String token = loginResponse.headers().get("Set-Cookie").getValue().split(";")[0].split("=")[1];

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2026-07-01");
        params.put("time", "4");
        params.put("theme", "1");

        ExtractableResponse<Response> adminResponse = RestAssured.given().log().all()
                .body(params)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertThat(adminResponse.statusCode()).isEqualTo(201);
        assertThat(adminResponse.jsonPath().getString("name")).isEqualTo("브라운");


        Map<String, String> userParams = new HashMap<>();
        userParams.put("date", "2026-07-02");
        userParams.put("time", "4");
        userParams.put("theme", "1");

        ExtractableResponse<Response> userResponse = RestAssured.given().log().all()
                .body(userParams)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertThat(userResponse.statusCode()).isEqualTo(201);
        assertThat(userResponse.jsonPath().getString("name")).isEqualTo("어드민");
    }

    @Test
    void 삼단계() {
        String brownToken = createToken("brown@email.com", "USER");

        RestAssured.given().log().all()
                .cookie("token", brownToken)
                .get("/admin")
                .then().log().all()
                .statusCode(403);

        String adminToken = createToken("admin@email.com", "ADMIN");

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    private String createToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", email.split("@")[0]);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("1")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
