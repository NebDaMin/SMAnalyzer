package main.sminterfaces;

import smsdk.*;
import java.util.*;

import org.json.*;

public class FBClient {

    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FbAPI FbClient;
    private ArrayList<NormalizedComment> PostArrayList;

    //Initializes the FB Client with the API and initializes a new PostArrayList
    public FBClient() {
        FbClient = new FbAPI(APP_ID, APP_SECRET);
        PostArrayList = new ArrayList();
    }

    /* Entering a url with no post id will result in the first post being grabbed
       Adds the initial post to the PostArrayList. And includes if comments past the top-level should be included
    */
    public void fetchRandomPagePost(String pageName, Boolean children) {
        if (FbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("limit", 1);
            JSONObject pageFeed = FbClient.getConnections(pageName, "feed", params);
            List<JSONObject> postList = FbClient.convertJsonDataToList(pageFeed);
            for (JSONObject post : postList) {
                NormalizedComment normComment = new NormalizedComment();
                normComment.setFromFacebook(post);
                PostArrayList.add(normComment);
                System.out.println("Post id: " + post.getString("id"));
                System.out.println("Message: " + post.getString("message"));
                System.out.println("Created on: " + post.getString("created_time"));
                System.out.println();
                
                //Begins a recursive check for comments
                addCommentsToArrayList(post.getString("id"), children);
            }
        }
        System.out.println("PostArrayList size: " + PostArrayList.size());
    }

    //Retrieves the post for the specified Id. Adds the initial post to the PostArrayList
    public void fetchSpecificPagePost(String pageName, String postId, Boolean children) {
        if (FbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            JSONObject page = FbClient.getObject(pageName);
            String pageId = page.getString("id");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("fields", "id,message,shares,created_time");
            JSONObject post = FbClient.getObject(pageId + "_" + postId, params);
            NormalizedComment normComment = new NormalizedComment();
            normComment.setFromFacebook(post);
            PostArrayList.add(normComment);
            addCommentsToArrayList(post.getString("id"), children);
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public ArrayList<NormalizedComment> getPostArray() {
        return PostArrayList;
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    //Recursively find any and all comments and adds them to the PostArrayList
    public int addCommentsToArrayList(String id, boolean children) {
        FBCommentList comments = new FBCommentList(id, FbClient, null);
        if (comments.getData() == null) {
            return 1;
        } else if (comments.getHasNext()) {
            boolean searched = false;
            
            //comments.getHasNext() checks for if there is a cursor for more comments
            while (comments.getHasNext() || searched == false) {
                searched = true;
                for (int i = 0; i < comments.getCount(); i++) {
                    JSONObject comment = comments.getData().getJSONObject(i);
                    NormalizedComment normComment = new NormalizedComment();
                    if (!comment.has("message")) {
                        comment.put("message", "[No Text]");
                    }
                    normComment.setFromFacebook(comment);
                    PostArrayList.add(normComment);
                    String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                    System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                    //When children == true then comments past the top level are retrieved
                    if (children == true) {
                        addCommentsToArrayList(comment.getString("id"), children);
                    }
                }
                //when comments.getHasNext() == true then get the next token and generate a new comment list
                if (comments.getHasNext()) {
                    comments = new FBCommentList(id, FbClient, comments.getAfter());
                    searched = false;
                }
            }
        } else {
            /* comments.getHasNext() == false so the last set of comments has been reached 
               and needs to be added to the PostArrayList.
            */
            for (int i = 0; i < comments.getCount(); i++) {
                JSONObject comment = comments.getData().getJSONObject(i);
                NormalizedComment normComment = new NormalizedComment();
                normComment.setFromFacebook(comment);
                PostArrayList.add(normComment);
                String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                if (children == true) {
                    addCommentsToArrayList(comment.getString("id"), children);
                }
            }
        }
        return 0;
    }
}
