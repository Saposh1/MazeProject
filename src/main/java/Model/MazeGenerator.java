package Model;

import algorithms.mazeGenerators.Maze;

import java.util.Arrays;

public class MazeGenerator {
    public static void main(String[] args) {
        MazeGenerator generator = new MazeGenerator();
        Maze maze = generator.generateRandomMaze(5, 5);
        System.out.println(maze.toString());
    }

    public Maze generateRandomMaze(int rows, int cols){
        Maze maze = new Maze(rows,cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze.setCell((int) Math.round(Math.random()),i,j);
            }
        }
        return maze;
    }
}
