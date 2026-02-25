package server;

import com.google.gson.Gson;
import model.AuthData;
import model.result.CreateGameResult;
import model.GameData;
import model.request.JoinRequest;
import model.UserData;
import model.request.LoginRequest;
import model.result.ListGamesResult;
import ui.EscapeSequences;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(UserData userData) throws Exception {
        HttpRequest req = buildRequest("/user", "POST", userData, null);
        HttpResponse<String> response = sendRequest(req);
        if (response != null && wasSuccessful(response.statusCode())){
            return gson.fromJson(response.body(), Map.class).get("authToken").toString();
        }
        throw new Exception("Could not register");
    }

    public String login(LoginRequest loginRequest) throws Exception {
        HttpRequest req = buildRequest("/session", "POST", loginRequest, null);
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, AuthData.class).authToken();
    }

    public void logout(String authToken) throws Exception {
        HttpRequest req = buildRequest("/session", "DELETE", null, authToken);
        HttpResponse<String> response = sendRequest(req);
        handleResponse(response, null);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        HttpRequest req = buildRequest("/game", "GET", null, authToken);
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, ListGamesResult.class).games();
    }

    public int createGame(String authToken, String gameName) throws Exception {
        HttpRequest req = buildRequest("/game", "POST", Map.of("gameName", gameName), authToken);
        HttpResponse<String> response = sendRequest(req);
        return handleResponse(response, CreateGameResult.class).gameID();
    }

    public void joinGame(String authToken, JoinRequest joinRequest){

    }

    public void clear() throws Exception {
        HttpRequest req = buildRequest("/db", "DELETE", null, null);
        sendRequest(req);
    }

    public void makeMove(String authToken, GameData movedGame){

    }

    private HttpRequest buildRequest(String path, String method, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
        if (authToken != null){
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: could not register user\n" + e.getMessage());
            throw new Exception("bruh");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        if (wasSuccessful(response.statusCode())){
            if (responseClass == null){
                return null;
            } else {
                return gson.fromJson(response.body(), responseClass);
            }
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + gson.fromJson(response.body(), HashMap.class));
            throw new Exception("Could not logout");
        }
    }

    private boolean wasSuccessful(int statusCode){
        return statusCode/100==2;
    }
}
