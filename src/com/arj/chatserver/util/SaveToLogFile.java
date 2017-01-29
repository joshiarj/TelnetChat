/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arj.chatserver.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Zeppelin
 */
public class SaveToLogFile {

    public static void write(String message) throws IOException {
        String path = "C:/Users/Zeppelin/Documents/NetBeansProjects/ChatServer/src/com/arj/chatserver/log/";
        String logFileName = path + "chatlog.log";
        FileWriter writer = new FileWriter(logFileName, true);
        writer.write(message + "\r\n");
        writer.flush();
        writer.close();
    }

    public static void writePM(String sender, String receiver, String message) throws IOException {
        String path = "C:/Users/Zeppelin/Documents/NetBeansProjects/ChatServer/src/com/arj/chatserver/log/";
        String userFolder = "PMs_to_" + receiver;
        File file = new File(path + userFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        String pmFileName = path + userFolder + "/" + "from_" + sender + ".log";
        FileWriter writer = new FileWriter(pmFileName, true);
        writer.write(message + "\r\n");
        writer.flush();
        writer.close();
    }

}
