Feature: homepage
         As an anonymous user
         I want to be a list of items
         So I can visit them quickly


         Scenario: Load homepage
                   Givern I am an anonymous user or logged-in user
                   When I visit the "/" page
                   Then I will see an item list
