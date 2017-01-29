/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arj.chatserver.command;

import com.arj.chatserver.handler.Client;
import com.arj.chatserver.handler.ClientHandler;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Zeppelin
 */
public class ListCommand {

    private ClientHandler handler;
    private Socket socket;
    private Client client;
    private PrintStream ps;

    public ListCommand(ClientHandler handler, Client client, Socket socket) throws IOException {
        this.handler = handler;
        this.client = client;
        this.socket = socket;
        ps = new PrintStream(socket.getOutputStream());
    }

    public void showList() throws IOException {
        for (Client c : handler.getAll()) {
            ps.println(c.getUserName() + "(IP: " + socket.getInetAddress().getHostAddress() + ")");
        }
    }

}
