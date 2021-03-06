Feature: Preferred time zone
  A customer wants to set the preferred time zone

  Scenario: An anonymous customer sets their preferred time zone
    Given the customer Test begins using the application
    And the user is not currently logged in
    When the user sets their preferred time zone to "US/Pacific"
    Then no error should occur
