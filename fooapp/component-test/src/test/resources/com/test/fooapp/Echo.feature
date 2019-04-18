@FixtureLoad
Feature: Only echo cool sentences

    Scenario: An request without bar
        Given a "cool" sentence
        When I shout in the cave
        Then I hear it back

    Scenario: An request with bar
        Given a "not cool" sentence
        When I shout in the cave
        Then I dont hear anything
