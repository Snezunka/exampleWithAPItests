package com.home.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.dto.ResultsDTO;
import com.home.dto.ResultsDTO.User;
import contexts.ScenarioContext;
import contexts.ScenarioContext.Context;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

public class RestAPISteps {

  public void checkStatusCode(int statusCode) {
    Response response = ScenarioContext.getContext(Context.HTTP_RESPONSE);
    response.then().log().all();
    response.then().statusCode(statusCode);
  }

  public void checkPageNumberInResponseIsEqualTo(int pageNumber) {
    ResultsDTO result = parseResponse();
    int actualPage = result.getInfo().getPage();
    Assertions.assertThat(actualPage)
        .as("Actual page number is not equal to expected")
        .isEqualTo(pageNumber);
  }

  public void checkResultsInResponseIsEqualTo(int numberOfResults) {
    ResultsDTO result = parseResponse();
    int actualResults = result.getInfo().getResults();
    Assertions.assertThat(actualResults)
        .as("Actual number of results is not equal to expected")
        .isEqualTo(numberOfResults);
  }

  public void checkSeedInResponseIsEqualTo(String seed) {
    ResultsDTO result = parseResponse();
    String actualSeed = result.getInfo().getSeed();
    Assertions.assertThat(actualSeed)
        .as("Actual seed is not equal to expected")
        .isEqualTo(seed);
  }

  public void checkNumberOfUsersInResponseIsEqualTo(int numberOfResults) {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    Assertions.assertThat(listOfUsers.size())
        .as("Actual number of Users in response is not equal to expected")
        .isEqualTo(numberOfResults);
  }

  public void checkNationalityInResponseIsEqualTo(String nationality) {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    String actualNationality = listOfUsers.get(0).getNat();
    Assertions.assertThat(actualNationality)
        .as("Actual nationality of User in response is not equal to expected")
        .isEqualToIgnoringCase(nationality);
  }

  public void checkUserPasswordRespondToPasswordConstraints(String passwordConstrains) {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    String actualPassword = listOfUsers.get(0).getLogin().getPassword();
    String pattern = generatePattern(passwordConstrains);
    Assertions.assertThat(actualPassword.matches(pattern))
        .as("Password " + actualPassword + " doesn't match pattern " + pattern).isTrue();
  }

  public void makeAdditionalChecks() {
    int personAge = 40;
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(haveAllPersonsOnThePageTheSameGender())
        .as("Not all person on the page have the same gender").isTrue();

    softAssertions.assertThat(containAllPasswordsOnThePageAtLeastOneDigitAndOneCapitalSymbol())
        .as("All passwords on the page don't contain at least one digit and one capital symbol")
        .isTrue();

    softAssertions.assertThat(noneOfPasswordsContainUserName())
        .as("At least one password contains User name").isTrue();

    softAssertions.assertThat(isAtLeastOneOccurenceOfMrsMsMr())
        .as("Average age of users on th page is not lower than " + personAge).isTrue();

    softAssertions.assertThat(isAverageAgeOnThePageLowerThan(personAge))
        .as("Average age of users on th page is not lower than " + personAge).isTrue();

    softAssertions.assertAll();
  }

  private boolean isAtLeastOneOccurenceOfMrsMsMr() {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    return listOfUsers.stream()
        .anyMatch(user -> { String userTitle = user.getName().getTitle();
        return userTitle.equals("Mrs") || userTitle.equals("Mr") || userTitle.equals("Ms");
        });
  }

  private boolean isAverageAgeOnThePageLowerThan(int personAge) {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    double averageAge = listOfUsers.stream()
        .mapToInt(user -> user.getDob().getAge())
        .average()
        .getAsDouble();
    return averageAge < personAge;
  }

  private boolean noneOfPasswordsContainUserName() {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    return listOfUsers.stream()
        .noneMatch(user -> {
          String password = user.getLogin().getPassword();
          String firstName = user.getName().getFirst();
          String lastName = user.getName().getLast();
          return password.contains(firstName) || password.contains(lastName);
        });
  }

  private boolean containAllPasswordsOnThePageAtLeastOneDigitAndOneCapitalSymbol() {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    String pattern = "(?=.*[0-9])(?=.*[A-Z])";
    return listOfUsers.stream()
        .allMatch(user -> user.getLogin().getPassword().matches(pattern));
  }

  private boolean haveAllPersonsOnThePageTheSameGender() {
    ResultsDTO result = parseResponse();
    List<User> listOfUsers = result.getResults();
    boolean allUsersHaveFemaleGender = listOfUsers.stream()
        .allMatch(user -> user.getGender().equals("female"));
    boolean allUsersHaveMaleGender = listOfUsers.stream()
        .allMatch(user -> user.getGender().equals("male"));
    return allUsersHaveFemaleGender || allUsersHaveMaleGender;
  }

  private ResultsDTO parseResponse() {
    Response response = ScenarioContext.getContext(Context.HTTP_RESPONSE);
    ObjectMapper mapper = new ObjectMapper();
    ResultsDTO result = null;
    String body = response.body().asString();
    try {
      result = mapper.readValue(body, ResultsDTO.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private String generatePattern(String passwordConstrains) {
    StringBuilder pattern = new StringBuilder();
    if (passwordConstrains.contains("upper")) {
      pattern.append("(?=.*[A-Z])");
      passwordConstrains = passwordConstrains.replace("upper,", "");
    }
    if (passwordConstrains.contains("lower")) {
      pattern.append("(?=.*[a-z])");
      passwordConstrains = passwordConstrains.replace("lower,", "");
    }
    if (passwordConstrains.contains("number")) {
      pattern.append("(?=.*[0-9])");
      passwordConstrains = passwordConstrains.replace("number,", "");
    }
    int index = passwordConstrains.indexOf("-");
    int minNumberOfSymbols = Integer.parseInt(passwordConstrains.substring(0, index));
    int maxNumberOfSymbols = Integer
        .parseInt(passwordConstrains.substring(index + 1, passwordConstrains.length()));
    String passwordLengthConstraints = String
        .format(".{%s,%s}", minNumberOfSymbols, maxNumberOfSymbols);
    pattern.append(passwordLengthConstraints);
    return pattern.toString();
  }
}
