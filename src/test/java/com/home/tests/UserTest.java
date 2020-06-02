package com.home.tests;

import com.home.enums.StatusCode;
import com.home.services.api.UsersService;
import com.home.steps.RestAPISteps;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class UserTest {

  private UsersService usersService = new UsersService();
  private RestAPISteps restAPISteps = new RestAPISteps();

  @Test
  public void getUsersWithPageResultsAndSeedTest() {
    int pageNumber = 3;
    int numberOfResults = 10;
    String seed = "abc";

    usersService.getUsers(pageNumber, numberOfResults, seed);
    restAPISteps.checkStatusCode(StatusCode.OK.getValue());
    restAPISteps.checkPageNumberInResponseIsEqualTo(pageNumber);
    restAPISteps.checkResultsInResponseIsEqualTo(numberOfResults);
    restAPISteps.checkSeedInResponseIsEqualTo(seed);
    restAPISteps.checkNumberOfUsersInResponseIsEqualTo(numberOfResults);
  }

  @Test
  public void getUserWithSpecificNationalityTest() {
    String nationality = "gb";

    usersService.getUserByNationality(nationality);
    restAPISteps.checkStatusCode(StatusCode.OK.getValue());
    restAPISteps.checkNationalityInResponseIsEqualTo(nationality);
  }

  @Test
  public void getUserWithPasswordConstraintsTest() {
    String passwordConstraints = "upper,lower,number,5-16";

    usersService.getUserWithPasswordConstraints(passwordConstraints);
    restAPISteps.checkStatusCode(StatusCode.OK.getValue());
    restAPISteps.checkUserPasswordRespondToPasswordConstraints(passwordConstraints);
  }

  @Test(dataProvider = "dataProviderForAPITest")
  public void getUsersWithPageAndResultsTest(int numberOfResults) {
    usersService.getUsers(numberOfResults);
    restAPISteps.checkStatusCode(StatusCode.OK.getValue());
    restAPISteps.checkResultsInResponseIsEqualTo(numberOfResults);
    restAPISteps.checkNumberOfUsersInResponseIsEqualTo(numberOfResults);
    restAPISteps.makeAdditionalChecks();
  }

  @DataProvider(name = "dataProviderForAPITest")
  public Object[] createData() {
    Integer[] data = new Integer[10];
    int numberOfResultsOnPage = 5;
    for (int i = 0; numberOfResultsOnPage <= 50; i++) {
      data[i] = numberOfResultsOnPage;
      numberOfResultsOnPage = numberOfResultsOnPage + 5;
    }
    return data;
  }
}
