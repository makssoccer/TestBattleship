package org.example.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import javax.websocket.*;

import static org.example.model.CommonVariables.*;

public class BattleshipClient {

    private static final String SERVER_ADDRESS = "ws://localhost:8080/";

    private static Scanner scanner = new Scanner(System.in);

    public static boolean isGameOver = false;


    public static void main(String[] args) {

        System.out.println("Please enter your username:");
        String name = scanner.nextLine();

        System.out.println("Welcome to Battleship!");
        System.out.println("Select game mode:");
        System.out.println("1. Single player");
        System.out.println("2. Online");


        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        switch (choice) {
            case 1:
                System.out.println("Starting single player game...");

                startSinglePlayerGame();
                break;
            case 2:
                System.out.println("Starting online game...");
                startOnlineGame(name);
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                break;
        }
    }

    private static void startSinglePlayerGame() {

        АbstractField аbstractField = new АbstractField();// Создаём экземпляр абстрактного поля, где будут созданы поля
        BattleshipBot bot = new BattleshipBot(аbstractField.playerBoard);// Создаём бота
        System.out.println("Let's play Battleship!");

        while (!isGameOver) {
            // Ход игрока
            processPlayerTurn(аbstractField);
            // Ход бота
            processBotTurn(аbstractField, bot);
        }

        System.out.println("Game Over!");

    }

    private static void processBotTurn(АbstractField аbstractField, BattleshipBot bot) {
        // Выбор координаты ботом
        String guess = bot.makeMove();

        int col = guess.charAt(0) - 'A';
        int row = Integer.parseInt(guess.substring(1)) - 1;

        // Проверка на попадание
        if (аbstractField.playerBoard.gameBoard[row][col] == SHIP_SYMBOL) {
            // Попадание
            аbstractField.playerBoard.gameBoard[row][col] = HIT_SYMBOL;
            // Проверка на уничтожение корабля
            if (аbstractField.playerBoard.checkOtherCoordinates(row, col)) {
                аbstractField.destroy(row, col,аbstractField.playerBoard);
                bot.setIsSearching();
                System.out.println("Bot Kill at " + (char) ('A' + col) + (row + 1));
            } else {
                System.out.println("Bot Wounded at " + (char) ('A' + col) + (row + 1));
            }
            System.out.println("Player Board");
            аbstractField.playerBoard.printBoard();
            processBotTurn(аbstractField,bot);

            // Проверка на попадание в повреждённый участок корабля, повторную стрельбу
        } else if (аbstractField.playerBoard.gameBoard[row][col] == MISS_SYMBOL ||
                аbstractField.playerBoard.gameBoard[row][col] == HIT_SYMBOL ||
                аbstractField.playerBoard.gameBoard[row][col] == DESTROY_SYMBOL) {
            processBotTurn(аbstractField, bot);
        } else {
            аbstractField.playerBoard.gameBoard[row][col] = MISS_SYMBOL; // Промах
            System.out.println("Bot missed at " + (char) ('A' + col) + (row + 1));
            System.out.println("Player Board");
            аbstractField.playerBoard.printBoard();
        }
    }

    private static void processPlayerTurn(АbstractField abstractFild) {

        System.out.println("Enter your guess (row and column, e.g. A5): ");
        String guess = scanner.nextLine().toUpperCase();
        int col = guess.charAt(0) - 'A';
        int row = Integer.parseInt(guess.substring(1)) - 1;

        // Проверка на попадание
        if (abstractFild.botBoard.gameBoard[row][col] == SHIP_SYMBOL) {

            abstractFild.botBoard.gameBoard[row][col] = HIT_SYMBOL; // Попадание
            // Проверка на уничтожение корабля
            if (abstractFild.botBoard.checkOtherCoordinates(row, col)) {
                abstractFild.destroy(row, col, abstractFild.botBoard);// Закраска убитого корабля и уменьшение кол. кораблей
                System.out.println("You Kill at " + (char) ('A' + col) + (row + 1));
            } else {
                System.out.println("You Wounded at " + (char) ('A' + col) + (row + 1));
            }
            System.out.println("Bot Board");
            abstractFild.botBoard.printBoard();
            processPlayerTurn(abstractFild);
            // Проверка на попадание в повреждённый участок корабля, повторную стрельбу
        } else if (abstractFild.botBoard.gameBoard[row][col] == MISS_SYMBOL ||
                abstractFild.botBoard.gameBoard[row][col] == HIT_SYMBOL ||
                abstractFild.botBoard.gameBoard[row][col] == DESTROY_SYMBOL) {
            System.out.println("You have already shot at " + guess + ", choose a different coordinate ");
            processPlayerTurn(abstractFild);

        } else {
            abstractFild.botBoard.gameBoard[row][col] = MISS_SYMBOL; // Промах
            System.out.println("Player missed at " + guess);
            System.out.println("Bot Board");
            abstractFild.botBoard.printBoard();
        }
    }

    private static void startOnlineGame(String name) {

//         BattleshipServer.startServer(); // Запуск сервера

        // Подключение к серверу WebSocket
        try {
            BattleshipClientEndpoint battleshipClientEndpoint = new BattleshipClientEndpoint();


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(battleshipClientEndpoint, new URI(SERVER_ADDRESS));

            battleshipClientEndpoint.sendMessage(name);

//            Session session = battleshipClientEndpoint.getSession();
//            Получение сессии
//            Session session = container.connectToServer(BattleshipClientEndpoint.class, new URI(SERVER_ADDRESS));
//            Отправка экземпляра BattleshipField и Session на сервер
//            BattleshipServer.setGameBoard((org.eclipse.jetty.websocket.api.Session) session, battleshipField);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while ((userInput = reader.readLine()) != null) {
                battleshipClientEndpoint.sendMessage(userInput);
            }

        } catch (DeploymentException | URISyntaxException | IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }

    }

    public static void gameOver() {
        isGameOver = true;
    }
}