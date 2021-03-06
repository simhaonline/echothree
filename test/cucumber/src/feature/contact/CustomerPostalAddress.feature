Feature: Customer postal address
  A customer wants to add, edit and delete postal addresses associated with their account

  Background:
    Given the customer Test begins using the application
    And the user is not currently logged in
    When the user logs in as a customer with the username "TestC@echothree.com" and password "password"
    Then no error should occur

  Scenario: Existing customer adds and then deletes an postal address without a description and does not allow solicitations
    Given the customer Test begins using the application
    And the user begins entering a new postal address
    And the user sets the postal address's first name to "Test"
    And the user sets the postal address's last name to "Customer"
    And the user sets the postal address's line 1 to "256 N Test Street"
    And the user sets the postal address's city to "Des Moines"
    And the user sets the postal address's state to "IA"
    And the user sets the postal address's postal code to "50310"
    And the user sets the postal address's country to "US"
    And the user's postal address is not a commercial location
    And the user does not allow solicitations to the postal address
    And the user adds the new postal address
    Then no error should occur
    And the user deletes the last postal address added
    Then no error should occur

  Scenario: Existing customer adds and then deletes an postal address without a description and does allow solicitations
    Given the customer Test begins using the application
    And the user begins entering a new postal address
    And the user sets the postal address's first name to "Test"
    And the user sets the postal address's last name to "Customer"
    And the user sets the postal address's line 1 to "256 N Test Street"
    And the user sets the postal address's city to "Des Moines"
    And the user sets the postal address's state to "IA"
    And the user sets the postal address's postal code to "50310"
    And the user sets the postal address's country to "US"
    And the user's postal address is not a commercial location
    And the user does allow solicitations to the postal address
    And the user adds the new postal address
    Then no error should occur
    And the user deletes the last postal address added
    Then no error should occur

  Scenario: Existing customer adds and then deletes an postal address with a description and does not allow solicitations
    Given the customer Test begins using the application
    And the user begins entering a new postal address
    And the user sets the postal address's first name to "Test"
    And the user sets the postal address's last name to "Customer"
    And the user sets the postal address's line 1 to "256 N Test Street"
    And the user sets the postal address's city to "Des Moines"
    And the user sets the postal address's state to "IA"
    And the user sets the postal address's postal code to "50310"
    And the user sets the postal address's country to "US"
    And the user sets the postal address's description to "Additional Postal"
    And the user's postal address is not a commercial location
    And the user does not allow solicitations to the postal address
    And the user adds the new postal address
    Then no error should occur
    And the user deletes the last postal address added
    Then no error should occur

  Scenario: Existing customer adds and then deletes an postal address with a description and does allow solicitations
    Given the customer Test begins using the application
    And the user begins entering a new postal address
    And the user sets the postal address's first name to "Test"
    And the user sets the postal address's last name to "Customer"
    And the user sets the postal address's line 1 to "256 N Test Street"
    And the user sets the postal address's city to "Des Moines"
    And the user sets the postal address's state to "IA"
    And the user sets the postal address's postal code to "50310"
    And the user sets the postal address's country to "US"
    And the user sets the postal address's description to "Additional Postal"
    And the user's postal address is not a commercial location
    And the user does allow solicitations to the postal address
    And the user adds the new postal address
    Then no error should occur
    And the user deletes the last postal address added
    Then no error should occur

  Scenario: Existing customer adds, edits and then deletes an postal address with a description and does allow solicitations
    Given the customer Test begins using the application
    And the user begins entering a new postal address
    And the user sets the postal address's first name to "Test"
    And the user sets the postal address's last name to "Customer"
    And the user sets the postal address's line 1 to "256 N Test Street"
    And the user sets the postal address's city to "Des Moines"
    And the user sets the postal address's state to "IA"
    And the user sets the postal address's postal code to "50310"
    And the user sets the postal address's country to "US"
    And the user sets the postal address's description to "Additional Postal"
    And the user's postal address is not a commercial location
    And the user does not allow solicitations to the postal address
    And the user adds the new postal address
    Then no error should occur
    And the user begins editing the last postal address added
    Then no error should occur
    And the user sets the postal address's line 1 to "128 N Test Street"
    And the user sets the postal address's description to "Edited Postal"
    And the user finishes editing the postal address
    Then no error should occur
    And the user deletes the last postal address added
    Then no error should occur
