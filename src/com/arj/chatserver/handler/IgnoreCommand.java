/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arj.chatserver.handler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zeppelin
 */
public class IgnoreCommand {

    private List<Client> ignoreList = new ArrayList<>();
    private Client client;
    private ClientHandler handler;

    public IgnoreCommand() {
    }

    public IgnoreCommand(Client client, ClientHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    public boolean add(Client c) {
        if (c != null) {
            return ignoreList.add(c);
        } 
        return false;
    }

    public boolean remove(Client c) {
        return ignoreList.remove(c);
    }

    public List<Client> getAll() {
        return ignoreList;
    }
    
    public boolean isIgnored(Client c){
        for(Client cl : ignoreList){
            if (cl.equals(c)){
                return true;
            }
        }
        return false;
    }

}
