package main;

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

import main.fbinterface.*;
import main.humandataanalysisproject.*;

public class Main extends Application {
    static FBClient fbClient;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        fbClient = new FBClient();
        FlowPane mainPane = new FlowPane();
        Scene scene = new Scene(mainPane, 200, 200);

        Button btn1 = new Button("View Token");
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                fbClient.viewToken();
            }
        }
        );
        
        Button btn2 = new Button("Get Page Post");
        btn2.setOnAction(new EventHandler<ActionEvent>(){
           @Override
           public void handle(ActionEvent e){
               fbClient.fetchPagePost("StarWars");
           }
        });
        
        Button btn3 = new Button("Run Analysis");
        btn3.setOnAction(new EventHandler<ActionEvent>(){
           @Override
           public void handle(ActionEvent e){
               try { HumanDataAnalysisProject hda = new HumanDataAnalysisProject(); }
               catch(Exception ex){ }
            }
        });
        
        
        mainPane.getChildren().addAll(btn1, btn2, btn3);
        stage.setTitle("Human Data Analysis Project");
        stage.setScene(scene);
        stage.show();

    }
}
