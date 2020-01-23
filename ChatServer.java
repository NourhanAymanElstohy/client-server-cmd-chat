import java.io.*;
import java.net.*;

import java.util.Vector;

public class ChatServer {
    ServerSocket sok;

    public ChatServer() {
        try {
            sok = new ServerSocket(5000);
            while (true) {
                Socket s = sok.accept();
                new ChatHandler(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}

class ChatHandler extends Thread {
    DataInputStream dis;
    PrintStream ps;

    String name;
    // List <String> names = new ArrayList<String>();

    static Vector<ChatHandler> clientsVector = new Vector<ChatHandler>();

    public ChatHandler(Socket cs) {

        try {
            dis = new DataInputStream(cs.getInputStream());
            ps = new PrintStream(cs.getOutputStream());

            clientsVector.add(this);
            start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        while (true) {
            try {

                String str = dis.readLine();
                name = str.substring(0, str.indexOf(":"));
                System.out.println(str);
                sendMessageToAll(str);

            } catch (IOException e) {
                try {
                    clientsVector.remove(this);
                    ps.close();
                    dis.close();

                } catch (IOException e2) {
                    e2.printStackTrace();
                } finally {
                    String msg = name + "has been left";
                    sendMessageToAll(msg);
                    break;
                }

            }

        }

    }

    void sendMessageToAll(String msg) {
        for (ChatHandler ch : clientsVector) {
            ch.ps.println(msg);
        }
    }
}