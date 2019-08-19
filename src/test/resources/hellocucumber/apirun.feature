Feature: Run APi

   Scenario: run test
    Given the following API Fortress Project name "Workbench"
    When run all tests
    Then all test should pass