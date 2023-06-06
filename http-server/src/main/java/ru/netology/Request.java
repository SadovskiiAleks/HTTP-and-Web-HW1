package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    //debag
    String exeptionTEST;


    String methodOfHandler;
    String handlerName;
    String httpVersion;

    Map<String, String> mapOfHeaders = new HashMap<>();

    byte[] body;


    Request addRequest(InputStream inputStream) throws IOException {
        final var in = new BufferedReader(new InputStreamReader(inputStream));


        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1

        //checkFeastLine, need check on new exception 404*
        methodOfHandler = parts[0];
        handlerName = parts[1];
        httpVersion = parts[2];

        //read all headers
        String readLine;
        do {
            readLine = in.readLine();
            String[] headersParts = readLine.split(": ");
            mapOfHeaders.put(headersParts[0], headersParts[1]);
        }
        while (readLine.equals("\r\n\r\n"));

        //"тело запроса, если есть."
        if (mapOfHeaders.get("Content-Length") != null) {
            byte[] body = new byte[Integer.parseInt(mapOfHeaders.get("Content-Length"))];
            body = inputStream.readAllBytes();
        }

        //in.close();

        return this;
    }
}
