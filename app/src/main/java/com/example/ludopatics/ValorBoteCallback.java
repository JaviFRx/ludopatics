package com.example.ludopatics;

public interface ValorBoteCallback {
    void onValorObtenido(int valor);
    void onError(Exception e);
}