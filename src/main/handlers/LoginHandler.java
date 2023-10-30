package handlers;

import com.google.gson.Gson;
import services.LoginService;
import services.request.LoginRequest;
import services.response.LoginResponse;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class LoginHandler {

    public String handleRequest(Request request, Response response) {
        LoginService loginService = new LoginService();
        Gson gson = new Gson();

        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

        LoginResponse loginResponse = loginService.login(loginRequest);

        if (Objects.equals(loginResponse.getMessage(), "Error: unauthorized")) {
            response.status(401);
        }
        if (Objects.equals(loginResponse.getMessage(), "Error: already logged in")) {
            response.status(500);
        }

        return gson.toJson(loginResponse);
    }

}