/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arj.chatserver.handler;

import com.arj.chatserver.command.PrivateMessage;
import com.arj.chatserver.util.SaveToLogFile;
import com.arj.chatserver.dao.UserDAO;
import com.arj.chatserver.dao.impl.UserDAOImpl;
import com.arj.chatserver.entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Zeppelin
 */
public class ClientListener extends Thread {

    private Socket socket;
    private Client client;
    private PrintStream ps;
    private ClientHandler handler;
    private BufferedReader reader;
    private UserDAO userDAO = new UserDAOImpl();

    public ClientListener(Socket socket, ClientHandler handler) throws IOException {
        this.socket = socket;
        this.handler = handler;
        ps = new PrintStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            login();
            while (!isInterrupted()) {
                chatBody();
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public boolean login() throws IOException {
        while (true) {
            ps.println("Enter your name:");
            String name = reader.readLine();
            ps.println("Enter your password:");
            String password = reader.readLine();

            User user = userDAO.login(name, password);
            if (user == null) {
                ps.println("Invalid login.");
            } else {
                ps.println("You have successfully logged in.");
                client = new Client(name, socket);
                handler.addClient(client);
                welcomeMessage();
                broadcastLogInMessage(client.getUserName());
                return true;
            }
        }
    }

    public void chatBody() throws IOException {
        ps.print("> ");
        String line = reader.readLine();
//        IgnoreCommand ignore = new IgnoreCommand();
//        if (tokens.length == 1) {
        if (line.equalsIgnoreCase("#list")) {
            showList();
//            } else if (line.equalsIgnoreCase("ignorelist")) {
//                ps.println("Showing your Ignore List:");
////                for (Client cl:handler.getAll()){
//                ps.println(ignore.getAll()); // + "(IP: " + socket.getInetAddress().getHostAddress() + ")");
////                }
        } else if (line.equalsIgnoreCase("#exit")) {
            exit(client);
        } else if (line.equalsIgnoreCase("#pm")) {
            PrivateMessage pvtmsg = new PrivateMessage(socket, client, handler);
            pvtmsg.run();
        } else {
            broadcastMessage(client, line);
        }
//        } else {
//            String command = tokens[0];
//            if (command.equalsIgnoreCase("pm")) {
//                String receiver = tokens[1];
//                if (tokens.length > 2) {
//                    DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                    Date date = new Date();
//                    String pm = dateformat.format(date) + " | " + client.getUserName()
//                            + " has PM'd you >> " + tokens[tokens.length - 1];
//                    PrivateMessage.sendPM(client, handler.getByUserName(receiver), pm);
//                }
//            } else 
//            if (command.equalsIgnoreCase("ignore")) {
//                if (tokens.length == 2) {
//                    String toIgnore = tokens[1];
//                    ignore.add(handler.getByUserName(toIgnore));
//                    PrivateMessage.sendPM(client, client, toIgnore + " has been added to your Ignore List.");
//                }
//            }
//            else {
//                ps.println("Invalid command.");
//                break;
//            }
//        }
    }

    public void welcomeMessage() throws IOException {
        ps.println("\n***Welcome to LAN Chat***\r\n\r\n"
                + "Commands for this chatroom are:\r\n"
                + "#list - see current users.\r\n"
                + "#pm - start PM session with one of the current users.\r\n"
                + "#leavepm - to close PM session.\r\n"
                + "#exit - exit chat session.\r\n\r\n"
                + "***Enjoy your stay!***\r\n");
    }

    public void broadcastLogInMessage(String userName) throws IOException {
        DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logInMsg = "NEW LOGIN: " + userName + "(IP: "
                + handler.getByUserName(userName).getSocket().getInetAddress().getHostAddress()
                + ") at: " + dateformat.format(date);
        for (Client c : handler.getAll()) {
            PrintStream out = new PrintStream(c.getSocket().getOutputStream());
            out.println(logInMsg);
        }
        SaveToLogFile.write(logInMsg);
    }

    public void broadcastMessage(Client sender, String message) throws IOException {
        DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String messagePrompt = dateformat.format(date) + " | " + client.getUserName() + " says >> ";
        for (Client c : handler.getAll()) {
            if (!c.equals(sender)) {
                PrintStream out = new PrintStream(c.getSocket().getOutputStream());
                out.println(messagePrompt + message);
            }
        }
        SaveToLogFile.write(messagePrompt + message);
    }

    public void showList() throws IOException {
        ps.println("Showing current users:");
        for (Client c : handler.getAll()) {
            ps.println(c.getUserName() + "(IP: " + socket.getInetAddress().getHostAddress() + ")");
        }
    }

    public void exit(Client c) throws IOException {
        ps.println("Are you sure you want to exit? [Y/N]");
        if (reader.readLine().equalsIgnoreCase("Y")) {
            ps.println("Thank you for logging in. Exiting chatserver...");
            broadcastLogOutMessage(c.getUserName());
            handler.removeClient(c);
            socket.close();
        }
    }

    public void broadcastLogOutMessage(String userName) throws IOException {
        DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logOutMessage = "LOG OUT: "+userName + "(IP: "
                + handler.getByUserName(userName).getSocket().getInetAddress().getHostAddress()
                + ") at: " + dateformat.format(date);
        for (Client c : handler.getAll()) {
            if (!c.getUserName().equals(userName)) {
                PrintStream out = new PrintStream(c.getSocket().getOutputStream());
                out.println(logOutMessage);
            }
        }
        SaveToLogFile.write(logOutMessage);
    }

}
