package main.sminterfaces;

import smsdk.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.json.*;

public class FBClient {

    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FbAPI fbClient;
    private ArrayList<NormalizedComment> PostArrayList;

    public FBClient() {
        fbClient = new FbAPI(APP_ID, APP_SECRET);
        PostArrayList = new ArrayList();
    }

    public void fetchRandomPagePost(String pageName, Boolean children, Boolean file) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("limit", 1);
            JSONObject pageFeed = fbClient.getConnections(pageName, "feed", params);
            List<JSONObject> postList = fbClient.convertJsonDataToList(pageFeed);
            for (JSONObject post : postList) {
                NormalizedComment normComment = new NormalizedComment();
                normComment.setFromFacebook(post);
                PostArrayList.add(normComment);
                System.out.println("Post id: " + post.getString("id"));
                System.out.println("Message: " + post.getString("message"));
                System.out.println("Created on: " + post.getString("created_time"));
                System.out.println();

                getComments(post.getString("id"), children);
            }
        }
        System.out.println("PostArrayList size: " + PostArrayList.size());
    }

    public void fetchSpecificPagePost(String pageName, String postId, Boolean children, Boolean file) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            JSONObject page = fbClient.getObject(pageName);
            String pageId = page.getString("id");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("fields", "id,message,shares,created_time");
            JSONObject post = fbClient.getObject(pageId + "_" + postId, params);
            NormalizedComment normComment = new NormalizedComment();
            normComment.setFromFacebook(post);
            PostArrayList.add(normComment);
            System.out.println("Post id: " + post.getString("id"));
            System.out.println("Message: " + post.getString("message"));
            System.out.println("Created on: " + post.getString("created_time"));
            try {
                System.out.println("Shares: " + post.getJSONObject("shares").get("count"));
            } catch (JSONException ex) {
            }
            System.out.println();

            getComments(post.getString("id"), children);
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public ArrayList<NormalizedComment> getPostArray() {
        return PostArrayList;
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public int getComments(String id, boolean children) {
        FBCommentList comments = new FBCommentList(id, fbClient, null);
        if (comments.getData() == null) {
            return 1;
        } else {
            if (comments.getHasNext()) {
                boolean searched = false;
                while (comments.getHasNext() || searched == false) {
                    searched = true;
                    for (int i = 0; i < comments.getCount(); i++) {
                        JSONObject comment = comments.getData().getJSONObject(i);
                        NormalizedComment normComment = new NormalizedComment();
                        normComment.setFromFacebook(comment);
                        PostArrayList.add(normComment);
                        String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                        System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                        if (children == true) {
                            getComments(comment.getString("id"), children);
                        }
                    }
                    if (comments.getHasNext()) {
                        comments = new FBCommentList(id, fbClient, comments.getAfter());
                        searched = false;
                    }
                }
            } else {
                for (int i = 0; i < comments.getCount(); i++) {
                    JSONObject comment = comments.getData().getJSONObject(i);
                    NormalizedComment normComment = new NormalizedComment();
                    normComment.setFromFacebook(comment);
                    PostArrayList.add(normComment);
                    String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                    System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                    if (children == true) {
                        getComments(comment.getString("id"), children);
                    }
                }
            }
        }
        return 0;
    }
}
