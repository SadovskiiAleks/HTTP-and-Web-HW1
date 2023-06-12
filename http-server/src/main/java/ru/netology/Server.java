package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    int numberOfSocket;
    Map<String, Handler> getMap = new HashMap<>();
    Map<String, Handler> postMap = new HashMap<>();

    Server(int numberOfSocket) {
        this.numberOfSocket = numberOfSocket;
    }

    void start() {
        ExecutorService threadPool = Executors.newFixedThreadPool(64);

        try (final var serverSocket = new ServerSocket(numberOfSocket)) {

            while (true) {
                try {
                    //Accept a new connection
                    final var socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    Request request = new Request(4096).addRequest(inputStream);
                    if (request.badRequest) {
                        new HttpRequestException(new BufferedOutputStream(socket.getOutputStream()));
                        socket.shutdownOutput();
                        continue;
                    }

                    socket.shutdownInput();
                    Handler handler = this.getHandler(request.methodOfHandler, request.handlerName);
                    if (handler == null) {
                        new HttpRequestException(new BufferedOutputStream(socket.getOutputStream()));
                        socket.shutdownOutput();
                        continue;
                    }

                    RunnableClass runnableClass = new RunnableClass(handler, request, socket);

                    //Create and start new Thread on ThreadPool
                    threadPool.execute(runnableClass);


                } catch (
                        IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    public void addHandler(String method, String wayOfURL, Handler handler) throws HttpMethodException {
        if (method.equals("GET")) {
            getMap.put(wayOfURL, handler);
        } else if (method.equals("POST")) {
            postMap.put(wayOfURL, handler);
        } else {
            new HttpMethodException(method);
        }

    }

    public Handler getHandler(String method, String handlerName) {
        if (method.equals("GET")) {
            return getMap.get(handlerName);

        } else if (method.equals("POST")) {
            return postMap.get(handlerName);

        } else {
            //return new exception
            return null;
        }
    }


}