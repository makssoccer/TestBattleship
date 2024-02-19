package org.example.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import javax.websocket.*;

public class BattleshipClient {

    private static final String SERVER_ADDRESS = "ws://localhost:8080/";

    private static Scanner scanner = new Scanner(System.in);

    public static BattleshipField playerField;

    public static BattleshipField botBoardField;

    public static boolean isGameOver = false;

    private static String name;

    private static final char SHIP_SYMBOL = 'S';
    private static final char HIT_SYMBOL = 'H';
    private static final char DESTROY_SYMBOL = 'X';
    private static final char EMPTY_SYMBOL = ' ';
    private static final char MISS_SYMBOL = '.';
    private static BattleshipBot bot;


    public static void main(String[] args) {

        System.out.println("Please enter your username:");
        name = scanner.nextLine();

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
                startOnlineGame();
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                break;
        }
    }

    private static void startSinglePlayerGame() {
        playerField = new BattleshipField();
        System.out.println("------------------------------");
        botBoardField = new BattleshipField();
        bot = new BattleshipBot(playerField);

        System.out.println("Let's play Battleship!");

        while (!isGameOver) {
//             Ход игрока (пропущен для простоты)
            processPlayerTurn();
//             Ход бота
            processBotTurn();
        }

        System.out.println("Game Over!");

    }

    private static void processBotTurn() {

        String guess = bot.makeMove();
        int col = guess.charAt(0) - 'A';
        int row = Integer.parseInt(guess.substring(1)) - 1;

        // Стреляем в клетку
        if (playerField.gameBoard[row][col] == SHIP_SYMBOL) {
            playerField.gameBoard[row][col] = HIT_SYMBOL; // Попадание

            if (playerField.checkOtherCoordinates(row, col)) {
                playerField.markSunkShip(row, col);
                playerField.checkAfterDestroy();
                bot.setIsSearching();
                System.out.println("You Kill at " + (char) ('A' + col) + (row + 1));
            } else {
                System.out.println("You Wounded at " + (char) ('A' + col) + (row + 1));
            }
            System.out.println("Player Board");
            playerField.printBoard();
            processBotTurn();

        } else if (playerField.gameBoard[row][col] == MISS_SYMBOL || playerField.gameBoard[row][col] == HIT_SYMBOL || playerField.gameBoard[row][col] == DESTROY_SYMBOL) {
            processBotTurn();
        } else {
            playerField.gameBoard[row][col] = MISS_SYMBOL; // Промах
            System.out.println("Bot missed at " + (char) ('A' + col) + (row + 1));
            System.out.println("Player Board");
            playerField.printBoard();
        }
    }

    private static void processPlayerTurn() {

        System.out.println("Enter your guess (row and column, e.g. A5): ");
        String guess = scanner.nextLine().toUpperCase();
        int col = guess.charAt(0) - 'A';
        int row = Integer.parseInt(guess.substring(1)) - 1;

        if (botBoardField.gameBoard[row][col] == SHIP_SYMBOL) {
            botBoardField.gameBoard[row][col] = HIT_SYMBOL; // Попадание

            if (botBoardField.checkOtherCoordinates(row, col)) {
                botBoardField.markSunkShip(row, col);
                botBoardField.checkAfterDestroy();
                System.out.println("You Kill at " + (char) ('A' + col) + (row + 1));
            } else {
                System.out.println("You Wounded at " + (char) ('A' + col) + (row + 1));
            }
            System.out.println("Bot Board");
            botBoardField.printBoard();
            processPlayerTurn();

        } else if (botBoardField.gameBoard[row][col] == MISS_SYMBOL || botBoardField.gameBoard[row][col] == HIT_SYMBOL || botBoardField.gameBoard[row][col] == DESTROY_SYMBOL) {
            System.out.println("You have already shot at " + guess + ", choose a different coordinate ");
            processPlayerTurn();

        } else {
            botBoardField.gameBoard[row][col] = MISS_SYMBOL; // Промах
            System.out.println("Player missed at " + guess);
            System.out.println("Bot Board");
            botBoardField.printBoard();
        }
    }

    private static void startOnlineGame() {
//        BattleshipServer.startServer(); // Запуск сервера

        // Создание экземпляра класса BattleshipField и заполнение его
//        battleshipField = new BattleshipField();
//        battleshipField.placeShips();

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
                battleshipClientEndpoint.sendBattleshipField();
            }

        } catch (DeploymentException | URISyntaxException | IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        } catch (EncodeException e) {
            throw new RuntimeException(e);
        }

    }

}