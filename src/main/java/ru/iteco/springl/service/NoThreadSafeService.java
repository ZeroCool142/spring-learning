package ru.iteco.springl.service;

public interface NoThreadSafeService {
    String method1();

    String method2();

    int increment();

    int getIncrement();
}
