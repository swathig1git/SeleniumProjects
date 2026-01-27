package org.dbApiTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.dbpojo.CartItem;
import org.dbpojo.CartResponse;
import org.dbpojo.LoginRequest;
import org.dbpojo.Product;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class DBTests {
    Statement       stmt;
    Connection conn;
    @BeforeMethod
    public void initialize() throws SQLException {
        conn = DriverManager.getConnection(    "jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC",
                        "root",
                        "Password123");

        stmt = conn.createStatement();

        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void deleteUserTest() {
        String userNameToDelete = "A";
        Response response = RestAssured
                .given()
                .when()
                .delete("/users/delete/{name}", userNameToDelete);

        response.then().statusCode(200)
                .body("message", equalTo("User deleted successfully"));
    }
    @Test
    public void createUserTest() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "AB");
        user.put("email", "AB@b.com");
        user.put("age", "30");
        user.put("password", "password");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register");

        response.then().statusCode(201)
                .body("message", equalTo("User created successfully"));
    }

    public String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login");

        response.then()
                .statusCode(200)
                .body("message", equalTo("Login successful"))
                .body("token", notNullValue());

        return response.jsonPath().getString("token");
    }

    @Test
    public void deleteCartItemTest() throws SQLException {
        String name = "Alice";
        String token = loginAndGetToken("Alice", "password");


        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("productId", 2)
                .when()
                .delete("/cart/remove");



        //response.prettyPrint();
        response.then()
                .statusCode(200);
    }

    @Test
    public void viewCartSuccessTest() throws SQLException {
        String name = "Alice";
        String token = loginAndGetToken("Alice", "password");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cart/view");

        HashMap<Integer, Integer> dbCartItems = new HashMap<>();
        dbCartItems = getCartItemsFromDb(name);
        response.then()
                .statusCode(200);
        CartResponse cartResponse = response.as(CartResponse.class);
        for (CartItem cartItem : cartResponse.getItems()){
            Integer productId = cartItem.getProductId();
            Integer quantity = cartItem.getQuantity();
            assertEquals(quantity, dbCartItems.get(productId));
        }
    }

    public HashMap<Integer, Integer> getCartItemsFromDb(String name) throws SQLException {
        PreparedStatement cartStmt = conn.prepareStatement("select product_id, quantity  from cart_item where cart_id in(select id from cart where user_id in (select id from users where name= ?))");
        cartStmt.setString(1, name);

        ResultSet rs = cartStmt.executeQuery();
        HashMap<Integer, Integer> dbCartItems = new HashMap<>();

        while (rs.next()){
            Integer productId = rs.getInt("product_id");
            Integer quantity = rs.getInt("quantity");
            dbCartItems.put(productId, quantity);
        }
        return dbCartItems;
    }
    @Test
    public void updateCartSuccessTest() throws SQLException {
        String name = "Alice";
        String token = loginAndGetToken("Alice", "password");



        Map<Integer, Integer> dbCartItems = new HashMap<>();
        dbCartItems = getCartItemsFromDb(name);

        Map<Integer, Integer> cartItem = new HashMap<>();
        cartItem.put(2, 5); // productId: 2, quantity: 5

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", cartItem);

        Response addToCartResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(cart)
                .when()
                .post("/cart/add");

        addToCartResponse.then()
                .statusCode(200);

        CartResponse cartResponse = addToCartResponse.as(CartResponse.class);

        for (CartItem cartItem1 : cartResponse.getItems()){
            Integer productId = cartItem1.getProductId();
            Integer quantity = cartItem1.getQuantity();
            if (dbCartItems.containsKey(productId)){
                assertEquals(quantity, (Integer)(dbCartItems.get(productId) + cartItem.getOrDefault(productId, 0)));
            }
            else {
                assertEquals(quantity, cartItem.get(productId));
            }
        }

    }
    @Test
    public void addToCartNoStockTest() throws SQLException {
        String name = "Alice";
        String token = loginAndGetToken(name, "password");

        Map<Integer, Integer> dbCartItems = new HashMap<>();
        dbCartItems = getCartItemsFromDb(name);

        Map<Integer, Integer> cartItem = new HashMap<>();
        cartItem.put(4, 5); // productId: 2, quantity: 5
        cartItem.put(11,3);

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", cartItem);

        Response addToCartResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(cart)
                .when()
                .post("/cart/add");

        addToCartResponse.then()
                .statusCode(400);


    }
        @Test
    public void addToCartSuccessTest() {
        LoginRequest loginRequest = new LoginRequest("Alice", "password");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login");

        response.then()
                .statusCode(200)
                .body("message", equalTo("Login successful"))
                .body("token", notNullValue());

        String token = response.jsonPath().getString("token");

        Map <Integer, Integer> cartItem = new HashMap<>();
        cartItem.put(1, 2); // productId: 1, quantity:
        cartItem.put(3,1);
        Map<String, Object> cart = new HashMap<>();
        cart.put("products", cartItem);

//        {
//            "products": {
//            "1": 2,
//                    "3": 1
//        }
//        }

        Response addToCartResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(cart)
                .when()
                .post("/cart/add");

        addToCartResponse.then()
                .statusCode(200);

        CartResponse cartResponse = addToCartResponse.as(CartResponse.class);

        Set<Integer> cartProductIds = cartResponse.getItems().stream().map(CartItem::getProductId).collect(Collectors.toSet());

        for(Integer productId: cartItem.keySet()){
            assertTrue(cartProductIds.contains(productId));
        }




    }
    @Test
    public void verifyLoginSuccessTest() {
        LoginRequest loginRequest = new LoginRequest("Alice", "password");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login");

        response.then()
                .statusCode(200)
                .body("message", equalTo("Login successful"))
                .body("token", notNullValue());


    }

    @Test
    public void verifyLoginFailureTest() {
        LoginRequest loginRequest = new LoginRequest("Alice", "wrongpassword");
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login");

        response.then()
                .statusCode(401)
                .body("message", equalTo("Invalid credentials"));
    }

    @Test
    public void verifyUsersTest() throws SQLException {
        Response response = RestAssured
                .get("/users/userList");

        response.prettyPrint();
        List<Map<String, Object>> apiUsers =
                response.jsonPath().getList("$");

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
                            dbAge == ((Number) user.get("age")).intValue()
            );

            assertTrue(matchFound, "Mismatch for DB user id: " + dbId);
        }
    }

    @Test
    public void verifyProductPriceFormParamTest() throws SQLException{
        Double minPrice = 5000.0;
        Double maxPrice = 10000.0;
        Response response = RestAssured
                .given()
                .contentType(ContentType.URLENC)
                .formParam("minPrice", minPrice)
                .formParam("maxPrice", maxPrice)
                .when()
                .post("/products/products/filter");
        response.prettyPrint();
        response.then().statusCode(200);
    }
    @Test
    public void verifyProductPriceMinNullTest() throws SQLException{
        Response response = RestAssured
                .given()
                .queryParam("minPrice", "null")
                .when()
                .get("/products/productList");
        response.prettyPrint();
    }
    @Test
    public void verifyProductPriceMinGTMaxTest() throws SQLException{
        double minPrice = 50000.0;
        double maxPrice = 5000.0;
        Response response = RestAssured
                .given()
                .queryParam("minPrice", Double.toString(minPrice))
                .queryParam("maxPrice", Double.toString(maxPrice))
                .when()
                .get("/products/productList");

        response.prettyPrint();
        response.then().statusCode(200);
        List<?> products = response.jsonPath().getList("$");
        assertNotNull(products);
        assertTrue(products.isEmpty());

    }

    @Test
    public void verifyCookieProductListTest(){
        Response response = RestAssured
                .given()
                .when()
                .get("/products/productList");
        response.prettyPrint();
        response.then().statusCode(200)
                .cookie("lastVisited", "productList");

        Cookie cookie = response.getDetailedCookie("lastVisited");
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecured());
        assertEquals(cookie.getPath(), "/");

    }
    @Test
    public void verifyProductPriceRangeMaxOnlyTest() throws SQLException{
        double maxPrice = 50000.0;
        Response response = RestAssured
                .given()
                .queryParam("maxPrice", Double.toString(maxPrice))
                .when()
                .get("/products/productList");
        response.prettyPrint();
        List<Double> apiPriceList = response.jsonPath().getList("content.price", Double.class);
        assertTrue(apiPriceList.stream().allMatch(apiPrice->apiPrice<=maxPrice));

    }

    @Test
    public void verifyProductPriceRangeMinOnlyTest() throws SQLException{
        double minPrice = 70000.0;
        Response response = RestAssured
                .given()
                .queryParam("minPrice", Double.toString(minPrice))
                .when()
                .get("/products/productList");
        response.prettyPrint();
//        List<Number> apiPriceListNum = response.jsonPath().getList("content.price", Number.class);
//        List<Double> apiPriceList = apiPriceListNum.stream().map(n-> n!=null?n.doubleValue():null).toList();
        List<Double> apiPriceList = response.jsonPath().getList("content.price", Double.class);
        assertTrue(apiPriceList.stream().allMatch(apiPrice->apiPrice >= minPrice));


    }
    @Test
    public void verifyProductsPriceRangeTest() throws SQLException{
        Double minPrice = 5000.0;
        Double maxPrice = 10000.0;


        Response response = RestAssured
                .given()
                .queryParam("minPrice", Double.toString(minPrice))
                .queryParam("maxPrice", Double.toString(maxPrice))
                .when()
                .get("/products/productList");
        response.prettyPrint();
        List<Number> apiPriceListNum = response.jsonPath().getList("content.price", Number.class);
        List<Double> apiPriceList = apiPriceListNum.stream().map(n->n!=null?n.doubleValue():null).toList();
        assertTrue(apiPriceList.stream().allMatch(apiPrice -> apiPrice >= minPrice));
        assertTrue(apiPriceList.stream().allMatch(apiPrice->apiPrice <= maxPrice));

    }
    @Test
    public void verifyProductPatchNameTest() throws SQLException{
        PreparedStatement prodStmt = conn.prepareStatement("select * from product where id= ?");
        prodStmt.setLong(1, 4);
        ResultSet rs = prodStmt.executeQuery();
        Product prodBeforeChange;

        if(rs.next()){

            prodBeforeChange = new Product(rs.getLong("id"), rs.getString("name"),
                    rs.getString("price")!=null?rs.getDouble("price"):null);
        }
        else throw new SQLException("no product");

        Map<String, Object> patchBody = new HashMap<>();
        patchBody.put("name", "namepatch");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(patchBody)
                .when()
                .patch("/products/products/{id}", prodBeforeChange.getId());

        response.prettyPrint();

        System.out.println("prodBeforeChange.getPrice()" + prodBeforeChange.getPrice() + " response.jsonPath().getDouble(\"price\")");
                //+ response.jsonPath().getDouble("price"));
        if(response.jsonPath().getString("price") != null)
        assertEquals(prodBeforeChange.getPrice(), response.jsonPath().getDouble("price"), 0.0001);
        else
            assertNull(prodBeforeChange.getPrice());
        assertEquals(response.jsonPath().getString("name"), patchBody.get("name"));

        rs = prodStmt.executeQuery();
        Product prodAfterChange;
        if(rs.next()){
            prodAfterChange = new Product(rs.getLong("id"), rs.getString("name"),
                    rs.getString("price")!= null?  rs.getDouble("price"):null);
        }
        else throw new SQLException("prod not found");

        if ( prodBeforeChange.getPrice() == null)
            assertNull(prodAfterChange.getPrice());
        else
            assertEquals(prodAfterChange.getPrice(), prodBeforeChange.getPrice(), 0.0001);

        assertEquals(prodAfterChange.getName(), patchBody.get("name"));

    }
    @Test
    public void verifyProductPatchPriceTest() throws SQLException{
        PreparedStatement prodStmt = conn.prepareStatement("select * from product where id = ?");
        prodStmt.setLong(1, 5);
        ResultSet rs = prodStmt.executeQuery();
        if(rs.next()) {
            Product prodBeforeChange = new Product(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"));


            Map<String, Object> patchBody = new HashMap<String, Object>();
            patchBody.put("price", 24.0);

            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(patchBody)
                    .when()
                    .patch("/products/products/{id}", prodBeforeChange.getId());

            response.prettyPrint();

            assertEquals(prodBeforeChange.getName(), response.jsonPath().get("name"));
            assertEquals(response.jsonPath().getDouble("price"), (Double) patchBody.get("price"),  0.0001);

            rs = prodStmt.executeQuery();
            if(rs.next()){
                Product updatedProduct = new Product(rs.getLong("id"), rs.getString("name"), rs.getDouble("price"));
                assertEquals(prodBeforeChange.getId(), updatedProduct.getId());
                assertEquals(prodBeforeChange.getName(), updatedProduct.getName());
                assertEquals(updatedProduct.getPrice(), (Double) patchBody.get("price"), 0.0001);
            }

            }



    }
    @Test
    public void verifyProductUpdatePutTest() throws  SQLException{
        Product product = new Product(3L, "new laptop1", 23.0);
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", product.getId());

        response.prettyPrint();

        response.then().statusCode(200);
        PreparedStatement prodStmt = conn.prepareStatement("select * from product where id = ?");
        prodStmt.setLong(1, product.getId());
        ResultSet rs = prodStmt.executeQuery();
        while (rs.next()){
            String dbName = rs.getString("name");
            Double dbPrice = rs.getDouble("price");
            assertEquals(dbName, product.getName());
            assertEquals(dbPrice, product.getPrice());
        }
    }
    @Test
    public void verifyProductsSortDescTest() throws SQLException{
        boolean isLastPage = false;
        int pageNumber = 0;
        int pageSize = 10;
        double lastPricePrevPage = 0;

        while(!isLastPage ){
            Response response = RestAssured
                    .given()
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize)
                    .queryParam("sort", "price,desc")
                    .when()
                    .get("/products/paged");
            List<Double> apiPriceList = response.jsonPath().getList("content.price", Double.class);
            System.out.println(apiPriceList);
            List<Double> nonNullPriceList = apiPriceList.stream().filter(Objects::nonNull).toList();
            List<Double> sortedPriceList = new ArrayList<>(nonNullPriceList);
            sortedPriceList.sort(Collections.reverseOrder());
            assertEquals(nonNullPriceList, sortedPriceList);
            pageNumber++;
            isLastPage = response.jsonPath().get("last");
            System.out.println("LAst=" + isLastPage);

        }
    }
    @Test
    public void verifyProductsSortAscTest() throws SQLException {

        boolean isLastPage = false;
        int pageNumber = 0;
        int pageSize = 10;
        double lastPricePrevPage = 0;

        while(!isLastPage){
            Response response = RestAssured
                    .given()
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize)
                    .queryParam("sort", "price,asc")
                    .when()
                    .get("/products/paged");

            List<Double> apiPriceList = response.jsonPath().getList("content.price", Double.class);
            List<Double> nonNullPrices = apiPriceList.stream().filter(Objects::nonNull).toList();
            List<Double> sortedPrices = new ArrayList<>(nonNullPrices);
            Collections.sort(sortedPrices);
            assertEquals(nonNullPrices, sortedPrices);
            System.out.println(apiPriceList);

            if (!nonNullPrices.isEmpty()) {

                Double firstPriceCurrentPage = nonNullPrices.getFirst();

                if (pageNumber > 0)
                    assertTrue(lastPricePrevPage <= firstPriceCurrentPage, "Pagination sort broken between pages");

                lastPricePrevPage = nonNullPrices.getLast();
            }



            isLastPage = response.jsonPath().get("last");
            pageNumber++;
        }
    }
        @Test
    public void verifyProductsPaginationTest() throws SQLException {
        int pageNumber = 0;
        int pageSize = 10;
        boolean isLastPage = false;

        List<Map<String, Object>> allApiProducts = new ArrayList<>();

        while(!isLastPage) {
            Response response = RestAssured
                    .given()
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize)
                    .get("/products/paged");

            response.prettyPrint();
            List<Map<String, Object>> apiProducts =
                    response.jsonPath().getList("content");
            allApiProducts.addAll(apiProducts);
            isLastPage = response.jsonPath().get("last");
            pageNumber++;

        }

        ResultSet rs = stmt.executeQuery("select * from product");
        while(rs.next()){
            long dbId = rs.getLong("id");
            String dbName = rs.getString("name");
            Double dbPrice = rs.getDouble("price");

            PreparedStatement featureStmt = conn.prepareStatement
                    ("Select * from feature where product_id = ?");
            featureStmt.setLong(1, dbId);

            ResultSet rsFeature = featureStmt.executeQuery();

            List<Map<String, Object>> dbFeatures = new ArrayList<>();
            while (rsFeature.next()){
                Map<String, Object> feature = new HashMap<>();
                feature.put("id", rsFeature.getLong("id"));
                feature.put("name", rsFeature.getString("name"));
                feature.put("description", rsFeature.getString("description"));
                dbFeatures.add(feature);
            }

            boolean matchFound = allApiProducts.stream().anyMatch(apiProduct -> {
                Number apiPriceNum = (Number) apiProduct.get("price");
                Double apiPrice = apiPriceNum != null ? apiPriceNum.doubleValue():null;
                return dbId == (((Number) apiProduct.get("id")).longValue()) &&
                        dbName.equals(apiProduct.get("name")) &&
                        Objects.equals(dbPrice, apiPrice) &&
                        dbFeatures.equals(apiProduct.get("features")); // compare feature lists
            });
        }

    }

    @Test
    public void verifyProductsTest() throws SQLException {
        Response response = RestAssured
                .get("/products/productList");

        response.prettyPrint();
        List<Map<String, Object>> apiProducts =
                response.jsonPath().getList("$");

// Assume apiProducts is a List<Map<String, Object>> from API response
        ResultSet rs = stmt.executeQuery("SELECT * FROM product");
        while (rs.next()) {
            Long dbId = rs.getLong("id");
            String dbName = rs.getString("name");
            Double dbPrice;
            double priceValue = rs.getDouble("price");
            if(!rs.wasNull())
                dbPrice = priceValue;
            else {
                dbPrice = null;
            }

            // Get features for this product
            PreparedStatement featureStmt = conn.prepareStatement(
                    "SELECT * FROM feature WHERE product_id = ?"
            );
            featureStmt.setLong(1, dbId);
            ResultSet rsFeature = featureStmt.executeQuery();

            List<Map<String, Object>> dbFeatures = new ArrayList<>();
            while (rsFeature.next()) {
                Map<String, Object> feature = new HashMap<>();
                feature.put("id", rsFeature.getLong("id"));
                feature.put("name", rsFeature.getString("name"));
                feature.put("description", rsFeature.getString("description"));
                dbFeatures.add(feature);
            }

            boolean matchFound = apiProducts.stream().anyMatch(apiProduct -> {
                Number apiPriceNum = (Number) apiProduct.get("price");
                Double apiPrice = apiPriceNum != null?apiPriceNum.doubleValue() : null;
                if (dbId.equals(((Number) apiProduct.get("id")).longValue()))
                    System.out.println("dbid = " + dbId + " dbPrice = " + dbPrice + " apiPrice = " + apiPrice);

                return dbId.equals(((Number) apiProduct.get("id")).longValue()) &&
                        dbName.equals(apiProduct.get("name")) &&
                        Objects.equals(dbPrice, apiPrice) ;
                        //dbFeatures.equals(apiProduct.get("features")
                       // ); // compare feature lists
            });

            if (!matchFound) {
                System.out.println("Mismatch found for product ID: " + dbId);
            }
        }

    }

    @Test
    public void testAddProductAndVerifyDb() throws SQLException {
        // Prepare features
        Map<String, Object> feature1 = Map.of("name", "RAM", "description", "16GB");
        Map<String, Object> feature2 = Map.of("name", "Storage", "description", "512GB SSD");
        List<Map<String, Object>> features = List.of(feature1, feature2);

        // Prepare product JSON
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Laptop");
        product.put("price", 75000);
        product.put("features", features);

        // Send POST request
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(product)
                .post("/products/add");

        // Validate API response
        response.then().statusCode(200);
        Long productId = response.jsonPath().getLong("id");
        assertThat(productId, notNullValue());
        assertThat(response.jsonPath().getString("name"), equalTo("Laptop"));
        assertThat(response.jsonPath().getFloat("price"), equalTo(75000f));

        // --- Verify database ---
        // 1️⃣ Verify product table
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product WHERE id = ?");
        stmt.setLong(1, productId);
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next(), is(true));
        assertThat(rs.getString("name"), equalTo("Laptop"));
        assertThat(rs.getDouble("price"), equalTo(75000d));

        // 2️⃣ Verify features table
        PreparedStatement fStmt = conn.prepareStatement("SELECT * FROM feature WHERE product_id = ?");
        fStmt.setLong(1, productId);
        ResultSet rsFeatures = fStmt.executeQuery();

        List<Map<String, String>> dbFeatures = new ArrayList<>();
        while (rsFeatures.next()) {
            Map<String, String> f = new HashMap<>();
            f.put("name", rsFeatures.getString("name"));
            f.put("description", rsFeatures.getString("description"));
            dbFeatures.add(f);
        }

        assertThat(dbFeatures.size(), equalTo(2));
        List<String> featureNames = dbFeatures.stream().map(f -> f.get("name")).toList();
        assertThat(featureNames, hasItems("RAM", "Storage"));
    }

}
