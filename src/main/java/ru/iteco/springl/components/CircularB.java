package ru.iteco.springl.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularB {

    private CircularA circularA;

    public CircularB(CircularA circularA) {
        System.out.println("In constructor CircularB dependency = " + circularA);
        this.circularA = circularA;
    }

    @PostConstruct
    public void init() {
        System.out.println("In init CircularB dependency = " + circularA);
    }


    public CircularA getCircularA() {
        return circularA;
    }
}
