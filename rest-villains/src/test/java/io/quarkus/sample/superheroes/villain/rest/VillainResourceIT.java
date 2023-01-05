package io.quarkus.sample.superheroes.villain.rest;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.sample.superheroes.villain.Power;
import io.quarkus.sample.superheroes.villain.Villain;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusIntegrationTest
@TestMethodOrder(OrderAnnotation.class)
public class VillainResourceIT {
	private static final int DEFAULT_ORDER = 0;
	private static final String DEFAULT_NAME = "Super Chocolatine";
	private static final String UPDATED_NAME = DEFAULT_NAME + " (updated)";
	private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
	private static final String UPDATED_OTHER_NAME = DEFAULT_OTHER_NAME + " (updated)";
	private static final String DEFAULT_PICTURE = "super_chocolatine.png";
	private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
	private static final Set<Power> DEFAULT_POWERS = Set.of(new Power("chocolat", "Base", 10, "", "does not eat pain au chocolat"));
	private static final Set<Power> UPDATED_POWERS = Set.of(new Power("dark chocolat", "Base", 99, "", "does not eat pain au dark chocolat"));
	private static final int DEFAULT_LEVEL = 42;
	private static final int UPDATED_LEVEL = DEFAULT_LEVEL + 1;
	private static final double LEVEL_MULTIPLIER = 0.5;

	private static final int NB_VILLAINS = 4; //100
	private static String villainId;

	@Test
	@Order(DEFAULT_ORDER)
	public void helloEndpoint() {
		given()
			.when()
				.accept(TEXT_PLAIN)
				.get("/api/villains/hello")
			.then()
				.statusCode(200)
				.body(is("Hello Villain Resource"));
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotGetUnknownVillain() {
		get("/api/villains/{id}", new Random().nextLong())
			.then().statusCode(NOT_FOUND.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldGetRandomVillainFound() {
		get("/api/villains/random")
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(JSON)
				.body("$", notNullValue());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotAddInvalidItem() {
		var villain = new Villain();
		villain.name = null;
		villain.otherName = DEFAULT_OTHER_NAME;
		villain.picture = DEFAULT_PICTURE;
		villain.addAllPowers(DEFAULT_POWERS);
		villain.level = 0;

		given()
			.when()
				.body(villain)
				.contentType(JSON)
				.accept(JSON)
				.post("/api/villains")
			.then()
				.statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotFullyUpdateInvalidItem() {
		var villain = new Villain();
		villain.id = 1L;
		villain.name = null;
		villain.otherName = UPDATED_OTHER_NAME;
		villain.picture = UPDATED_PICTURE;
		villain.addAllPowers(UPDATED_POWERS);
		villain.level = 0;

		given()
			.when()
				.body(villain)
				.contentType(JSON)
				.accept(JSON)
				.put("/api/villains/{id}", villain.id)
			.then()
				.statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotPartiallyUpdateInvalidItem() {
		var villain = new Villain();
		villain.id = 1L;
		villain.name = null;
		villain.otherName = UPDATED_OTHER_NAME;
		villain.picture = UPDATED_PICTURE;
		villain.addAllPowers(UPDATED_POWERS);
		villain.level = 0;

		given()
			.when()
				.body(villain)
				.contentType(JSON)
				.accept(JSON)
				.patch("/api/villains/{id}", villain.id)
			.then()
				.statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotAddNullItem() {
		given()
			.when()
				.contentType(JSON)
				.accept(JSON)
				.post("/api/villains")
			.then()
				.statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotFullyUpdateNullItem() {
		given()
				.when()
          .contentType(JSON)
          .accept(JSON)
          .body("")
        .put("/api/villains/{id}", 1L)
          .then()
          .statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotFullyUpdateNotFoundItem() {
		Villain villain = new Villain();
		villain.id = -1L;
		villain.name = UPDATED_NAME;
		villain.otherName = UPDATED_OTHER_NAME;
		villain.picture = UPDATED_PICTURE;
		villain.addAllPowers(UPDATED_POWERS);
		villain.level = UPDATED_LEVEL;

		given()
			.when()
				.contentType(JSON)
				.accept(JSON)
				.body(villain)
			.put("/api/villains/{id}", -1L)
				.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotPartiallyUpdateNullItem() {
		given()
			.when()
				.contentType(JSON)
				.accept(JSON)
				.body("")
				.patch("/api/villains/{id}", 1L)
			.then()
				.statusCode(BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void shouldNotPartiallyUpdateNotFoundItem() {
		Villain villain = new Villain();
		villain.picture = DEFAULT_PICTURE;
		villain.addAllPowers(DEFAULT_POWERS);

		given()
			.when()
				.contentType(JSON)
				.accept(JSON)
				.body(villain)
				.patch("/api/villains/{id}", -1L)
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}

  @Test
  @Order(DEFAULT_ORDER)
  public void shouldNotGetAnyVillainsThatDontMatchFilterCriteria() {
    given()
      .when()
        .queryParam("name_filter", "iooi90904890358349 8890re9ierkjlk;sdf098w459idxflkjdfjoiio4ue")
        .get("/api/villains")
      .then()
        .statusCode(OK.getStatusCode())
        .body("size()", is(0));
  }

  @Test
  @Order(DEFAULT_ORDER)
  public void shouldGetVillainsThatMatchFilterCriteria() {
    given()
      .when()
        .queryParam("name_filter", "Rao")
        .get("/api/villains")
      .then()
        .statusCode(OK.getStatusCode())
        .body("size()", is(1)) //2
        .body("[0].name", is("Rao"));
        // .body("[0].name", is("Darth Sidious"))
        // .body("[1].name", is("Darth Vader"));
  }

	@Test
	@Order(DEFAULT_ORDER + 1)
	public void shouldGetInitialItems() {
		get("/api/villains")
			.then()
			.statusCode(OK.getStatusCode())
			.contentType(JSON)
			.body("size()", is(NB_VILLAINS));
	}

	@Test
	@Order(DEFAULT_ORDER + 2)
	public void shouldAddAnItem() {
		Villain villain = new Villain();
		villain.name = DEFAULT_NAME;
		villain.otherName = DEFAULT_OTHER_NAME;
		villain.picture = DEFAULT_PICTURE;
		villain.addAllPowers(DEFAULT_POWERS);
		villain.level = DEFAULT_LEVEL;

		String location = given()
			.when()
				.body(villain)
				.contentType(JSON)
				.accept(JSON)
				.post("/api/villains")
			.then()
				.statusCode(CREATED.getStatusCode())
				.extract()
				.header(HttpHeaders.LOCATION);

		assertThat(location)
			.isNotBlank()
			.contains("/api/villains");

		// Stores the id
		String[] segments = location.split("/");
		villainId = segments[segments.length - 1];

		assertThat(villainId)
			.isNotNull();

		Villain villainResponse = get("/api/villains/{id}", villainId)
			.then()
				.contentType(JSON)
				.statusCode(OK.getStatusCode())
				.and()
				.extract().body()
				.as(new TypeRef<Villain>() {});
				
		assertThat(villainResponse, notNullValue());
		assertThat(villainResponse.name, is(DEFAULT_NAME));
		assertThat(villainResponse.otherName, is(DEFAULT_OTHER_NAME));
		assertThat(villainResponse.level, is((int)(DEFAULT_LEVEL * LEVEL_MULTIPLIER)));
		assertThat(villainResponse.picture, is(DEFAULT_PICTURE));
		assertThat(villainResponse.getPowers(), equalTo(DEFAULT_POWERS));

		verifyNumberOfVillains(NB_VILLAINS + 1);
	}

  private static void verifyNumberOfVillains(int expected) {
    get("/api/villains")
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(JSON)
			  .body("size()", is(expected));
  }

	@Test
	@Order(DEFAULT_ORDER + 3)
	public void shouldFullyUpdateAnItem() {
		Villain villain = new Villain();
		villain.id = Long.valueOf(villainId);
		villain.name = UPDATED_NAME;
		villain.otherName = UPDATED_OTHER_NAME;
		villain.picture = UPDATED_PICTURE;
		villain.addAllPowers(UPDATED_POWERS);
		villain.level = UPDATED_LEVEL;

		given()
			.when()
				.body(villain)
				.contentType(JSON)
				.accept(JSON)
				.put("/api/villains/{id}", villain.id)
			.then()
				.statusCode(NO_CONTENT.getStatusCode())
				.body(blankOrNullString());

    verifyNumberOfVillains(NB_VILLAINS +1);
	}

	@Test
	@Order(DEFAULT_ORDER + 4)
	public void shouldPartiallyUpdateAnItem() {
		Villain villain = new Villain();
		villain.picture = DEFAULT_PICTURE;
		villain.addAllPowers(DEFAULT_POWERS);

		Villain villainResponse = 
			given()
				.when()
					.body(villain)
					.contentType(JSON)
					.accept(JSON)
					.patch("/api/villains/{id}", villainId)
				.then()
					.statusCode(OK.getStatusCode())
					.contentType(JSON)
					.and()
					.extract().body()
					.as(new TypeRef<Villain>() {});
				
		assertThat(villainResponse, notNullValue());
		assertThat(villainResponse.id, is(Long.parseLong(villainId)));
		assertThat(villainResponse.name, is(UPDATED_NAME));
		assertThat(villainResponse.otherName, is(UPDATED_OTHER_NAME));
		assertThat(villainResponse.level, is(UPDATED_LEVEL));
		assertThat(villainResponse.picture, is(DEFAULT_PICTURE));
		assertThat(villainResponse.getPowers(), equalTo(DEFAULT_POWERS));

    verifyNumberOfVillains(NB_VILLAINS +1);
	}

	@Test
	@Order(DEFAULT_ORDER + 5)
	public void shouldDeleteVillain() {
		delete("/api/villains/{id}", villainId)
			.then()
				.statusCode(NO_CONTENT.getStatusCode())
				.body(blankOrNullString());

    verifyNumberOfVillains(NB_VILLAINS);
	}

	@Test
	@Order(DEFAULT_ORDER + 6)
	public void shouldDeleteAllVillains() {
		delete("/api/villains/")
			.then()
				.statusCode(NO_CONTENT.getStatusCode())
				.body(blankOrNullString());

    verifyNumberOfVillains(0);
	}

	@Test
	@Order(DEFAULT_ORDER + 7)
	public void shouldGetRandomVillainNotFound() {
		given()
			.when().get("/api/villains/random")
			.then().statusCode(NOT_FOUND.getStatusCode());
	}

  @Test
  @Order(DEFAULT_ORDER + 8)
  public void shouldReplaceAllVillains() {
    var v1 = new Villain();
		v1.name = DEFAULT_NAME;
		v1.otherName = DEFAULT_OTHER_NAME;
		v1.picture = DEFAULT_PICTURE;
		v1.addAllPowers(DEFAULT_POWERS);
		v1.level = DEFAULT_LEVEL;

    var v2 = new Villain();
    v2.name = UPDATED_NAME;
    v2.otherName = UPDATED_OTHER_NAME;
    v2.picture = UPDATED_PICTURE;
		v2.addAllPowers(UPDATED_POWERS);
    v2.level = UPDATED_LEVEL;

		given()
			.when()
				.body(v1)
				.contentType(JSON)
				.accept(JSON)
				.post("/api/villains")
			.then()
				.statusCode(CREATED.getStatusCode())
				.header(HttpHeaders.LOCATION, containsString("/api/villains"));

    verifyNumberOfVillains(1);

    given()
			.when()
				.body(List.of(v1, v2))
				.contentType(JSON)
				.accept(JSON)
				.put("/api/villains")
			.then()
				.statusCode(CREATED.getStatusCode())
				.header(HttpHeaders.LOCATION, endsWith("/api/villains"));

    List<Villain> villainsResponse = 
			get("/api/villains")
				.then()
					.statusCode(OK.getStatusCode())
					.contentType(JSON)
				.and()
					.extract().body()
					.as(new TypeRef<List<Villain>>() {});
			
		assertThat(villainsResponse, notNullValue());
		assertThat(villainsResponse.size(), is(2));
		assertThat(villainsResponse.get(0).name, is(DEFAULT_NAME));
		assertThat(villainsResponse.get(0).otherName, is(DEFAULT_OTHER_NAME));
		assertThat(villainsResponse.get(0).level, is(DEFAULT_LEVEL));
		assertThat(villainsResponse.get(0).picture, is(DEFAULT_PICTURE));
		assertThat(villainsResponse.get(0).getPowers(), equalTo(DEFAULT_POWERS));
		assertThat(villainsResponse.get(1).name, is(UPDATED_NAME));
		assertThat(villainsResponse.get(1).otherName, is(UPDATED_OTHER_NAME));
		assertThat(villainsResponse.get(1).level, is(UPDATED_LEVEL));
		assertThat(villainsResponse.get(1).picture, is(UPDATED_PICTURE));
		assertThat(villainsResponse.get(1).getPowers(), equalTo(UPDATED_POWERS));
  }

	@Test
	public void shouldPingOpenAPI() {
		given()
			.when()
				.accept(JSON)
				.get("/q/openapi")
			.then()
				.statusCode(OK.getStatusCode());
	}
}
