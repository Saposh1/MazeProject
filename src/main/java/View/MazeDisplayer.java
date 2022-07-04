package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Solution solution;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    private int goalCol;
    private int goalRow;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameSol = new SimpleStringProperty();
    StringProperty imageFileNameWin = new SimpleStringProperty();


    public MazeDisplayer() {

        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());

    }

    public String getImageFileNameWin() {
        return imageFileNameWin.get();
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public int getGoalCol() {
        return goalCol;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        if(solution!=null) {
            draw();
        }
    }

    public String getImageFileNameSol() {
        return imageFileNameSol.get();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameSol(String imageFileNameSol) {
        this.imageFileNameSol.set(imageFileNameSol);
    }

    public void setImageFileNameWin(String imageFileNameWin) {
        this.imageFileNameWin.set(imageFileNameWin);
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        goalCol = maze.getGoalPosition().getColumnIndex();
        goalRow = maze.getGoalPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();
        playerRow = maze.getStartPosition().getRowIndex();
        draw();
    }

    public void draw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRow();
            int cols = maze.getColumn();

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            if (solution != null)
                drawSolution(solution);
            drawPlayer(graphicsContext, cellHeight, cellWidth);
            drawGoal(graphicsContext, cellHeight, cellWidth);
        }
    }

    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double z = getGoalCol() * cellWidth;
        double y = getGoalRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image goalImage = null;
        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if (goalImage == null)
            graphicsContext.fillRect(z, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(goalImage, z, y, cellWidth, cellHeight);
    }


    private void drawSolution(Solution sol) {
        solution = sol;
        if (maze != null) {
            Image solImage = null;
            try {
                solImage = new Image(new FileInputStream(getImageFileNameSol()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no sol image file");
            }
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRow();
            int cols = maze.getColumn();
            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas
            if (solImage == null) {
                graphicsContext.setFill(Color.GREEN);
            }
            for (AState state : solution.getSolutionPath()
            ) {
                double x = ((MazeState) state).getPosition().getColumnIndex() * cellWidth;
                double y = ((MazeState) state).getPosition().getRowIndex() * cellHeight;

                if (solImage == null) {
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                } else {
                    graphicsContext.drawImage(solImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }


    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);
        Image wallImage = null;
        Image floorImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                if (maze.getCell(i, j) == 1) {
                    //if it is a wall:
                    if (wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                } else {
                    graphicsContext.drawImage(floorImage, x, y, cellWidth, cellHeight);

                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if (playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}


