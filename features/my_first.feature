Feature: Find Bus Stop Code

  Scenario: Find a valid Bus Stop Code
    When I press "OK"
    Then I wait up to 5 seconds for the "HomeActivity" screen to appear
    Then I press view with id "home_btn_search"
    Then I wait
    And I enter text "PA663" into field with id "search_src_text"
	And I press the enter button
	Then I wait up to 5 seconds for the "TransChooseServiceActivity" screen to appear
