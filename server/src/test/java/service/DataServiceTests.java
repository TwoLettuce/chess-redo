package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataServiceTests {
    public final MemoryDataAccess dataAccess = new MemoryDataAccess();
    public final DataService dataService = new DataService(dataAccess);
    public final UserService userService = new UserService(dataAccess);


    @Test
    public void testClear() throws AlreadyTakenException {
        userService.registerUser(new UserData("user1", "pass1", "email1"));
        Assertions.assertDoesNotThrow(dataService::clear);
        Assertions.assertNull(dataAccess.getUser("user1"));
    }
}
