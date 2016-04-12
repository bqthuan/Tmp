package com.bqt.chatclient;

import android.app.Application;

import io.socket.client.Socket;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class ChatApplication extends Application {

    private Socket mSocket;

    public void setSocket(Socket socket) {
        mSocket = socket;
    }

    public Socket getSocket() {
        return mSocket;
    }
}
