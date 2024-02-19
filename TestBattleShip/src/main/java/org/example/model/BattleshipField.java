package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.model.CommonVariables.*;

public class BattleshipField {

    private static final int BOARD_SIZE = 16;
    private static final char[] COLUMN_LABELS = "ABCDEFGHIJKLMNOP".toCharArray();
    private static final int[] SHIP_SIZES = {6, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1};
    private final int CONST = 21;
    private int numberOfShip ;
    public char[][] gameBoard;
    public int shipsRemaining;


    public BattleshipField() {
        gameBoard = new char[BOARD_SIZE][BOARD_SIZE];
        numberOfShip = CONST;
        initializeBoard();
        placeShips();
        printBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = ' ';
            }
        }
    }

    public void placeShips() {
        Random random = new Random();

        for (int size : SHIP_SIZES) {
            boolean shipPlaced = false;
            while (!shipPlaced) {
                int row = random.nextInt(BOARD_SIZE);
                int col = random.nextInt(BOARD_SIZE);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(row, col, size, horizontal)) {
                    placeShip(row, col, size, horizontal);
                    shipPlaced = true;
                }
            }
        }

        shipsRemaining = SHIP_SIZES.length;
    }


    private boolean canPlaceShip(int startRow, int startCol, int size, boolean horizontal) {
        if (horizontal && startRow + size > BOARD_SIZE) {
            return false;
        }
        if (horizontal == false && startCol + size > BOARD_SIZE) {
            return false;
        }

        if(horizontal) {
            for (int i = -1; i <= size; i++) {
                for (int j = -1; j <= 1; j++) {
                    int row = startRow + i;
                    int col = startCol + j;

                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        if (gameBoard[row][col] != ' ' || gameBoard[row][col] == SHIP_SYMBOL) {
                            return false;  // Есть корабль на расстоянии 1 клетки
                        }
                    }
                }
            }
        }else{
        for (int i = -1; i <= size; i++) {
            for (int j = -1; j <= 1; j++) {
                int row = startRow + j;
                int col = startCol + i;

                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    if (gameBoard[row][col] != ' ' || gameBoard[row][col] == SHIP_SYMBOL) {
                        return false;  // Есть корабль на расстоянии 1 клетки
                    }
                }
            }
        }
        }

        return true;
    }

    private void placeShip(int startRow, int startCol, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int row = horizontal ? startRow + i : startRow;
            int col = horizontal ? startCol : startCol + i;

            gameBoard[row][col] = 'S';  // 'S' represents a part of the ship
        }
    }

    public void printBoard() {
        System.out.print("   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(COLUMN_LABELS[i] + " ");
        }
        System.out.println();

        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(gameBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

//    public boolean takeTurn(int row, int col) {
//        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
//            System.out.println("Invalid move. Try again.");
//            return false;
//        }
//
//        if (gameBoard[row][col] == 'S') {
//            System.out.println("Hit!");
//            gameBoard[row][col] = 'X';  // 'X' represents a hit
//            shipsRemaining--;
//
//            if (shipsRemaining == 0) {
//                System.out.println("All ships sunk. You win!");
//            }
//        } else if (gameBoard[row][col] == 'X') {
//            System.out.println("Already hit this location. Try again.");
//        } else {
//            System.out.println("Miss.");
//            gameBoard[row][col] = 'O';  // 'O' represents a miss
//        }
//
//        return true;
//    }

    public boolean checkShipDestroyed(int row, int col) {
        // Если клетка, в которую произведен выстрел, не является частью корабля, возвращаем false
        if (gameBoard[row][col] != SHIP_SYMBOL) {
            return false;
        }

        // Маркируем текущую клетку как подбитую
        gameBoard[row][col] = HIT_SYMBOL;

        // Рекурсивно проверяем соседние клетки корабля
        return checkAdjacentCells(row, col);
    }

    private boolean checkAdjacentCells(int row, int col) {
        // Проверяем, есть ли еще не подбитые клетки корабля вокруг текущей
        boolean shipDestroyed = true;
        for (int i = Math.max(0, row - 1); i <= Math.min(gameBoard.length - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(gameBoard[i].length - 1, col + 1); j++) {
                if ((i != row || j != col) && gameBoard[i][j] == SHIP_SYMBOL) {
                    // Если в текущей клетке находится символ корабля и это не текущая клетка, то продолжаем проверку
                    shipDestroyed = false;
                    if (!checkShipDestroyed(i, j)) {
                        // Если хотя бы одна клетка корабля еще не подбита, то весь корабль не уничтожен
                        return false;
                    }
                }
            }
        }
        return shipDestroyed;
    }

    public boolean checkOtherCoordinates(int row, int col) {
        // Проверяем, есть ли еще целые клетки корабля в вертикальном направлении
        for (int i = row - 1; i >= 0; i--) {
            if (gameBoard[i][col] == HIT_SYMBOL) {
                continue;
            }
            if (gameBoard[i][col] == SHIP_SYMBOL) {
                return false; // Найдена целая клетка корабля выше
            } else if (gameBoard[i][col] == EMPTY_SYMBOL || gameBoard[i][col] == MISS_SYMBOL) {
                break; // Наткнулись на пустую клетку, дальше корабль прерывается
            }
        }
        for (int i = row + 1; i < gameBoard.length; i++) {
            if (gameBoard[i][col] == HIT_SYMBOL) {
                continue;
            }
            if (gameBoard[i][col] == SHIP_SYMBOL) {
                return false; // Найдена целая клетка корабля ниже
            } else if (gameBoard[i][col] == EMPTY_SYMBOL || gameBoard[i][col] == MISS_SYMBOL) {
                break; // Наткнулись на пустую клетку, дальше корабль прерывается
            }
        }

        // Проверяем, есть ли еще целые клетки корабля в горизонтальном направлении
        for (int j = col - 1; j >= 0; j--) {
            if (gameBoard[row][j] == HIT_SYMBOL) {
                continue;
            }
            if (gameBoard[row][j] == SHIP_SYMBOL) {
                return false; // Найдена целая клетка корабля слева
            } else if (gameBoard[row][j] == EMPTY_SYMBOL || gameBoard[row][j] == MISS_SYMBOL) {
                break; // Наткнулись на пустую клетку, дальше корабль прерывается
            }
        }
        for (int j = col + 1; j < gameBoard[row].length; j++) {
            if (gameBoard[row][j] == HIT_SYMBOL) {
                continue;
            }
            if (gameBoard[row][j] == SHIP_SYMBOL) {
                return false; // Найдена целая клетка корабля справа
            } else if (gameBoard[row][j] == EMPTY_SYMBOL || gameBoard[row][j] == MISS_SYMBOL) {
                break; // Наткнулись на пустую клетку, дальше корабль прерывается
            }
        }

        // Если дошли до этой точки, значит других целых клеток корабля не найдено
        return true;
    }

    public void markSunkShip(int x, int y) {
        List<int[]> shipCoordinates = findShipCoordinates(x, y);

        // Mark adjacent cells around the ship
        for (int[] coordinate : shipCoordinates) {
            markAdjacentCells(coordinate[0], coordinate[1]);
        }
    }

    private List<int[]> findShipCoordinates(int x, int y) {
        List<int[]> shipCoordinates = new ArrayList<>();
        findShipCoordinatesRecursive(x, y, shipCoordinates);
        return shipCoordinates;
    }

    private void findShipCoordinatesRecursive(int x, int y, List<int[]> shipCoordinates) {
        if (!isValidCell(x, y, gameBoard.length, gameBoard[0].length) || gameBoard[x][y] != HIT_SYMBOL) {
            return;
        }

        // Mark the current cell as visited
        gameBoard[x][y] = DESTROY_SYMBOL;
        shipCoordinates.add(new int[]{x, y});

        // Recursively check adjacent cells
        findShipCoordinatesRecursive(x - 1, y, shipCoordinates);
        findShipCoordinatesRecursive(x + 1, y, shipCoordinates);
        findShipCoordinatesRecursive(x, y - 1, shipCoordinates);
        findShipCoordinatesRecursive(x, y + 1, shipCoordinates);
    }

    private void markAdjacentCells(int x, int y) {
        int rows = gameBoard.length;
        int cols = gameBoard[0].length;

        // Mark horizontally, vertically, and diagonally adjacent cells
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidCell(i, j, rows, cols) && gameBoard[i][j] == EMPTY_SYMBOL) {
                    gameBoard[i][j] = MISS_SYMBOL;
                }
            }
        }
    }

    private boolean isValidCell(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }


    public void checkAfterDestroy() {
        numberOfShip -= 1;
        if (numberOfShip == 0) {
            BattleshipClient.gameOver();
        }

    }

//    public static void main(String[] args) {
//        BattleshipField game = new BattleshipField();
//        Scanner scanner = new Scanner(System.in);
//
//        while (game.shipsRemaining > 0) {
//            game.printBoard();
//            System.out.print("Enter row (1-16): ");
//            int row = scanner.nextInt() - 1;
//
//            System.out.print("Enter column (A-P): ");
//            char colChar = scanner.next().toUpperCase().charAt(0);
//            int col = colChar - 'A';
//
//            game.takeTurn(row, col);
//        }
//
//        scanner.close();
//    }


}
