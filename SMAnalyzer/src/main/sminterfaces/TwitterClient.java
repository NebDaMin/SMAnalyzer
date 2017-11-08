package main.sminterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import smsdk.twitter.TwitterAPI;

public class TwitterClient {

    private static final String CLIENT_ID = "7Jhzqrv72HDSbTNVHzzt0VIx8";
    private static final String CLIENT_SECRET = "TAt2xMHO0VXBuKA4WtdPwa4SnZ0qShCwX4x6E2Nq9sgSNkfpg2";
    static TwitterAPI twitterClient;
    private ArrayList<JSONObject> PostArrayList;

    public TwitterClient() {
        twitterClient = new TwitterAPI("----", CLIENT_SECRET);
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        JSONObject comments = twitterClient.getObject(params);
        ArrayList<JSONObject> commentsList = twitterClient.convertJsonItemsToList(comments);
        for (JSONObject comment : commentsList) {
            PostArrayList.add(comment);
            System.out.println("Comment id: " + comment.getString("id"));
            System.out.println("Message: " + comment.getString("text"));
            System.out.println("Created on: " + comment.getString("created_at"));
            System.out.println();
        }
    }

    public void clearArray() {
        PostArrayList.clear();
    }
}
