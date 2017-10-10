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
                JSONObject comments = fbClient.getConnections(post.getString("id"), "comments", null);
                List<JSONObject> commentList = fbClient.convertJsonDataToList(comments);
                i = i + commentList.size();
                for (JSONObject comment : commentList) {
                    PostArrayList.add(comment.getString("message"));
                    System.out.println("Comment: " + comment.getString("message"));
                    if (children == true) {
                        JSONObject childComments = fbClient.getConnections(comment.getString("id"), "comments", null);
                        List<JSONObject> childCommentsList = fbClient.convertJsonDataToList(childComments);
                        i = i + childCommentsList.size();
                        for (JSONObject childComment : childCommentsList) {
                            PostArrayList.add(childComment.getString("message"));
                            System.out.println("Comment: " + childComment.getString("message"));
                        }
                    }
                }
            }
            System.out.println("Total comments: " + i);
        }
    }

    public void fetchSpecificPagePost(String pageName, String postId, Boolean children) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            JSONObject page = fbClient.getObject(pageName);
            String pageId = page.getString("id");
            JSONObject post = fbClient.getObject(pageId + "_" + postId);
            PostArrayList.add(post.getString("message"));
            System.out.println("Post id: " + post.getString("id"));
            System.out.println("Message: " + post.getString("message"));
            System.out.println("Created on: " + post.getString("created_time"));
            System.out.println();
            JSONObject comments = fbClient.getConnections(post.getString("id"), "comments", null);
            List<JSONObject> commentList = fbClient.convertJsonDataToList(comments);
            i = i + commentList.size();
            for (JSONObject comment : commentList) {
                PostArrayList.add(comment.getString("message"));
                System.out.println("Comment: " + comment.getString("message"));
                if (children == true) {
                    JSONObject childComments = fbClient.getConnections(comment.getString("id"), "comments", null);
                    List<JSONObject> childCommentsList = fbClient.convertJsonDataToList(childComments);
                    i = i + childCommentsList.size();
                    for (JSONObject childComment : childCommentsList) {
                        PostArrayList.add(childComment.getString("message"));
                        System.out.println("Comment: " + childComment.getString("message"));
                    }
                }
            }

            System.out.println("Total comments: " + i);
        }
    }

    public ArrayList<String> getPostArray() {
        return PostArrayList;
    }

    public void clearArray() {
        PostArrayList.clear();
    }
}
