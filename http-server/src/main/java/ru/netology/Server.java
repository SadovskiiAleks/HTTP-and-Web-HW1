package ru.netology;

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
        try (final var serverSocket = new ServerSocket(numberOfSocket)) {
            final ExecutorService threadPool = Executors.newFixedThreadPool(64);
            while (true) {
                try {
                    //Accept a new connection
                    final var socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    Request request = new Request().addRequest(inputStream);

                    Handler handler = this.getHandler(request.methodOfHandler, request.handlerName);
                    RunnableClass runnableClass = new RunnableClass(handler, request, socket);

                    //Create and start new Thread on ThreadPool
                    threadPool.execute(runnableClass);

                    //inputStream.close();


                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                //I need shutdown threadPool ?
                // threadPool.shutdown();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String wayOfURL, Handler handler) {
        if (method.equals("GET")) {
            getMap.put(wayOfURL, handler);
        }
        if (method.equals("POST")) {
            postMap.put(wayOfURL, handler);
        } else {
            //return new exception
        }
    }

    public Handler getHandler(String method, String handlerName) {
        if (method.equals("GET")) {
            return getMap.get(handlerName);

        }
        if (method.equals("POST")) {
            return postMap.get(handlerName);

        } else {
            //return new exception
            return null;
        }
    }


}
