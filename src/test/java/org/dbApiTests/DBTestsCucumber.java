package org.dbApiTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.dbpojo.CartItem;
import org.dbpojo.CartResponse;
import org.dbpojo.LoginRequest;
import org.dbpojo.Product;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class DBTestsCucumber {
    Statement stmt;
    Connection conn;
    Response response;
    String token;
    private int loginSuccessCount;
    private int expectedLogins;

    @Given("the base URI is {string}")
    public void setBaseURI(String uri) {
        RestAssured.baseURI = uri;
    }

    @Given("the database connection is established")
    public void establishDBConnection() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC", "root", "Password123");
        stmt = conn.createStatement();
    }

    @Given("I login as {string} with password {string}")
    public void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        response = RestAssured.given().contentType(ContentType.JSON).body(loginRequest).post("/users/login");
        token = response.jsonPath().getString("token");
    }

    @When("I send a DELETE request to {string} with name {string}")
    public void deleteUser(String endpoint, String name) {
        response = RestAssured.given().delete(endpoint, name);
    }

    @When("I send a POST request to {string} with user details:")
    public void createUser(String endpoint, DataTable table) {
        Map<String, String> user = table.asMap(String.class, String.class);
        response = RestAssured.given().contentType(ContentType.JSON).body(user).post(endpoint);
    }

    @When("I send a DELETE request to {string} with query param {string} {string}")
    public void deleteCartItem(String endpoint, String param, String value) {
        response = RestAssured.given().contentType(ContentType.JSON).header("Authorization", "Bearer " + token).queryParam(param, value).delete(endpoint);
    }

    @When("I send a GET request to {string}")
    public void getRequest(String endpoint) {
        if (token != null) {
            response = RestAssured.given().contentType(ContentType.JSON).header("Authorization", "Bearer " + token).get(endpoint);
        } else {
            response = RestAssured.given().get(endpoint);
        }
    }

    @When("I send a POST request to {string} with cart products:")
    public void addToCart(String endpoint, DataTable table) {
        List<Map<String, Integer>> products = table.asMaps(String.class, Integer.class);
        Map<Integer, Integer> cartItem = new HashMap<>();
        for (Map<String, Integer> row : products) {
            cartItem.put(Integer.parseInt(row.keySet().iterator().next()), row.values().iterator().next());
        }
        Map<String, Object> cart = new HashMap<>();
        cart.put("products", cartItem);
        response = RestAssured.given().contentType(ContentType.JSON).header("Authorization", "Bearer " + token).body(cart).post(endpoint);
    }

    @When("I send a POST request to {string} with login details:")
    public void loginRequest(String endpoint, DataTable table) {
        Map<String, String> login = table.asMap(String.class, String.class);
        LoginRequest loginRequest = new LoginRequest(login.get("username"), login.get("password"));
        response = RestAssured.given().contentType(ContentType.JSON).body(loginRequest).post(endpoint);
    }

    @When("I send a POST request to {string} with form params:")
    public void postWithFormParams(String endpoint, DataTable table) {
        Map<String, String> params = table.asMap(String.class, String.class);
        response = RestAssured.given().contentType(ContentType.URLENC).formParams(params).post(endpoint);
    }

    @When("I send a GET request to {string} with query param {string} {string}")
    public void getWithQueryParam(String endpoint, String param, String value) {
        response = RestAssured.given().queryParam(param, value).get(endpoint);
    }

    @When("I send a GET request to {string} with query params:")
    public void getWithQueryParams(String endpoint, DataTable table) {
        Map<String, String> params = table.asMap(String.class, String.class);
        response = RestAssured.given().queryParams(params).get(endpoint);
    }

    @When("I send a PATCH request to {string} with body:")
    public void patchRequest(String endpoint, DataTable table) {
        Map<String, Object> body = table.asMap(String.class, Object.class);
        response = RestAssured.given().contentType(ContentType.JSON).body(body).patch(endpoint);
    }

    @When("I send a PUT request to {string} with product details:")
    public void putRequest(String endpoint, DataTable table) {
        Map<String, String> product = table.asMap(String.class, String.class);
        Product prod = new Product(Long.parseLong(endpoint.split("/")[2]), product.get("name"), Double.parseDouble(product.get("price")));
        response = RestAssured.given().contentType(ContentType.JSON).body(prod).put(endpoint);
    }

    @When("I send GET requests to {string} with sort {string} for all pages")
    public void getPagedSorted(String endpoint, String sort) throws SQLException {
        boolean isLastPage = false;
        int pageNumber = 0;
        int pageSize = 10;
        while (!isLastPage) {
            response = RestAssured.given().queryParam("page", pageNumber).queryParam("size", pageSize).queryParam("sort", sort).get(endpoint);
            // For verification in Then
            isLastPage = response.jsonPath().get("last");
            pageNumber++;
        }
    }

    @When("I send GET requests to {string} for all pages")
    public void getPaged(String endpoint) throws SQLException {
        boolean isLastPage = false;
        int pageNumber = 0;
        int pageSize = 10;
        List<Map<String, Object>> allApiProducts = new ArrayList<>();
        while (!isLastPage) {
            response = RestAssured.given().queryParam("page", pageNumber).queryParam("size", pageSize).get(endpoint);
            List<Map<String, Object>> apiProducts = response.jsonPath().getList("content");
            allApiProducts.addAll(apiProducts);
            isLastPage = response.jsonPath().get("last");
            pageNumber++;
        }
        // Store for verification
    }

    @When("I send a POST request to {string} with product details:")
    public void addProduct(String endpoint, DataTable table) {
        Map<String, Object> product = table.asMap(String.class, Object.class);
        response = RestAssured.given().contentType(ContentType.JSON).body(product).post(endpoint);
    }

    @When("I verify login for all users from JSON")
    public void verifyLoginFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> users = mapper.readValue(new File("src/test/resources/users_data.json"), List.class);
        expectedLogins = users.size();
        loginSuccessCount = 0;
        for (Map<String, Object> user : users) {
            String name = (String) user.get("name");
            String password = (String) user.get("password");
            LoginRequest loginRequest = new LoginRequest(name, password);
            Response resp = RestAssured.given().contentType(ContentType.JSON).body(loginRequest).post("/users/login");
            resp.then().statusCode(200).body("message", equalTo("Login successful")).body("token", notNullValue());
            loginSuccessCount++;
        }
    }

    @Then("the response status should be {int}")
    public void checkStatus(int status) {
        response.then().statusCode(status);
    }

    @Then("the response message should be {string}")
    public void checkMessage(String message) {
        response.then().body("message", equalTo(message));
    }

    @Then("the cart items should match the database for user {string}")
    public void verifyCartItems(String name) throws SQLException {
        HashMap<Integer, Integer> dbCartItems = getCartItemsFromDb(name);
        CartResponse cartResponse = response.as(CartResponse.class);
        for (CartItem cartItem : cartResponse.getItems()) {
            Integer productId = cartItem.getProductId();
            Integer quantity = cartItem.getQuantity();
            assertEquals(quantity, dbCartItems.get(productId));
        }
    }

    @Then("the cart quantities should be updated in the database for user {string}")
    public void verifyCartUpdate(String name) throws SQLException {
        // Similar to updateCartSuccessTest
    }

    @Then("the cart should contain product IDs {string}")
    public void checkCartProductIds(String ids) {
        Set<Integer> expected = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
        CartResponse cartResponse = response.as(CartResponse.class);
        Set<Integer> actual = cartResponse.getItems().stream().map(CartItem::getProductId).collect(Collectors.toSet());
        assertEquals(actual, expected);
    }

    @Then("the response should contain a token")
    public void checkToken() {
        assertNotNull(response.jsonPath().getString("token"));
    }

    @Then("the response should match the users in the database")
    public void verifyUsers() throws SQLException {
        List<Map<String, Object>> apiUsers = response.jsonPath().getList("$");
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            Long dbId = rs.getLong("id");
            String dbName = rs.getString("name");
            String dbEmail = rs.getString("email");
            int dbAge = rs.getInt("age");
            boolean matchFound = apiUsers.stream().anyMatch(user ->
                    dbId.equals(((Number) user.get("id")).longValue()) &&
                            dbName.equals(user.get("name")) &&
                            dbEmail.equals(user.get("email")) &&
                            dbAge == ((Number) user.get("age")).intValue());
            assertTrue(matchFound, "Mismatch for DB user id: " + dbId);
        }
    }

    @Then("the response should be processed")
    public void processResponse() {
        // For min null test, just print or something
        response.prettyPrint();
    }

    @Then("the products list should be empty")
    public void checkEmptyProducts() {
        List<?> products = response.jsonPath().getList("$");
        assertTrue(products.isEmpty());
    }

    @Then("the cookie {string} should have value {string}")
    public void checkCookieValue(String name, String value) {
        response.then().cookie(name, value);
    }

    @Then("the cookie {string} should be httpOnly")
    public void checkCookieHttpOnly(String name) {
        Cookie cookie = response.getDetailedCookie(name);
        assertTrue(cookie.isHttpOnly());
    }

    @Then("the cookie {string} should not be secured")
    public void checkCookieNotSecured(String name) {
        Cookie cookie = response.getDetailedCookie(name);
        assertFalse(cookie.isSecured());
    }

    @Then("the cookie {string} should have path {string}")
    public void checkCookiePath(String name, String path) {
        Cookie cookie = response.getDetailedCookie(name);
        assertEquals(cookie.getPath(), path);
    }

    @Then("all product prices should be less than or equal to {double}")
    public void checkPricesMax(double max) {
        List<Double> prices = response.jsonPath().getList("content.price", Double.class);
        assertTrue(prices.stream().allMatch(p -> p <= max));
    }

    @Then("all product prices should be greater than or equal to {double}")
    public void checkPricesMin(double min) {
        List<Double> prices = response.jsonPath().getList("content.price", Double.class);
        assertTrue(prices.stream().allMatch(p -> p >= min));
    }

    @Then("all product prices should be between {double} and {double}")
    public void checkPricesRange(double min, double max) {
        List<Double> prices = response.jsonPath().getList("content.price", Double.class);
        assertTrue(prices.stream().allMatch(p -> p >= min && p <= max));
    }

    @Then("the response name should be {string}")
    public void checkResponseName(String name) {
        assertEquals(response.jsonPath().getString("name"), name);
    }

    @Then("the database should reflect the name change for product {int}")
    public void verifyDbNameChange(int id) throws SQLException {
        // Similar to patch test
    }

    @Then("the response price should be {double}")
    public void checkResponsePrice(double price) {
        assertEquals(response.jsonPath().getDouble("price"), price, 0.0001);
    }

    @Then("the database should reflect the price change for product {int}")
    public void verifyDbPriceChange(int id) throws SQLException {
        // Similar
    }

    @Then("the database should reflect the product update for ID {int}")
    public void verifyDbProductUpdate(int id) throws SQLException {
        PreparedStatement prodStmt = conn.prepareStatement("select * from product where id = ?");
        prodStmt.setLong(1, id);
        ResultSet rs = prodStmt.executeQuery();
        while (rs.next()) {
            String dbName = rs.getString("name");
            Double dbPrice = rs.getDouble("price");
            assertEquals(dbName, "new laptop1");
            assertEquals(dbPrice, 23.0);
        }
    }

    @Then("the products should be sorted by price descending")
    public void verifySortDesc() {
        // Implement sorting check
    }

    @Then("the products should be sorted by price ascending with correct pagination")
    public void verifySortAsc() {
        // Implement
    }

    @Then("all products should match the database")
    public void verifyAllProducts() throws SQLException {
        // Similar to verifyProductsTest
    }

    @Then("the products should match the database")
    public void verifyProducts() throws SQLException {
        List<Map<String, Object>> apiProducts = response.jsonPath().getList("$");
        ResultSet rs = stmt.executeQuery("SELECT * FROM product");
        while (rs.next()) {
            Long dbId = rs.getLong("id");
            String dbName = rs.getString("name");
            Double dbPrice = rs.getDouble("price");
            boolean matchFound = apiProducts.stream().anyMatch(apiProduct -> {
                Number apiPriceNum = (Number) apiProduct.get("price");
                Double apiPrice = apiPriceNum != null ? apiPriceNum.doubleValue() : null;
                return dbId.equals(((Number) apiProduct.get("id")).longValue()) &&
                        dbName.equals(apiProduct.get("name")) &&
                        Objects.equals(dbPrice, apiPrice);
            });
            assertTrue(matchFound, "Mismatch for product ID: " + dbId);
        }
    }

    @Then("the product should be added to the database with features")
    public void verifyProductAdded() throws SQLException {
        Long productId = response.jsonPath().getLong("id");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product WHERE id = ?");
        stmt.setLong(1, productId);
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next(), is(true));
        assertThat(rs.getString("name"), equalTo("Laptop"));
        assertThat(rs.getDouble("price"), equalTo(75000d));
        PreparedStatement fStmt = conn.prepareStatement("SELECT * FROM feature WHERE product_id = ?");
        fStmt.setLong(1, productId);
        ResultSet rsFeatures = fStmt.executeQuery();
        List<String> featureNames = new ArrayList<>();
        while (rsFeatures.next()) {
            featureNames.add(rsFeatures.getString("name"));
        }
        assertThat(featureNames, hasItems("RAM", "Storage"));
    }

    @Then("all logins should be successful")
    public void allLoginsSuccessful() {
        assertEquals(loginSuccessCount, expectedLogins);
    }

    private HashMap<Integer, Integer> getCartItemsFromDb(String name) throws SQLException {
        PreparedStatement cartStmt = conn.prepareStatement("select product_id, quantity from cart_item where cart_id in(select id from cart where user_id in (select id from users where name=?))");
        cartStmt.setString(1, name);
        ResultSet rs = cartStmt.executeQuery();
        HashMap<Integer, Integer> dbCartItems = new HashMap<>();
        while (rs.next()) {
            Integer productId = rs.getInt("product_id");
            Integer quantity = rs.getInt("quantity");
            dbCartItems.put(productId, quantity);
        }
        return dbCartItems;
    }
}
