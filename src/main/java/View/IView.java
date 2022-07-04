package View;

import ViewModel.MyViewModel;
import javafx.stage.Stage;

public interface IView {
    public void setViewModel(MyViewModel myViewModel, Stage prime);
    public Stage getPrimeStage();
    public void setUpdatePlayerRow(int updatePlayerRow);
    public String getUpdatePlayerCol();
    public void setUpdatePlayerCol(int updatePlayerCol);
    public void setPlayerPosition(int row, int col);
    public void win();
    public void mazeSolved();
    public void playerMoved();
    public void mazeGenerated();

}
