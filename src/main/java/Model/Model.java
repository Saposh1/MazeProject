package Model;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Configurations;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class Model extends Observable implements IModel {
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private int cols;
    private int rows;
    private Solution solution;
    private Server servGen = new Server(5400,1000,new ServerStrategyGenerateMaze());
    private Server servSol = new Server(5402,1000,new ServerStrategySolveSearchProblem());

    public Model()
    {
        this.maze=null;
        this.playerRow=0;
        this.playerCol=0;
        this.solution=null;
        this.cols=10;
        this.rows=10;
    }

    @Override
    public void generateMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = ((byte[]) fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[mazeDimensions[0]*mazeDimensions[1] + 72];
                        is.read(decompressedMaze);
                        Maze maze = new Maze(decompressedMaze);

                        playerCol = maze.getStartPosition().getColumnIndex();
                        playerRow = maze.getStartPosition().getRowIndex();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers( "maze generated" );
    }

    public void solveMaze (Maze maze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5402, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                                toServer.flush();
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                                solution=mazeSolution;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers( "maze solved" );
    }
    public void start(){
        servGen.start();
        servSol.start();
    }
    public void stop(){
        servGen.stop();
        servSol.stop();
    }
    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        boolean changed = false;
        switch (direction) {
            case UP -> {
                if (playerRow > 0 && maze.getCell(playerRow-1,playerCol)==0){
                    movePlayer(playerRow - 1, playerCol);
                    changed=true;}
            }

            case DOWN -> {
                if (playerRow < maze.getRow() - 1 && maze.getCell(playerRow+1,playerCol)==0){
                    movePlayer(playerRow + 1, playerCol);
                    changed=true;}
            }
            case LEFT -> {
                if (playerCol > 0 && maze.getCell(playerRow,playerCol-1)==0){
                    movePlayer(playerRow, playerCol - 1);
                    changed=true;}
            }
            case RIGHT -> {
                if (playerCol < maze.getColumn() - 1 && maze.getCell(playerRow,playerCol+1)==0){
                    movePlayer(playerRow, playerCol + 1);
                    changed=true;}
            }
            case UPRIGHT -> {
                if (playerCol < maze.getColumn() - 1 && playerRow > 0 && maze.getCell(playerRow-1,playerCol+1)==0){
                    movePlayer(playerRow - 1, playerCol + 1);
                    changed=true;}
            }
            case UPLEFT -> {
                if (playerCol > 0 && playerRow > 0 && maze.getCell(playerRow-1,playerCol-1)==0){
                    movePlayer(playerRow -1, playerCol - 1);
                    changed=true;}
            }
            case DOWNLEFT -> {
                if (playerRow < maze.getRow() - 1 && playerCol > 0 && maze.getCell(playerRow+1,playerCol-1)==0 ){
                    movePlayer(playerRow+1, playerCol - 1);
                    changed=true;}
            }
            case DOWNRIGHT -> {
                if (playerRow < maze.getRow() - 1 && playerCol < maze.getColumn() - 1 && maze.getCell(playerRow+1,playerCol+1)==0){
                    movePlayer(playerRow+1, playerCol + 1);
                    changed=true;}
            }
        }
        if(changed=true){
            setChanged();
            if(finish()){
                notifyObservers("finish");
            }
            else{
                notifyObservers("update location");
            }
        }
    }

    public boolean finish() {
        return playerRow==maze.getGoalPosition().getRowIndex() &&playerCol==maze.getGoalPosition().getColumnIndex();
    }


    private void movePlayer(int row, int col){
        this.playerRow = row;
        this.playerCol = col;
        setChanged();
        notifyObservers("player moved");
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }



    @Override
    public void solveMaze() {
        //solve the maze
        solution = new Solution();
        setChanged();
        notifyObservers("maze solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void updateConf(int row, int col, String solAlg, String genAlg) {
        rows=row;
        cols=col;
        Configurations conf = Configurations.getInstance();
        conf.setGenAlgorithm(genAlg);
        conf.setSolverAlgorithm(solAlg);
        setChanged();
        notifyObservers("update configs");
    }

    @Override
    public String getGenConf() {
        Configurations conf = Configurations.getInstance();
        return conf.getGenerateAlgorithm();
    }

    @Override
    public String getSolveConf() {
        Configurations conf = Configurations.getInstance();
        return conf.getSolverAlgorithm();
    }
}
