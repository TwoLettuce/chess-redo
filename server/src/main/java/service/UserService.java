package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData userData) throws AlreadyTakenException, BadRequestException, InternalServerErrorException {
        if (dataAccess.getUser(userData.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData encryptedUserData = new UserData(userData.username(), cryptographizePassword(userData.password()), userData.email());
        dataAccess.addUser(encryptedUserData);
        AuthData authData = new AuthData(userData.username(), generateToken());
        dataAccess.addAuthData(authData);
        return authData;
    }

    public AuthData login(LoginRequest loginRequest) throws BadRequestException, UserNotFoundException, InternalServerErrorException {
        if (dataAccess.getUser(loginRequest.username()) == null ||
                !decryptographizePassword(loginRequest.password(), dataAccess.getUser(loginRequest.username()).password())){
            throw new UserNotFoundException("Error: unauthorized");
        }
        AuthData authData = new AuthData(loginRequest.username(), generateToken());
        dataAccess.addAuthData(authData);
        return authData;
    }

    public void logout(String authToken) throws NotLoggedInException, InternalServerErrorException {
        if (dataAccess.getAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        dataAccess.removeAuth(authToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String cryptographizePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean decryptographizePassword(String password, String crypographizedPassword){
        return BCrypt.checkpw(password, crypographizedPassword);
    }
}
