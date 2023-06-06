package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(9999);

        server.addHandler("GET", "/classic.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                try {
                    final var filePath = Path.of("A:\\HomeWorkNetology" +
                            "\\HTTP and the Modern Web\\jspr-code-master" +
                            "\\jspr-code-master\\01_web\\http-server\\public\\classic.html");

                    final var mimeType = Files.probeContentType(filePath);

                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.write(content);
                    responseStream.flush();
                    responseStream.close();
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.addHandler("GET", "/links.html", (x, y) -> {
            try {
                final var filePath = Path.of("A:\\HomeWorkNetology\\HTTP and the Modern Web" +
                        "\\jspr-code-master\\jspr-code-master\\01_web" +
                        "\\http-server\\public" + x.handlerName);

                final var mimeType = Files.probeContentType(filePath);

                final var length = Files.size(filePath);
                y.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, y);

                y.flush();
                y.close();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }

        });

        server.start();
    }
}


