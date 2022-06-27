package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import Model.Solution;
import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    private IModel model;

    public ViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof IModel) {
            setChanged();
            notifyObservers(arg);
        }
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }


    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction = MovementDirection.UP;
        switch (keyEvent.getCode()) {
            case NUMPAD8 :{ direction = MovementDirection.UP; break;} // UP
            case NUMPAD9 :{ direction = MovementDirection.UPRIGHT; break;} // UP RIGHT
            case NUMPAD6 :{ direction = MovementDirection.RIGHT; break;}// RIGHT
            case NUMPAD3 :{ direction = MovementDirection.DOWNRIGHT; break;} // DOWN RIGHT
            case NUMPAD2 :{ direction = MovementDirection.DOWN; break;} // DOWN
            case NUMPAD1 :{ direction = MovementDirection.DOWNLEFT; break;} // DOWN LEFT
            case NUMPAD4 :{ direction = MovementDirection.LEFT; break;} // LEFT
            case NUMPAD7 :{ direction = MovementDirection.UPLEFT; break;} // UP LEFT
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze(Maze maze){
       model.solveMaze();
    }
}
