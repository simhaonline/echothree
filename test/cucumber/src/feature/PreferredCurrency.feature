Feature: Preferred currency
  A customer wants to set the preferred currency

  Scenario: An anonymous customer sets their preferred currency
    Given Test is not currently logged in
    When the customer Test sets their preferred currency to "X-1"
    Then no error should occur