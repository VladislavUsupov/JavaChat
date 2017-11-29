package chat.client;

import chat.connection.Connection;
import chat.connection.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class ClientWindow extends JFrame implements ActionListener, ConnectionListener {

    private static final String clientIP = "127.0.0.1";
    private static final int clientPort = 8189;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Введите ваше имя: ");
    private final JTextField fieldInput = new JTextField("Введите ваше сообщение: ");

    private Connection connection;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }


    private ClientWindow() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setTitle("Client");
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton jbSendMessage = new JButton("Отправить");

        add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        bottomPanel.add(fieldInput, BorderLayout.CENTER);
        bottomPanel.add(fieldNickname, BorderLayout.WEST);

        add(log, BorderLayout.CENTER);

        JScrollPane jsp = new JScrollPane(log);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(jsp);

        jbSendMessage.addActionListener(this);

        setVisible(true);
        try {
            Socket socket=new Socket(clientIP, clientPort);
            connection = new Connection(this, socket);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }

        fieldInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fieldInput.setText("");
            }
        });

        fieldNickname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fieldNickname.setText("");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(Connection connection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(Connection connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(Connection connection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(Connection connection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }


}