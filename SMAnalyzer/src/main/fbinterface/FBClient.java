package main.fbinterface;

import com.restfb.*;
import com.restfb.types.*;
import java.util.ArrayList;

public class FBClient extends DefaultFacebookClient {

    public static AccessToken accessToken = null;
    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FacebookClient fbClient;
    public Browser browser;
    private ArrayList<String> PostArrayList;

    public FBClient() {
        accessToken = this.obtainAppAccessToken(APP_ID, APP_SECRET);
        fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
        PostArrayList = new ArrayList();
    }

    public void viewToken() {
        if (accessToken == null) {
            System.out.println("Access token is null");
        } else {
            System.out.println(accessToken.getAccessToken());
        }
    }

    public void fetchPagePost(String pageName, Boolean children) {
        if (accessToken == null) {
            System.out.println("Access token is null");
        } else {
            fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
            Page page = fbClient.fetchObject(pageName, Page.class);
            int i = 0;
            Connection<Post> pageFeed = fbClient.fetchConnection(page.getId() + "/feed", Post.class, Parameter.with("limit", 1));
            for (Post post : pageFeed.getData()) {
                PostArrayList.add(post.getMessage());
                System.out.println("Message: " + post.getMessage());
                System.out.println("Created on: " + post.getCreatedTime());
                System.out.println();
                Connection<Comment> comments = fbClient.fetchConnection(post.getId() + "/comments", Comment.class);
                i = i + comments.getData().size();
                for (Comment comment : comments.getData()) {
                    PostArrayList.add(comment.getMessage());
                    System.out.println("Comment: " + comment.getMessage());
                    if (children == true) {
                        Connection<Comment> childComments = fbClient.fetchConnection(comment.getId() + "/comments", Comment.class);
                        i = i + childComments.getData().size();
                        for (Comment childComment : childComments.getData()) {
                            PostArrayList.add(childComment.getMessage());
                            System.out.println("Comment: " + childComment.getMessage());
                        }
                    }
                }
            }
            System.out.println("Total comments: " + i);
        }
    }

    public ArrayList<String> getPostArray() {
        return PostArrayList;
    }
    
    public void clearArray(){
        PostArrayList.clear();
    }
}
