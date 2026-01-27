Feature: SAKS Product Filter Tests

  Background:
    Given the SAKS application is launched

  Scenario Outline: Browse by menu test for <productName>
    When I launch the page for product "<productName>"
    And I scroll until products are visible with count 4
    Then the browse by buttons should match the expected list for "<productName>"

    Examples:
      | productName |
      | ExampleProduct1 |
      | ExampleProduct2 |


  Scenario Outline: Price filter test for <productName>
    When I launch the category page for product "<productName>"
    And I wait for popup watcher to finish with timeout 20000
    And I scroll until products are visible with count 4
    And I update price range from 500 to 6000
    And I scroll until products are visible with count 8
    Then all product current prices should be between 500 and 6000 for "<productName>"

    Examples:
      | productName |
      | ExampleProduct1 |
      | ExampleProduct2 |

  Scenario Outline: Price filter out of range test for <productName>
    When I launch the category page for product "<productName>"
    And I wait for popup watcher to finish with timeout 20000
    And I scroll until products are visible with count 4
    And I update price range from 1000000 to 2000000
    Then the product list should be empty for "<productName>"

    Examples:
      | productName |
      | ExampleProduct1 |
      | ExampleProduct2 |
