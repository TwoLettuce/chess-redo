package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    ArrayList<UserData> users = new ArrayList<>();

    ArrayList<AuthData> authenticatedUsers = new ArrayList<>();
    @Override
    public UserData getUser(String username) {
        for (UserData user : users){
            if (Objects.equals(user.username(), username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public void addAuthData(AuthData authData) {
        authenticatedUsers.add(authData);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
