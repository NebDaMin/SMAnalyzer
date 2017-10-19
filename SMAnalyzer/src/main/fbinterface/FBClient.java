package main.fbinterface;

import fbapi.*;
import java.util.*;
import org.json.*;

public class FBClient {

    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FbAPI fbClient;
    private ArrayList<String> PostArrayList;

    public FBClient() {
        fbClient = new FbAPI(APP_ID, APP_SECRET);
        PostArrayList = new ArrayList();
    }

    public void fetchRandomPagePost(String pageName, Boolean children) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("limit", 1);
            JSONObject pageFeed = fbClient.getConnections(pageName, "feed", params);
            List<JSONObject> postList = fbClient.convertJsonDataToList(pageFeed);
            for (JSONObject post : postList) {
                PostArrayList.add(post.getString("message"));
                System.out.println("Post id: " + post.getString("id"));
                System.out.println("Message: " + post.getString("message"));
                System.out.println("Created on: " + post.getString("created_time"));
                System.out.println();

                getComments(post.getString("id"));
            }
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public void fetchSpecificPagePost(String pageName, String postId, Boolean children) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            JSONObject page = fbClient.getObject(pageName);
            String pageId = page.getString("id");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("fields", "id,message,shares,created_time");
            JSONObject post = fbClient.getObject(pageId + "_" + postId, params);
            PostArrayList.add(post.getString("message"));
            System.out.println("Post id: " + post.getString("id"));
            System.out.println("Message: " + post.getString("message"));
            System.out.println("Created on: " + post.getString("created_time"));
            try {
                System.out.println("Shares: " + post.getJSONObject("shares").get("count"));
            } catch (JSONException ex) {
            }
            System.out.println();

            getComments(post.getString("id"));
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public ArrayList<String> getPostArray() {
        return PostArrayList;
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public int getComments(String id) {
        FBCommentList comments = new FBCommentList(id, fbClient, null);
        if (comments.getData() == null) {
            return 1;
        } else {
            if (comments.getHasNext()) { //Escapes on second run because next got set to false. FIX
                boolean searched = false;
                while (comments.getHasNext() || searched == false) {
                    searched = true;
                    for (int i = 0; i < comments.getCount(); i++) {
                        JSONObject comment = comments.getData().getJSONObject(i);
                        PostArrayList.add(comment.getString("message"));
                        System.out.println("Comment: " + comment.getString("message"));
                        getComments(comment.getString("id"));
                    }
                    if (comments.getHasNext()) {
                        comments = new FBCommentList(id, fbClient, comments.getAfter());
                        searched = false;
                    }
                }
            } else {
                for (int i = 0; i < comments.getCount(); i++) {
                    JSONObject comment = comments.getData().getJSONObject(i);
                    PostArrayList.add(comment.getString("message"));
                    System.out.println("Comment: " + comment.getString("message"));
                    getComments(comment.getString("id"));
                }
            }
        }
        return 0;
    }
}
