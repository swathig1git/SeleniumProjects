Feature: DB API Tests

  Background:
    Given the base URI is "http://localhost:8080"
    And the database connection is established

  Scenario: Delete user test
    When I send a DELETE request to "/users/delete/{name}" with name "A"
    Then the response status should be 200
    And the response message should be "User deleted successfully"

  Scenario: Create user test
    When I send a POST request to "/users/register" with user details:
      | name     | AB       |
      | email    | AB@b.com |
      | age      | 30       |
      | password | password |
    Then the response status should be 201
    And the response message should be "User created successfully"

  Scenario: Delete cart item test
    Given I login as "Alice" with password "password"
    When I send a DELETE request to "/cart/remove" with query param "productId" "2"
    Then the response status should be 200

  Scenario: View cart success test
    Given I login as "Alice" with password "password"
    When I send a GET request to "/cart/view"
    Then the response status should be 200
    And the cart items should match the database for user "Alice"

  Scenario: Update cart success test
    Given I login as "Alice" with password "password"
    When I send a POST request to "/cart/add" with cart products:
      | 2 | 5 |
    Then the response status should be 200
    And the cart quantities should be updated in the database for user "Alice"

  Scenario: Add to cart no stock test
    Given I login as "Alice" with password "password"
    When I send a POST request to "/cart/add" with cart products:
      | 4 | 5 |
      | 11 | 3 |
    Then the response status should be 400

  Scenario: Add to cart success test
    Given I login as "Alice" with password "password"
    When I send a POST request to "/cart/add" with cart products:
      | 1 | 2 |
      | 3 | 1 |
    Then the response status should be 200
    And the cart should contain product IDs "1,3"

  @VerifyLoginSuccess
  Scenario: Verify login success for all users
    When I verify login for all users from JSON
    Then all logins should be successful

  Scenario: Verify login failure test
    When I send a POST request to "/users/login" with login details:
      | username | Alice          |
      | password | wrongpassword  |
    Then the response status should be 401
    And the response message should be "Invalid credentials"

  Scenario: Verify users test
    When I send a GET request to "/users/userList"
    Then the response should match the users in the database

  Scenario: Verify product price form param test
    When I send a POST request to "/products/products/filter" with form params:
      | minPrice | 5000.0 |
      | maxPrice | 10000.0 |
    Then the response status should be 200

  Scenario: Verify product price min null test
    When I send a GET request to "/products/productList" with query param "minPrice" "null"
    Then the response should be processed

  Scenario: Verify product price min GT max test
    When I send a GET request to "/products/productList" with query params:
      | minPrice | 50000.0 |
      | maxPrice | 5000.0  |
    Then the response status should be 200
    And the products list should be empty

  Scenario: Verify cookie product list test
    When I send a GET request to "/products/productList"
    Then the response status should be 200
    And the cookie "lastVisited" should have value "productList"
    And the cookie "lastVisited" should be httpOnly
    And the cookie "lastVisited" should not be secured
    And the cookie "lastVisited" should have path "/"

  Scenario: Verify product price range max only test
    When I send a GET request to "/products/productList" with query param "maxPrice" "50000.0"
    Then all product prices should be less than or equal to 50000.0

  Scenario: Verify product price range min only test
    When I send a GET request to "/products/productList" with query param "minPrice" "70000.0"
    Then all product prices should be greater than or equal to 70000.0

  Scenario: Verify products price range test
    When I send a GET request to "/products/productList" with query params:
      | minPrice | 5000.0 |
      | maxPrice | 10000.0 |
    Then all product prices should be between 5000.0 and 10000.0

  Scenario: Verify product patch name test
    When I send a PATCH request to "/products/products/4" with body:
      | name | namepatch |
    Then the response name should be "namepatch"
    And the database should reflect the name change for product 4

  Scenario: Verify product patch price test
    When I send a PATCH request to "/products/products/5" with body:
      | price | 24.0 |
    Then the response price should be 24.0
    And the database should reflect the price change for product 5

  Scenario: Verify product update put test
    When I send a PUT request to "/products/3" with product details:
      | name  | new laptop1 |
      | price | 23.0        |
    Then the response status should be 200
    And the database should reflect the product update for ID 3

  Scenario: Verify products sort desc test
    When I send GET requests to "/products/paged" with sort "price,desc" for all pages
    Then the products should be sorted by price descending

  Scenario: Verify products sort asc test
    When I send GET requests to "/products/paged" with sort "price,asc" for all pages
    Then the products should be sorted by price ascending with correct pagination

  Scenario: Verify products pagination test
    When I send GET requests to "/products/paged" for all pages
    Then all products should match the database

  Scenario: Verify products test
    When I send a GET request to "/products/productList"
    Then the products should match the database

  Scenario: Test add product and verify DB
    When I send a POST request to "/products/add" with product details:
      | name     | Laptop   |
      | price    | 75000    |
      | features | [{"name": "RAM", "description": "16GB"}, {"name": "Storage", "description": "512GB SSD"}] |
    Then the response status should be 200
    And the product should be added to the database with features
