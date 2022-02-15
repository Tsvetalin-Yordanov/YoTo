package com.example.yoto.model.exceptions;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String msg){
        super(msg);
    }
}