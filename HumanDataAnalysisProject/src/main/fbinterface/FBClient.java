package main.fbinterface;

import com.restfb.*;
import com.restfb.types.*;

public class FBClient extends DefaultFacebookClient {

    public static AccessToken accessToken = null;
    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FacebookClient fbClient;
    public Browser browser;

    public FBClient() {
        accessToken = this.obtainAppAccessToken(APP_ID, APP_SECRET);
        fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
    }

    public void viewToken() {
        if (accessToken == null) {
            System.out.println("Access token is null");
        } else {
            System.out.println(accessToken.getAccessToken());
        }
    }

    public void fetchPagePost(String pageName) {
        if (accessToken == null) {
            System.out.println("Access token is null");
        } else {
            fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
            Page page = fbClient.fetchObject(pageName, Page.class);
            Connection<Post> pageFeed = fbClient.fetchConnection(page.getId()+"/feed", Post.class,Parameter.with("limit", 5));
            for(Post post : pageFeed.getData()){
                System.out.println("Message: " + post.getMessage());
                System.out.println("Created on: " + post.getCreatedTime());
                System.out.println();
            }
        }
    }
}
