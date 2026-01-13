package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.NotLoggedInException;
import dataaccess.UserNotFoundException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import request.LoginRequest;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData userData) throws AlreadyTakenException{
        if (dataAccess.getUser(userData.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        dataAccess.addUser(userData);
        AuthData authData = new AuthData(userData.username(), generateToken());
        dataAccess.addAuthData(authData);
        return authData;
    }

    public AuthData login(LoginRequest loginRequest) throws UserNotFoundException {
        if (dataAccess.getUser(loginRequest.username()) == null ||
                !Objects.equals(dataAccess.getUser(loginRequest.username()).password(), loginRequest.password())){
            throw new UserNotFoundException("Error: unauthorized");
        }
        AuthData authData = new AuthData(loginRequest.username(), generateToken());
        dataAccess.addAuthData(authData);
        return authData;
    }

    public void logout(String authToken) throws NotLoggedInException {
        if (dataAccess.authenticateToken(authToken)){

        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
