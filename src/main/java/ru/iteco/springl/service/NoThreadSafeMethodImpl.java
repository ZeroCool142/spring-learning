package ru.iteco.springl.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NoThreadSafeMethodImpl implements NoThreadSafeService {

    private final Object l1 = new Object();
    private final Object l2 = new Object();

    private int i = 0;

    private ArrayList<Object> objects = new ArrayList<>();

    @Override
    public String method1() {
        synchronized (l1) {
            try {
                Thread.sleep(3000);
                method2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "method1";
    }

    @Override
    public String method2() {
        synchronized (l2) {
            try {
                Thread.sleep(2000);
                method1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "method2";
    }

    @Override
    public int increment() {
        return ++i;
    }

    @Override
    public int getIncrement() {
        return i;
    }
}
