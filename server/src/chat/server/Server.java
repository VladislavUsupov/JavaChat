package chat.server;

import chat.connection.Connection;
import chat.connection.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server implements ConnectionListener {

    public static void main(String[] args) {
        new Server();
    }

    private Date date=new Date();
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

    private final ArrayList<Connection> connections = new ArrayList<>();

    private Server() {
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            while(true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendToAllConnections("Клиент присоединился: " + connection);
        sendToAllConnections("Количество человек в чате: " + getNumberOfConnections());
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendToAllConnections("Клиент отключился: " + connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        for (int i = 0; i < getNumberOfConnections(); i++) connections.get(i).sendString("[" + dateFormat.format(date) + "] "+ value);
    }

    private int getNumberOfConnections(){
        return connections.size();
    }
}