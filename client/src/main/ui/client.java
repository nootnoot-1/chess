package ui;

import adapters.MoveAdapter;
import chess.ChessMove;
import chess.ChessPiece;
import chess.MoveImpl;
import chess.PositionImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Game;
import request.*;
import response.JoinGameResponse;
import response.ListGamesResponse;
import java.util.*;

public class client {
    private static String authToken;
    private static final Printer printer = new Printer();
    public static void main(String[] args) {
        System.out.println("WELCOME TO CHESS");
        ServerFacade server = new ServerFacade("http://localhost:8080");
        loggedoutClient(server);

//        Game game = new Game("game1");
//        GameImpl gameImpl = new GameImpl();
//        BoardImpl boardImpl = new BoardImpl();
//        boardImpl.resetBoard();
//        gameImpl.setBoard(boardImpl);
//        game.setGame(gameImpl);
//        Printer printer = new Printer();
//        printer.printGame(game);

//        PositionImpl startposition = new PositionImpl(1,2);
//        PositionImpl endposition = new PositionImpl(5,6);
//        MoveImpl move = new MoveImpl(startposition,endposition, ChessPiece.PieceType.ROOK);
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(MoveImpl.class, new MoveAdapter());
//        Gson gson = gsonBuilder.create();
//        String json = gson.toJson(move);
//        MoveImpl newmove = gson.fromJson(json, MoveImpl.class);
//
//        String moveString = "b7b6";
//
//        PositionImpl startpostition = new PositionImpl(moveString.charAt(0)-96, moveString.charAt(1)-48);
//        PositionImpl endpostition = new PositionImpl(moveString.charAt(2)-96, moveString.charAt(3)-48);
//
//
//        int i = 0;
    }
    private static void loggedoutClient(ServerFacade server) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String temp = scanner.nextLine();
            String[] input = parseInput(temp);

            if (Objects.equals(input[0], "quit") && input.length == 1) {
                break;
            }

            else if (Objects.equals(input[0], "help") && input.length == 1) {
                System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                System.out.println("quit - playing chess");
                System.out.println("help - with possible commands");
            }

            else if (Objects.equals(input[0], "register") && input.length == 4) {
                RegisterRequest request = new RegisterRequest();
                request.setUsername(input[1]);
                request.setPassword(input[2]);
                request.setEmail(input[3]);
                try {
                    authToken = server.register(request).getAuthToken();
                    System.out.println("user registered and logged in");
                    loggedinClient(server);
                    break;
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
            }

            else if (Objects.equals(input[0], "login") && input.length == 3) {
                LoginRequest request = new LoginRequest();
                request.setUsername(input[1]);
                request.setPassword(input[2]);
                try {
                    authToken = server.login(request).getAuthToken();
                    System.out.println("user logged in");
                    loggedinClient(server);
                    break;
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
            }

            else {
                System.out.println("invalid input, type \"help\" for what you can do <3");
            }
        }
    }
    private static void loggedinClient(ServerFacade server) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("[LOGGED_IN] >>> ");
            String temp = scanner.nextLine();
            String[] input = parseInput(temp);

            if (Objects.equals(input[0], "quit") && input.length == 1) {
                try {
                    server.logout(authToken);
                    System.out.println("user logged out");
                    authToken = null;
                    break;
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
                break;
            }

            else if (Objects.equals(input[0], "help") && input.length == 1) {
                System.out.println("join <ID> [WHITE|BLACK|<empty>] - a game");
                System.out.println("observe <ID> - a game");
                System.out.println("create <NAME> - a game");
                System.out.println("list - games");
                System.out.println("logout - when you are done");
                System.out.println("quit - playing chess");
                System.out.println("help - with possible commands");
            }

            else if (Objects.equals(input[0], "join") && input.length == 3) {
                JoinGameRequest request = new JoinGameRequest();
                request.setGameID(Integer.parseInt(input[1]));
                request.setPlayerColor(input[2]);
                try {
                    JoinGameResponse response = server.joinGame(request, authToken);
                    if (response.getMessage() == null) { //for phase 5 passoff remove websocket
                        System.out.println("joining game");
                        WebSocket.run(input[2], authToken, Integer.parseInt(input[1]));
                        //printer.printGame(response.getGame());
                    } else {
                        System.out.println("no game with that ID");
                    }
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("WEBBY BEBBY ERROR" + e);
                }
            }

            else if (Objects.equals(input[0], "observe") && input.length == 2) {
                JoinGameRequest request = new JoinGameRequest();
                request.setGameID(Integer.parseInt(input[1]));
                try {
                    JoinGameResponse response = server.joinGame(request, authToken);
                    if (response.getMessage() == null) { //for phase 5 passoff remove websocket
                        System.out.println("observing game");
                        WebSocket.run( null, authToken, Integer.parseInt(input[1]));
                        //printer.printGame(response.getGame());
                    } else {
                        System.out.println("no game with that ID");
                    }
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("WEBBY BEBBY ERROR" + e);
                }
            }

            else if (Objects.equals(input[0], "logout") && input.length == 1) {
                try {
                    server.logout(authToken);
                    System.out.println("user logged out");
                    authToken = null;
                    loggedoutClient(server);
                    break;
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
            }

            else if (Objects.equals(input[0], "list") && input.length == 1) {
                try {
                    ListGamesRequest request = new ListGamesRequest();
                    request.setAuthToken(authToken);
                    ListGamesResponse response = server.listGames(request);
                    Collection<Game> games = response.getGames();
                    if (games != null) {
                        int i = 1;
                        for (Game it : games) {
                            System.out.println(i + "\n" + it.getGameName() + ", ID: " + it.getGameID());
                            System.out.println("white: " + it.getWhiteUsername());
                            System.out.println("black: " + it.getBlackUsername());
                            ++i;
                        }
                    }
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
            }

            else if (Objects.equals(input[0], "create") && input.length == 2) {
                CreateGameRequest request = new CreateGameRequest();
                request.setGameName(input[1]);
                request.setAuthToken(authToken);
                try {
                    server.createGame(request);
                    System.out.println("game created");
                } catch (ServerFacade.ResponseException e) {
                    System.out.println(e.getMessage());
                }
            }

            else {
                System.out.println("invalid input, type \"help\" for what you can do <3");
            }
        }
    }
    private static String[] parseInput(String input) {
        String[] words = input.split("\\s+");
        return words;
    }

}
