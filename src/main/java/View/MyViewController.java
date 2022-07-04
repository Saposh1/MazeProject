package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import static java.lang.System.exit;

public class MyViewController implements Initializable, Observer ,IView{
    public MyViewModel myViewModel;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Pane mazePane;
    public Label playerCol;
    public Media media;
    public MediaPlayer mediaPlayer;
    public Button music;
    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    private Stage stage;



    public void setViewModel(MyViewModel myViewModel, Stage prime) {
        this.stage = prime;
        this.myViewModel = myViewModel;
        this.myViewModel.addObserver(this);
    }
    public Stage getPrimeStage(){return this.stage;}
    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);

        mazeDisplayer.widthProperty().bind(mazePane.widthProperty()); // for resizeable maze
        mazeDisplayer.heightProperty().bind(mazePane.heightProperty());

        media=new Media(new File("resources/music/gameMusic.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(1);
        mediaPlayer.play();
    }
    public void mouseScrolled(ScrollEvent scrollEvent) {
        if(scrollEvent.isControlDown()) {
            double zoomFactor = 1.5;
            if (scrollEvent.getDeltaY() <= 0) {
                // zoom out
                zoomFactor = 1 / zoomFactor;
            }
            zoomIn(mazeDisplayer, zoomFactor);
        }
    }

    public void zoomIn(MazeDisplayer pane, double factor){
        Scale newScale = new Scale();
        newScale.setX(pane.getScaleX() * factor);
        newScale.setY(pane.getScaleY() * factor);
        newScale.setPivotX(pane.getScaleX());
        newScale.setPivotY(pane.getScaleY());
        pane.getTransforms().add(newScale);

    }
    public void generateMaze(ActionEvent actionEvent) {
        int rows,cols;
        try {
            rows = Integer.valueOf(textField_mazeRows.getText());
            cols = Integer.valueOf(textField_mazeColumns.getText());
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Enter integers bigger than 2.");
            alert.show();
            return;
        }
        mediaPlayer.stop();
        media=new Media(new File("resources/music/music.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(1);
        mediaPlayer.play();
        mazeDisplayer.setSolution(null);
        myViewModel.generateMaze(rows, cols);
    }

    public void solveMaze(ActionEvent actionEvent) {
        if(myViewModel.getMaze()==null){
            Alert alert = new Alert(Alert.AlertType.ERROR,"No maze to solve.");
            alert.show();
            return;
        }
        myViewModel.solveMaze(myViewModel.getMaze());
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        if (chosen != null) {
            try {
                ObjectInputStream maze = new ObjectInputStream(new FileInputStream(chosen.getAbsolutePath()));
                Maze new1 = (Maze) maze.readObject();
                maze.close();
                mazeDisplayer.setMaze(new1);
                mazeDisplayer.draw();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        myViewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated":{ mazeGenerated(); mazeDisplayer.requestFocus(); break;}
            case "player moved" :{playerMoved();mazeDisplayer.requestFocus(); break;}
            case "finish" : {win();mazeDisplayer.requestFocus();break;}
            case "maze solved" :{ mazeSolved();mazeDisplayer.requestFocus(); break;}
            default :{ System.out.println("Not implemented change: " + change);}
        }
    }

    public void win() {
        //Adding scene to the stage
        mediaPlayer.stop();
        media=new Media(new File("resources/music/winSong.mp3").toURI().toString());
        MediaPlayer mediaPlayerWin = new MediaPlayer(media);
        mediaPlayerWin.setAutoPlay(true);
        mediaPlayerWin.setVolume(0.2);
        mediaPlayerWin.play();
        Image image = null;
        try {
            image = new Image(new FileInputStream(mazeDisplayer.getImageFileNameWin()));
        } catch (FileNotFoundException e) {
            System.out.println("No win image found!");
        }
        ImageView view=new ImageView(image);
        //Setting the preserve ratio of the image view
        view.setPreserveRatio(true);

        Group root = new Group(view);

        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setTitle("Winner");
        stage.setOnCloseRequest(e -> {
            // disable option to quit the window
            e.consume();
        });
        stage.setResizable(false);
        Button butPlayAgain = new Button();
        butPlayAgain.setStyle("-fx-font-size:20;-fx-background-color: linear-gradient(saddlebrown,sandybrown);-fx-font-family: \"Showcard Gothic\";");

        Button butQuit = new Button();
        butQuit.setStyle("-fx-font-size:20;-fx-background-color: linear-gradient(saddlebrown,sandybrown);-fx-font-family: \"Showcard Gothic\";");

        butPlayAgain.setText("PLAY AGAIN");
        butPlayAgain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    // set background music
                    mediaPlayerWin.stop();
                    mediaPlayer.play();
                    music.setText("Turn Off Music!");
                    generateMaze(e);
                    stage.close();
                }
                catch (Exception ignored) {}
            }});
        butQuit.setText("QUIT GAME");
        butQuit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { quitGame(e);}}
        );

        root.getChildren().add(butPlayAgain);
        root.getChildren().add(butQuit);


        GridPane grid = new GridPane();
        grid.add(butPlayAgain,0,0);
        grid.add(butQuit,0,1);
        root.getChildren().add(grid);
        Scene scene = new Scene(root, 700, 460);
        stage.setScene(scene);
        //Displaying the contents of the stage
        stage.show();
        mazeDisplayer.requestFocus();

    }
    public void setProperties(ActionEvent actionEvent) {
        mediaPlayer.stop();
        media=new Media(new File("resources/music/PropMusic.mp3").toURI().toString());
        MediaPlayer mediaPlayerWin = new MediaPlayer(media);
        mediaPlayerWin.setAutoPlay(true);
        mediaPlayerWin.setVolume(1);
        mediaPlayerWin.play();
        Image image = new Image("images/screen4.jpg");

        ImageView view=new ImageView(image);
        Group root = new Group(view);

        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setTitle("Properties");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            // disable option to quit the window
            e.consume();
        });
        Label generateAlgorithm = new Label("Maze generation algorithm:");
        ComboBox<String> comboGenerate = new ComboBox<>();
        comboGenerate.getItems().addAll("EmptyMazeGenerator","SimpleMazeGenerator","MyMazeGenerator");
        String currGen = "";
        try{
            currGen = myViewModel.getGenAlg();
        } catch (Exception ignored) {
        }
        comboGenerate.setValue(currGen);
        Label solverAlgorithm = new Label("Maze solver algorithm:");
        ComboBox<String> comboSolver = new ComboBox<>();
        comboSolver.getItems().addAll("BreadthFirstSearch","DepthFirstSearch","BestFirstSearch");
        String currSolver = "";
        try{
            currSolver = myViewModel.getSolverAlg();
        } catch (Exception e) {
        }
        comboSolver.setValue(currSolver);

        root.getChildren().add(generateAlgorithm);
        root.getChildren().add(comboGenerate);
        GridPane grid = new GridPane();
        grid.add(generateAlgorithm,0,0);
        grid.add(comboGenerate,0,1);
        grid.add(solverAlgorithm, 0,2);
        grid.add(comboSolver, 0,3);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        root.getChildren().add(grid);
        Scene scene = new Scene(root, 600, 800);
        stage.setScene(scene);
        //Displaying the contents of the stage
        stage.show();

        // submit button
        Button submit = new Button();
        submit.setText("Submit");
        stage.getIcons().add(new Image("images/icon.png"));
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    myViewModel.updateConfig(comboGenerate.getValue(),comboSolver.getValue());
                    mediaPlayer.stop();
                    media=new Media(new File("resources/music/gameMusic.mp3").toURI().toString());
                    MediaPlayer mediaPlayerWin = new MediaPlayer(media);
                    mediaPlayerWin.setAutoPlay(true);
                    mediaPlayerWin.setVolume(0.2);
                    mediaPlayerWin.play();
                    stage.close();
                }
            });
        grid.add(submit,3,5);
        stage.setScene(scene);
        stage.show();

    }
    public void mazeSolved() {
        mediaPlayer.stop();
        media=new Media(new File("resources/music/sol.mp3").toURI().toString());
        MediaPlayer mediaPlayerWin = new MediaPlayer(media);
        mediaPlayerWin.setAutoPlay(true);
        mediaPlayerWin.setVolume(0.5);
        mediaPlayerWin.play();
        mazeDisplayer.setSolution(myViewModel.getSol());
        mazeDisplayer.draw();
    }

    public void playerMoved() {
        setPlayerPosition(myViewModel.getPlayerRow(), myViewModel.getPlayerCol());
    }

    public void mazeGenerated() {
        mazeDisplayer.drawMaze(myViewModel.getMaze());
    }

    public void howToPlay(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        Image image = new Image("images/help.JPG");
        grid.getChildren().add(new ImageView(image));

        Scene scene = new Scene(grid, 450, 800);


        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setTitle("Help");
        stage.setResizable(false);

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public void details(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        Image image = new Image("images/About.JPG");
        grid.getChildren().add(new ImageView(image));

        Scene scene = new Scene(grid, 800, 580);

        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setResizable(false);

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public void quitGame(ActionEvent actionEvent) {
        exit(0);
    }

    public void music(ActionEvent actionEvent) {
        switch(music.getText()){
            case "Turn off Music":
                mediaPlayer.pause();
                music.setText("Turn On Music");
                break;
            case "Turn On Music":
                mediaPlayer.play();
                music.setText("Turn off Music");
                break;
        }
        mazeDisplayer.requestFocus();

    }
    public void mouseDragged(MouseEvent mouseEvent) {

        double canvasHeight = mazeDisplayer.getHeight(); // canvasHeight
        double canvasWidth = mazeDisplayer.getWidth();// canvasWidth

        int rows = myViewModel.getMaze().getRow();
        int cols = myViewModel.getMaze().getColumn();

        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        // calculate current mouse position by cells.
        double mouseX = (int) ((mouseEvent.getX()) / cellWidth);
        double mouseY = (int) ((mouseEvent.getY()) / cellHeight);

        // UP DOWN LEFT RIGHT
        if (mouseY < myViewModel.getPlayerRow() && mouseX == myViewModel.getPlayerCol()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD8", "NUMPAD8", KeyCode.NUMPAD8, false, false, false, false));
        }
        if (mouseY > myViewModel.getPlayerRow() && mouseX == myViewModel.getPlayerCol()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD2", "NUMPAD2", KeyCode.NUMPAD2, false, false, false, false));
        }
        if (mouseX < myViewModel.getPlayerCol() && mouseY == myViewModel.getPlayerRow()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD4", "NUMPAD4", KeyCode.NUMPAD4, false, false, false, false));
        }
        if (mouseX > myViewModel.getPlayerCol() && mouseY == myViewModel.getPlayerRow()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD6", "NUMPAD6", KeyCode.NUMPAD6, false, false, false, false));
        }

        // diagonals
        if (mouseY < myViewModel.getPlayerRow() && mouseX > myViewModel.getPlayerCol()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD9", "NUMPAD9", KeyCode.NUMPAD9, false, false, false, false));
        }
        if (mouseY > myViewModel.getPlayerRow() && mouseX > myViewModel.getPlayerCol()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD3", "NUMPAD3", KeyCode.NUMPAD3, false, false, false, false));
        }
        if (mouseX < myViewModel.getPlayerCol() && mouseY > myViewModel.getPlayerRow()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD1", "NUMPAD1", KeyCode.NUMPAD1, false, false, false, false));
        }
        if (mouseX < myViewModel.getPlayerCol() && mouseY < myViewModel.getPlayerRow()) {
            myViewModel.movePlayer(new KeyEvent(KeyEvent.KEY_PRESSED, "NUMPAD7", "NUMPAD7", KeyCode.NUMPAD7, false, false, false, false));
        }
    }


        public void SaveFile(ActionEvent actionEvent) {
        if(myViewModel.getMaze()==null){
            Alert alert = new Alert(Alert.AlertType.ERROR,"No maze to save.");
            alert.show();
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showSaveDialog(null);
        ObjectOutputStream out;
        if (chosen != null) {
            try {
                out = new ObjectOutputStream(new FileOutputStream(chosen.getAbsolutePath()));
                out.writeObject(myViewModel.getMaze());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

