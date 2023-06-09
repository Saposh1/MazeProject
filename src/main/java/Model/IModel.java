package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    Maze getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze(Maze maze);
    boolean finish();
    Solution getSolution() ;
    void updateConf(String solAlg,String genAlg);
    String getGenConf();
    String getSolveConf();
    void writeErrorToLog();

}
