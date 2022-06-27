package View;

import Model.IModel;
import Model.Model;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = new File("src/main/java/View/MyView.fxml").toURI().toURL();
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();

        IModel model = new Model();
        ViewModel viewModel = new ViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
