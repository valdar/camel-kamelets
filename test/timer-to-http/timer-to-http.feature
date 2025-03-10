Feature: Verify Camel K integrations

  Background:
    Given HTTP server "test-service"
    Given HTTP server listening on port 8080
    Given HTTP request timeout is 6000 ms
    Given Kubernetes timeout is 60000 ms

  Scenario: Verify timer-to-http integration
    # Create Http server
    Given create Kubernetes service test-service with target port 8080

    # Run Camel K integration
    Given Camel K integration property file application.properties
    When load Camel K integration timer-to-http.groovy
    Then Camel K integration timer-to-http should be running

    # Verify Http request and send response
    Then expect HTTP request body: YAKS rocks!
    And receive PUT /messages
    And HTTP response body: Thank You!
    And send HTTP 200 OK

    # Verify Camel K integration logs
    And Camel K integration timer-to-http should print Thank You!
