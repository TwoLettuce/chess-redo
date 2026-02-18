package server;

import com.google.gson.Gson;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import model.request.LoginRequest;
import ui.EscapeSequences;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String register(UserData userData) throws Exception {
        HttpRequest req = buildRequest("/user", "POST", userData);
        HttpResponse<String> response = sendRequest(req);
        if (response != null && wasSuccessful(response.statusCode())){
            return gson.fromJson(response.body(), Map.class).get("authToken").toString();
        }
        throw new Exception("Could not register");
    }

    public String login(LoginRequest loginRequest) throws Exception {
        HttpRequest req = buildRequest("/session", "POST", loginRequest);
        HttpResponse<String> response = sendRequest(req);
        if (response != null && wasSuccessful(response.statusCode())){
            return response.body();
        }
        throw new Exception("Could not login");

    }

    public void logout(String authToken){

    }

    public Collection<GameData> listGames(String authToken){
        return null;
    }

    public int createGame(String authToken, String gameName){
        return -0;
    }

    public void joinGame(String authToken, JoinRequest joinRequest){

    }

    public void clear(){
        HttpRequest req = buildRequest("/db", "DELETE", null);
        sendRequest(req);
    }

    public void makeMove(String authToken, GameData movedGame){

    }

    private HttpRequest buildRequest(String path, String method, Object body){
        return HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: could not register user\n" + e.getMessage());
            return null;
        }
    }
    private boolean wasSuccessful(int statusCode){
        return statusCode/100==2;
    }
}
