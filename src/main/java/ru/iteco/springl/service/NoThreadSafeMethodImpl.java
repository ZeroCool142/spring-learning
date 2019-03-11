package ru.iteco.springl.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NoThreadSafeMethodImpl implements NoThreadSafeService {

    private Object l1 = new Object();
    private Object l2 = new Object();

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
}
