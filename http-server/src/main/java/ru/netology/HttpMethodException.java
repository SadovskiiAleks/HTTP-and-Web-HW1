package ru.netology;

public class HttpMethodException extends Exception {

    public HttpMethodException(String method){
        System.out.println("Set method \" " + method + " \" incorrectly, use GET or POST method");
    }
}
