package main.sminterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import smsdk.*;

public class YTClient {

    private static final String API_KEY = "AIzaSyBsAXJxBUV4VvMFSe8GxXnmxgkwiTU7Yhs";
    static YtAPI ytClient;
    private ArrayList<JSONObject> PostArrayList;

    public YTClient() {
        ytClient = new YtAPI(API_KEY);
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String type, String id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (type.equals("user")) {
            params.put("forUsername", id);
            params.put("part", "id");
            JSONObject channel = ytClient.getObject("channels", params);
            JSONArray items = channel.getJSONArray("items");
            id = items.getJSONObject(0).getString("id");
            type = "channel";
            params.clear();
        }
        params.put(type + "Id", id);
        params.put("textFormat", "plainText");
        params.put("part", "snippet,replies");
        JSONObject comments = ytClient.getObject("commentThreads", params);
        ArrayList<JSONObject> commentsList = ytClient.convertJsonItemsToList(comments);
        for (JSONObject comment : commentsList) {
            PostArrayList.add(comment);
            System.out.println("Comment id: " + comment.getString("id"));
            System.out.println("Message: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("textDisplay"));
            System.out.println("Created on: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("publishedAt"));
            System.out.println();
        }
    }
    
    
    public void clearArray() {
        PostArrayList.clear();
    }
}
