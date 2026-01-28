package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class DataAccessTests {
    DataAccess dataAccess = new SQLDataAccess();

    public DataAccessTests() throws DataAccessException {
    }

    @AfterEach
    public void clear(){
        dataAccess.clear();
    }

    @Test
    public void addUserTest(){
        UserData newUser = new UserData("newbie", "pass", "jerry@hotmail.com");
        Assertions.assertDoesNotThrow(()->dataAccess.addUser(newUser));
    }

    @Test
    public void addBadUser(){
        UserData newUser = new UserData("hi", "password", null);
        Assertions.assertThrows(Exception.class, ()-> dataAccess.addUser(newUser));
    }

    @Test
    public void getUserTest(){
        UserData newUser = new UserData("hi", "password", "hipassword@gmail.com");
        UserData differentUser = new UserData("hey", "pass", "email@hotmail.com");
        dataAccess.addUser(newUser);
        UserData result = dataAccess.getUser(newUser.username());
        Assertions.assertEquals(newUser.username(), result.username());
        Assertions.assertEquals(newUser.email(), result.email());
        Assertions.assertNotEquals(differentUser, result);
    }

    @Test
    public void getBadUser() {
        Assertions.assertNull(dataAccess.getUser("fake"));
    }

    @Test
    public void addAuthDataTest(){
        AuthData authData = new AuthData("user", "auoiwj-r923nn2n92nn vi-");
        Assertions.assertDoesNotThrow(()->dataAccess.addAuthData(authData));
    }

    @Test
    public void addBadAuthTest(){
        AuthData authData = new AuthData(null, null);
        Assertions.assertThrows(BadRequestException.class, ()->dataAccess.addAuthData(authData));
    }

    @Test
    public void getAuthTest(){
        AuthData authData = new AuthData("user", "auoiwj-r923nn2n92nn vi-");
        dataAccess.addAuthData(authData);
        Assertions.assertEquals(authData, dataAccess.getAuthData(authData.authToken()));
    }

    @Test
    public void getBadAuthTest(){
        Assertions.assertNull(dataAccess.getAuthData("hey"));
    }
}
