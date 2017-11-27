package main.sminterfaces;

import java.io.IOException;
import smsdk.reddit.*;
import smsdk.*;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RedditClient {

    private final String ClientId = "tgM2m86Uy_uwKw";
    private final String ClientSecret = "9PWGdLhnmTE8S0biLTHbKOtGHe0";
    private RedditAPI RedditClient;
    private RedditToken Token;
    private RedditOAuth RedditOAuth;
    private ArrayList<NormalizedComment> PostArrayList;

    public RedditClient() {
        RedditClient = new RedditAPI(ClientId, ClientSecret);
        RedditOAuth = new RedditOAuth(RedditClient);
        Token = RedditOAuth.tokenAppOnly();
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String id) {
        RedditRequest request = new RedditRequest("/comments/"+id+".json");
        ArrayList<JSONObject> list = parse(RedditClient.get(Token, request));
        for(JSONObject obj : list){
            NormalizedComment normComment = new NormalizedComment();
            normComment.setFromReddit(obj);
        }
    }

    
    public ArrayList<JSONObject> parse(String jsonText){
        JSONArray json = Utility.parseJsonArray(jsonText);
        return null;
    }
    
    public void clearArray() {
        PostArrayList.clear();
    }
    
    public ArrayList<NormalizedComment> getPostArray(){
        return PostArrayList;
    }
}
