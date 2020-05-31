package com.home.services.api;

import static contexts.ScenarioContext.Context.HTTP_RESPONSE;

import com.home.helpers.RestAPIHelper;
import contexts.ScenarioContext;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsersService {

  public Response getUser() {
    return RestAPIHelper.get();
  }

  public void getUserByNationality(String nationality) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("nat", nationality);
    Response response = RestAPIHelper.getWithQueryParameters(parameters);
    ScenarioContext.setContext(HTTP_RESPONSE, response);
  }

  public void getUsers(int pageNumber, int numberOfResults, String seed) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("page", pageNumber);
    parameters.put("results", numberOfResults);
    parameters.put("seed", seed);
    Response response = RestAPIHelper.getWithQueryParameters(parameters);
    ScenarioContext.setContext(HTTP_RESPONSE, response);
  }


  public void getUsers(int numberOfResults) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("results", numberOfResults);
    Response response = RestAPIHelper.getWithQueryParameters(parameters);
    ScenarioContext.setContext(HTTP_RESPONSE, response);
  }

  public void getUserWithPasswordConstraints(String passwordConstrains) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("password", passwordConstrains);
    Response response = RestAPIHelper.getWithQueryParameters(parameters);
    ScenarioContext.setContext(HTTP_RESPONSE, response);
  }
}
