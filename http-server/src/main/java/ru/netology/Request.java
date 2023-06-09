package ru.netology;

import java.io.*;
import java.util.*;

public class Request {
    Boolean badRequest = false;

    String methodOfHandler;
    String handlerName;
    String httpVersion;
    int limit;

    List<String> listOfHeaders = new ArrayList<>();
    Map<String, String> mapOfHeaders = new HashMap<>();

    byte[] body;

    public Request(int limit) {
        this.limit = limit;
    }


    Request addRequest(InputStream inputStream) throws IOException {


        final var in = new BufferedInputStream(inputStream);

        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);


        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            badRequest = true;
        }

        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1


        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            badRequest = true;
        }
        methodOfHandler = requestLine[0];
        System.out.println(methodOfHandler);

        handlerName = requestLine[1];
        if (!handlerName.startsWith("/")) {
            badRequest = true;
        }
        System.out.println(handlerName);
        httpVersion = requestLine[2];


        //read all headers
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            badRequest = true;

        }

        in.reset();
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        listOfHeaders = Arrays.asList(new String(headersBytes).split("\r\n"));
        System.out.println(listOfHeaders);

        //"тело запроса, если есть."
        Boolean isHaveBody = listOfHeaders.stream().filter(x -> x.startsWith("Content-Length")).findFirst().isPresent();
        System.out.println("Request have body: " + isHaveBody);

        if (isHaveBody != null) {
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(listOfHeaders, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                body = in.readNBytes(length);
                System.out.println(new String(body));
            }
        }
        return this;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

}