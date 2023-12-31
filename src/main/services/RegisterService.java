package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import request.RegisterRequest;
import response.RegisterResponse;

/**
Service for HTTP request to register a user to the database
 */
public class RegisterService {

    /**
    registers a user into the server database
    @return RegisterResponse an object containing all response data
     */
    public RegisterResponse register(RegisterRequest r) {
        RegisterResponse registerResponse = new RegisterResponse();
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();

        //take care of any errors
        if (r.getPassword()==null || r.getUsername()==null || r.getEmail()==null) {
            registerResponse.setMessage("Error: bad request");
            return registerResponse;
        }
        try {
            userDAO.Find(r.getUsername());
            registerResponse.setMessage("Error: already taken");
            return registerResponse;
        } catch (DataAccessException ignored) {}

        //update database
        try {
            AuthToken authToken = new AuthToken();
            authToken.setUsername(r.getUsername());
            authDAO.Insert(authToken);

            User user = new User();
            user.setUsername(r.getUsername());
            user.setPassword(r.getPassword());
            user.setEmail(r.getEmail());

            userDAO.Insert(user);

            //fill in response
            registerResponse.setUsername(r.getUsername());
            registerResponse.setAuthToken(authToken.getAuthToken());
        } catch (DataAccessException e) {
            registerResponse.setMessage(e.getMessage());
        }


        //String s = "username: " + registerResponse.getUsername() + ", authToken: " + authToken.getAuthToken(); //have constructor in response that takes in

        return registerResponse;
    }

}


