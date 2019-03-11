package ru.iteco.springl.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularB {

    private CircularA circularA;

    public CircularB() {
        System.out.println("In constructor CircularB dependency = " + circularA);
    }

    @PostConstruct
    public void init() {
        System.out.println("In init CircularB dependency = " + circularA);
    }

    @Autowired
    public void setCircularA(CircularA circularA) {
        System.out.println("Injecting CircularA");
        this.circularA = circularA;
    }

    public CircularA getCircularA() {
        return circularA;
    }
}
