package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

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

    public AuthData login() throws UserNotFoundException{

    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
