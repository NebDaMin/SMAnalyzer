package main.fbinterface;

import com.restfb.*;
import com.restfb.FacebookClient.*;
import com.restfb.scope.*;
import com.restfb.types.*;
import java.util.List;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;

import main.humandataanalysisproject.*;

public class Main extends Application {

    public static AccessToken accessToken = null;
    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FacebookClient fbClient;
    public Browser browser;
    static FlowPane mainPane;
    public int userId;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        mainPane = new FlowPane();

        browser = new Browser(APP_ID, APP_SECRET);

        Button btn1 = new Button("Show Login");
        btn1.setAlignment(Pos.CENTER);
        Scene scene = new Scene(mainPane, 200, 200, Color.web(("#666970")));

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Optional<ButtonType> result = browser.showAndWait();
                if (result.get() == ButtonType.OK) {
                    accessToken = browser.getUserToken();
                } else if (result.get() == ButtonType.CANCEL) {
                    browser.close();
                }

            }
        }
        );

        Button btn2 = new Button("View Token");
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                accessToken = browser.getUserToken();
                if (accessToken == null) {
                    System.out.println("Access token is null");
                } else {
                    System.out.println(accessToken.getAccessToken());
                }
            }
        }
        );

        Button btn3 = new Button("Get Username");
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (accessToken == null) {
                    System.out.println("Access token is null");
                } else {
                    fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
                    User user = fbClient.fetchObject("me", User.class);
                    System.out.println("Username is " + user.getName());
                    
                }
            }
        }
        );

        Button btn4 = new Button("Get Test Post");
        btn4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (accessToken == null) {
                    System.out.println("Access token is null");
                } else {
                    fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);

                    Connection<Post> feed = fbClient.fetchConnection("me/feed", Post.class);
                    System.out.println("First item in feed: " + feed.getData().get(0).getMessage());
                    System.out.println("Comments on post: ");
                    Connection<Comment> comments = fbClient.fetchConnection(feed.getData().get(0).getId()+"/comments", Comment.class);
                    for(Comment comment : comments.getData()){
                        System.out.println("Comment by " + comment.getFrom().getName()+ ": "+ comment.getMessage());
                        System.out.println("Made at: " + comment.getCreatedTime().toString());
                        System.out.println();
                    }
                    
                }
            }
        });
        
        Button btn5 = new Button("Get Page Post");
        btn5.setOnAction(new EventHandler<ActionEvent>(){
           @Override
           public void handle(ActionEvent e){
               if(accessToken == null){
                    System.out.println("Access token is null");
               } else {
                   fbClient = new DefaultFacebookClient(accessToken.getAccessToken(), APP_SECRET);
                   Page page = fbClient.fetchObject("StarWars", Page.class);
                   
                   System.out.println(page.getAbout());
               }
           }
        });
        
        Button btn6 = new Button("Run Analysis");
        btn6.setOnAction(new EventHandler<ActionEvent>(){
           @Override
           public void handle(ActionEvent e){
               try { HumanDataAnalysisProject hda = new HumanDataAnalysisProject(); }
               catch(Exception ex){ }
            }
        });
        
        
        mainPane.getChildren().addAll(btn1, btn2, btn3, btn4, btn5, btn6);
        stage.setTitle("FB Login");
        stage.setScene(scene);
        stage.show();

    }
}
