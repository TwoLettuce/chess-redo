package service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    private static final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private static final UserService userService = new UserService(dataAccess);
    private static final DataService dataService = new DataService(dataAccess);

    @BeforeEach
    public void clearDB(){
        dataService.clear();
    }

    @Test
    public void registerNormal(){
        UserData user1 = new UserData("user1", "password1", "email1");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(user1));
        UserData user2 = new UserData("user2", "password2", "email2");
        Assertions.assertDoesNotThrow(()-> userService.registerUser(user2));
    }

    @Test
    public void registerExisting(){
        UserData user1 = new UserData("user1", "password1", "email1");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(user1));
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.registerUser(user1));
        UserData user1again = new UserData("user1", "pass", "email");
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.registerUser(user1again));
    }


    @Test
    public void loginNormal() throws AlreadyTakenException {
        UserData user1 = new UserData("user1", "password1", "email1");
        userService.registerUser(user1);

    }

    @Test
    public void loginWrongCredentials(){

    }
}
