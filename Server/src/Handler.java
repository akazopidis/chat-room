import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class Handler implements Runnable {
    private Socket client;
    private Set<Handler> users;
    private Scanner readFromClient;
    private PrintWriter writeToClient;

    Handler(Socket client, Set<Handler> users) {
        this.client = client;
        this.users = users;

        try {
            // make streams
            readFromClient = new Scanner(client.getInputStream(),
                    StandardCharsets.UTF_8);
            writeToClient = new PrintWriter(client.getOutputStream(),
                    true, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // make some login
        String username = readFromClient.nextLine();
        broadcast(users, "", "User '" + username + "' logged in.");

        try {
            // read from your client
            while (true) {
                String message = readFromClient.nextLine();
                if (message.equals("quit")) {
                    break;
                }
                broadcast(users, username, message);
            }
            broadcast(users, "", "User '" + username + "' logged out.");
        } catch (NoSuchElementException e) {
            users.remove(this);
            broadcast(users, "","User " + username + " logged out");
        }
    }


    private PrintWriter getOutputStream() {
        return writeToClient;
    }

    private static synchronized void broadcast(Set<Handler> users, String username, String message) {
        for (var user: users) {
            user.getOutputStream().println("[" + username + "] " + message);
        }
        System.out.println("[" + username + "] " + message);
    }
}
