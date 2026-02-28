package server;

import com.google.gson.Gson;
import exception.DataAccessException;
import model.AuthData;
import model.result.CreateGameResult;
import model.GameData;
import model.request.JoinRequest;
import model.UserData;
import model.request.LoginRequest;
import model.result.ListGamesResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(UserData userData) throws DataAccessException {
        HttpRequest req = buildRequest("/user", "POST", userData, null);
        HttpResponse<String> response = sendRequest(req);
        return Objects.requireNonNull(handleResponse(response, AuthData.class)).authToken();
    }

    public String login(LoginRequest loginRequest) throws DataAccessException {
        HttpRequest req = buildRequest("/session", "POST", loginRequest, null);
        HttpResponse<String> response = sendRequest(req);
        return Objects.requireNonNull(handleResponse(response, AuthData.class)).authToken();
    }

    public void logout(String authToken) throws DataAccessException {
        HttpRequest req = buildRequest("/session", "DELETE", null, authToken);
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        HttpRequest req = buildRequest("/game", "GET", null, authToken);
        HttpResponse<String> response = sendRequest(req);
        return Objects.requireNonNull(handleResponse(response, ListGamesResult.class)).games();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        HttpRequest req = buildRequest("/game", "POST", Map.of("gameName", gameName), authToken);
        HttpResponse<String> response = sendRequest(req);
        return Objects.requireNonNull(handleResponse(response, CreateGameResult.class)).gameID();
    }

    public void joinGame(String authToken, JoinRequest joinRequest) throws DataAccessException {
        HttpRequest req = buildRequest("/game", "PUT", joinRequest, authToken);
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    public void clear() throws DataAccessException {
        HttpRequest req = buildRequest("/db", "DELETE", null, null);
        sendRequest(req);
    }

//    public void makeMove(String authToken, GameData updatedGame){
//
//    }

    private HttpRequest buildRequest(String path, String method, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
        if (authToken != null){
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new DataAccessException(500, "Could not send request");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        if (wasSuccessful(response.statusCode())){
            if (responseClass == null){
                return null;
            } else {
                return gson.fromJson(response.body(), responseClass);
            }
        } else {
            DataAccessException ex = new DataAccessException((String) gson.fromJson(response.body(), Map.class).get("message"));
            ex.httpCode = response.statusCode();
            throw ex;
        }
    }

    private boolean wasSuccessful(int statusCode){
        return statusCode/100==2;
    }
}
