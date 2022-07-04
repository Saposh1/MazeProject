package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Image image = new Image("images/play.jpg");
        Button bt=new Button();
        bt.setText("Lets play!");
        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.getIcons().add(new Image("images/icon.png"));
        stage.setTitle("MapleStory");
        Image image2 = new Image("images/tap.png");  //pass in the image path
        Pane root = new Pane();
        root.getChildren().add(new ImageView(image));
        bt.setLayoutX(550);
        bt.setLayoutY(550);
        bt.setStyle("-fx-font-size:40;-fx-background-color: linear-gradient(saddlebrown,sandybrown);-fx-font-family: \"Showcard Gothic\";");
        root.getChildren().add(bt);
        stage.setScene(new Scene(root, 1100, 720));
        stage.getScene().setCursor(new ImageCursor(image2));
        stage.show();


        bt.setOnAction(new EventHandler<ActionEvent>() {
                           @Override
                           public void handle(ActionEvent e) {
                               stage.close();
                               URL url = null;
                               try {
                                   url = new File("src/main/java/View/MyView.fxml").toURI().toURL();
                               } catch (MalformedURLException ex) {
                                   throw new RuntimeException(ex);
                               }
                               FXMLLoader fxmlLoader = new FXMLLoader(url);
                               Parent root = null;
                               try {
                                   root = fxmlLoader.load();
                               } catch (IOException ex) {
                                   throw new RuntimeException(ex);
                               }
                               primaryStage.setTitle("MapleStory Maze Game");
                               primaryStage.getIcons().add(new Image("images/icon.png"));
                               primaryStage.setScene(new Scene(root, 1000, 700));

                               Image image = new Image("images/tap.png");  //pass in the image path
                               primaryStage.getScene().setCursor(new ImageCursor(image));
                               primaryStage.show();
                               MyModel myModel = new MyModel();
                               MyViewModel myViewModel = new MyViewModel(myModel);
                               MyViewController view = fxmlLoader.getController();
                               view.setViewModel(myViewModel,primaryStage);
                               myModel.start();
                               myViewModel.addObserver(view);
                           }});}

    public static void main(String[] args) {
        launch(args);
    }
}
