package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class HttpRequestException extends Exception {

    public HttpRequestException(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}
