package Model;

import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.search.ASearchingAlgorithm;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    Maze getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze();
    boolean finish();
    Solution getSolution() ;
    void updateConf(int row, int col,String solAlg,String genAlg);
    String getGenConf();
    String getSolveConf();
}
