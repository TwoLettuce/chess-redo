package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    UserData getUser(String username);

    void addUser(UserData userData);

    void addAuthData(AuthData authData);

    AuthData retrieveAuthData(String authToken);

    void removeAuth(String authToken);

    void clear();
}
