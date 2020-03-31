Feature: Anonymous user adds customer with login
  An anonymous user wants to add a new customer with login

  Scenario: Anonymous user adds a customer with login and an employee deletes the login
    Given the anonymous user Gerald begins adding a new customer
    And the anonymous user Gerald sets the new customer's first name to "Gerald"
    And the anonymous user Gerald sets the new customer's last name to "Jonas"
    And the anonymous user Gerald sets the new customer's email address to "gj-test@echothree.com"
    And the anonymous user Gerald does not allow solicitations to the new customer
    And the anonymous user Gerald sets the new customer's username to "gj"
    And the anonymous user Gerald sets the new customer's first password to "my-password"
    And the anonymous user Gerald sets the new customer's second password to "my-password"
    And the anonymous user Gerald sets the new customer's recovery question to PET_NAME
    And the anonymous user Gerald sets the new customer's answer to "Mufasa"
    And the anonymous user Gerald adds the new customer
    Then no error should occur
    And the employee Test begins using the application
    And the user is not currently logged in
    When the user logs in as an employee with the username "Test E" and password "password" and company "TEST_COMPANY"
    Then no error should occur
    And the employee Test deletes the user login added by the anonymous user Gerald
