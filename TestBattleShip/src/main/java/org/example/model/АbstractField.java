package org.example.model;

public class АbstractField {
    public BattleshipField playerBoard;
    public BattleshipField botBoard;

    public АbstractField() {
        this.playerBoard = new BattleshipField();
        System.out.println("------------------------------");
        this.botBoard = new BattleshipField();
    }
    public void destroy(int row, int col, BattleshipField board){
            board.markSunkShip(row, col);
            board.checkAfterDestroy();
    }
}
