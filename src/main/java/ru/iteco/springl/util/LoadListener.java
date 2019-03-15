package ru.iteco.springl.util;

import javax.persistence.PostLoad;

public class LoadListener {

    private static int counter = 0;

    @PostLoad
    public static void postLoad(Object o) {
        ++counter;
    }

    public static int getCounter() {
        return counter;
    }
}
