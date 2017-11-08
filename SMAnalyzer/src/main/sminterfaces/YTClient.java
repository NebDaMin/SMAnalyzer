package main.sminterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import smsdk.*;

public class YTClient {

    private static final String API_KEY = "AIzaSyBsAXJxBUV4VvMFSe8GxXnmxgkwiTU7Yhs";
    static YtAPI ytClient;
    private ArrayList<NormalizedComment> PostArrayList;

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
            NormalizedComment normComment = new NormalizedComment();
            normComment.setFromFacebook(comment);
            System.out.println("Parent Comment: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("textOriginal") + "\r\n");
            if (comment.has("replies")) {
                try {
                    ArrayList<JSONObject> replies = new ArrayList<JSONObject>();
                    for (int i = 0; i < comment.getJSONObject("replies").getJSONArray("comments").length(); i++) {
                        replies.add(Utility.parseJson(comment.getJSONObject("replies").getJSONArray("comments").get(i).toString()));
                    }

                    for (JSONObject reply : replies) {
                        System.out.print("   | ");
                        System.out.print("\r\n    -> ");
                        System.out.println(reply.getJSONObject("snippet").getString("textOriginal"));
                    }

                    System.out.println();
                } catch (JSONException jex) {
                    System.out.println(jex);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public ArrayList<NormalizedComment> getPostArray() {
        return PostArrayList;
    }

}
