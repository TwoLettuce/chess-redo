package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
    DataAccess dataAccess = new SQLDataAccess();

    public DataAccessTests() throws DataAccessException {
    }

    @Test
    public void addUserTest(){
        UserData newUser = new UserData("newbie", "pass", "jerry@hotmail.com");
        Assertions.assertDoesNotThrow(()->dataAccess.addUser(newUser));
    }

    @Test
    public void addBadUser(){
        UserData newUser = new UserData("hi", "password", null);
        Assertions.assertThrows()
    }
}
