import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {
    // GUI components
    Label lblChat;
    Label lblMessage;
    TextField txtMessage;
    TextArea txtChat;
    Button btn;

    // socket components
    String username;
    Socket client;
    ExecutorService es;
    Scanner readFromServer;
    PrintWriter writeToServer;

    public void connect() throws IOException {
        client = new Socket("127.0.0.1", 1234);
        es = Executors.newFixedThreadPool(2);
        readFromServer = new Scanner(client.getInputStream(),
                StandardCharsets.UTF_8);
        writeToServer = new PrintWriter(client.getOutputStream(),
                true, StandardCharsets.UTF_8);

        writeToServer.println(username);
        // new thread: read from server and print to the screen
        es.execute(()->{
            while(true) {
                try {
                    String response = readFromServer.nextLine();
                    if (response.equals("quit")) {
                        es.shutdownNow();
                        return;
                    }
                    txtChat.appendText("\n" + response);
                } catch (NoSuchElementException e) {
                    break;
                }
            }
        });
    }

    @Override
    public void start(Stage stage) {

        // SETUP THE PANE
        // The grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);

        // 1st row:

        lblChat = new Label("Chat: ");

        txtChat = new TextArea();
        txtChat.setPrefWidth(300);
        txtChat.setPrefHeight(300);
        txtChat.setWrapText(true);


        grid.add(lblChat,0,0);
        GridPane.setHalignment(lblChat, HPos.RIGHT);
        GridPane.setValignment(lblChat, VPos.CENTER);

        grid.add(txtChat,1,0);
        GridPane.setHalignment(txtChat, HPos.LEFT);
        GridPane.setValignment(txtChat, VPos.CENTER);

        // 2nd row:
        lblMessage = new Label("Message: ");
        txtMessage = new TextField();
        txtMessage.setPromptText("Enter text");
        txtMessage.setPrefWidth(300);

        grid.add(lblMessage,0,1);
        GridPane.setHalignment(lblMessage, HPos.RIGHT);
        GridPane.setValignment(lblMessage, VPos.CENTER);

        grid.add(txtMessage,1,1);
        GridPane.setHalignment(txtMessage, HPos.LEFT);
        GridPane.setValignment(txtMessage, VPos.CENTER);

        // 3rd row
        btn = new Button("Send");
        btn.setOnAction(e->click());

        grid.add(btn, 1,2);
        GridPane.setHalignment(btn, HPos.RIGHT);
        GridPane.setHalignment(btn,HPos.CENTER);

        Scene scene = new Scene(grid);
        txtMessage.requestFocus();

        stage.setScene(scene);
        stage.setTitle("CHAT!");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth()-400)/2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight()-300)/2);
        stage.setWidth(400);
        stage.setHeight(300);

        stage.show();

        // enter username
        username = new MessageBoxInputText("Enter your username", "Username")
                .getResponse();
        // connect to the server
        try {
            connect();
        }
        catch (IOException e) {
            System.out.println("connection error");
        }
        // actions to close window
        stage.setOnCloseRequest(e->{
            writeToServer.println("quit");
            es.shutdownNow();
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            stage.close();
        });
    }

    public void click() {
        String request = txtMessage.getText();
        writeToServer.println(request);
        if (request.equals("quit")) {
            es.shutdownNow();
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Bye Bye");
        }
        txtMessage.clear();
        txtChat.setScrollTop(Double.MAX_VALUE);
    }


    public static void main(String[] args) {
        launch(args);
    }
}