/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arj.chatserver.command;

import com.arj.chatserver.handler.Client;
import com.arj.chatserver.handler.ClientHandler;
import com.arj.chatserver.util.SaveToLogFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Zeppelin
 */
public class PrivateMessage {

    private Socket socket;
    private ClientHandler handler;
    private Client client;
    private PrintStream ps;
    private BufferedReader reader;

    public PrivateMessage(Socket socket, Client client, ClientHandler handler) throws IOException {
        this.socket = socket;
        this.client = client;
        this.handler = handler;
        ps = new PrintStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    public void run() throws IOException {
        ps.println("Who do you want to PM?");
        String receiver = reader.readLine();
        ps.println("You can now PM " + receiver + ". (Type \"#leavePM\" to leave this PM thread.)");
        while (true) {
            ps.print("Type PM for " + receiver + " > ");
            String pm = reader.readLine();
            if (pm.equalsIgnoreCase("#leavepm")) {
                ps.println("You have now left PM with " + receiver + ".");
                break;
            } else {
                DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String pmPrompt = dateformat.format(date) + " || PM from "+ client.getUserName() + ">> ";
                PrintStream out = new PrintStream(handler.getByUserName(receiver).getSocket().getOutputStream());
                out.println(pmPrompt + pm);
                SaveToLogFile.writePM(client.getUserName(), receiver, pmPrompt+pm);
            }
        }
    }

}
