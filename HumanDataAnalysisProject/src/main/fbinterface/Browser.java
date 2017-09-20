package main.fbinterface;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.*;
import com.restfb.Version;
import com.restfb.scope.ScopeBuilder;
import com.restfb.scope.UserDataPermissions;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Dialog<ButtonType> {

    public static final String SUCCESS_URL = "https://www.facebook.com/connect/login_success.html";
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    private String code;

    private String appId;
    private String appSecret;
    private AccessToken userToken;

    public Browser(String appId, String appSecret) {
        Pane pane = new Pane();
        this.appId = appId;
        this.appSecret = appSecret;

        pane.getChildren().add(browser);
        getDialogPane().setContent(pane);
        showLogin();
    getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    public void showLogin() {
        DefaultFacebookClient fbClient = new DefaultFacebookClient();
        ScopeBuilder scope = new ScopeBuilder();
        scope.addPermission(UserDataPermissions.USER_STATUS);
        scope.addPermission(UserDataPermissions.USER_ABOUT_ME);
        scope.addPermission(UserDataPermissions.USER_POSTS);
        String loadUrl = fbClient.getLoginDialogUrl(appId, SUCCESS_URL, scope);
        webEngine.load(loadUrl + "&display=popup&response_type=code");
        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                        return;
                    }

                    String url = webEngine.getLocation();

                    if (url.equals("https://www.facebook.com/dialog/close")) {
                        System.out.println("dialog closed");
                        System.exit(0);
                    }

                    if (url.startsWith(SUCCESS_URL)) {
                        System.out.println(url);
                        int pos = url.indexOf("code=");
                        code = url.substring(pos + "code=".length());
                        userToken = fbClient.obtainUserAccessToken(appId, appSecret, SUCCESS_URL, code);
                    }
                });
    }

    public AccessToken getUserToken() {
        return userToken;
    }
}
