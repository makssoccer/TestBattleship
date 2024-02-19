package org.example.model;

import java.util.Random;

public class BattleshipBot {
    public BattleshipField enemyBoard;
    private boolean[][] hitMap;
    private boolean isSearching;
    private static int currentDirection;
    private int firstHitX;
    private int firstHitY;
    private int lastHitX;
    private int lastHitY;

    public BattleshipBot(BattleshipField enemyBoard) {
        this.enemyBoard = enemyBoard;
        this.hitMap = new boolean[enemyBoard.gameBoard.length][enemyBoard.gameBoard[0].length];
        this.isSearching = true;
        this.firstHitX = -1;
        this.firstHitY = -1;
        this.lastHitX = -1;
        this.lastHitY = -1;
        this.currentDirection = -1;
    }

    public String makeMove() {
        if (isSearching) {
            return searchForShip();
        } else {
            return completeAttack();
        }
    }

    // Метод вызывается после уничтожения корабля
    public void setIsSearching() {
        this.isSearching = true;
    }

    private String searchForShip() {
        Random rand = new Random();
        int x = rand.nextInt(enemyBoard.gameBoard.length);
        int y = rand.nextInt(enemyBoard.gameBoard[0].length);
        while (hitMap[x][y]) {
            x = rand.nextInt(enemyBoard.gameBoard.length);
            y = rand.nextInt(enemyBoard.gameBoard[0].length);
        }
        attack(x, y);
        if (enemyBoard.gameBoard[x][y] == 'S') {
            firstHitX = x;
            firstHitY = y;
            lastHitX = x;
            lastHitY = y;
            currentDirection = -1; // Сбрасываем направление атаки
            isSearching = false;
        }
        return formatCoordinates(x, y);
    }

    private String completeAttack() {

        int x = lastHitX;
        int y = lastHitY;

        // Стреляем по соседним клеткам в направлении: слева, справа, сверху и снизу
        switch (++currentDirection) {
            case 0: // Стреляем вверх
                x--;
                break;
            case 1: // Стреляем вниз
                x++;
                break;
            case 2: // Стреляем влево
                y--;
                break;
            case 3: // Стреляем вправо
                y++;
                break;
        }
        // Проверяем, что координаты в пределах поля и клетка не была атакована ранее
        if (!isValidCell(x, y)) {
            // Если вышли за границы поля или клетка уже атакована, возвращаемся на первую точку попадания и меняем направление
            lastHitX = firstHitX;
            lastHitY = firstHitY;
            return completeAttack(); // Возвращаемся к поиску нового корабля
        }
        if (enemyBoard.gameBoard[x][y] == 'S') {
            lastHitX = x;
            lastHitY = y;
            --currentDirection;
        } else {
            lastHitX = firstHitX;
            lastHitY = firstHitY;
        }

        // Атакуем клетку
        attack(x, y);

        return formatCoordinates(x, y);
    }

    private void attack(int x, int y) {
        // Метод, который помечает клетку как атакованную
        hitMap[x][y] = true;
    }

    private String formatCoordinates(int x, int y) {
        // Метод, который форматирует координаты в строку (например, "A1")
        return String.valueOf((char) ('A' + y)) + (x + 1);
    }

    private boolean isValidCell(int x, int y) {
        // Метод, который проверяет, что клетка находится в пределах поля
        return x >= 0 && x < enemyBoard.gameBoard.length && y >= 0 && y < enemyBoard.gameBoard[0].length;
    }
}