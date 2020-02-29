Feature: Employee sales order
  An employee wants to add a sales order

  Background:
    Given the employee Test is not currently logged in
    When the employee Test logs in with the username "Test E" and password "password" and company "TEST_COMPANY"
    Then no error should occur

  Scenario: Existing employee adds a sales order with defaults
    Given the employee Test is currently logged in
    And the employee Test adds a new sales order
    Then no error should occur
