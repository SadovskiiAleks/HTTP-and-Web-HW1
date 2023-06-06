package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RunnableClass implements Runnable {
    Handler handler;
    Request request;
    Socket socket;

    public RunnableClass(Handler handler, Request request, Socket socket) {
        this.handler = handler;
        this.request = request;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handler.handle(request, new BufferedOutputStream(socket.getOutputStream()));

            //inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
