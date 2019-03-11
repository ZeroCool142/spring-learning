package ru.iteco.springl.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularA {

    private CircularB circularB;

    public CircularA() {
        System.out.println("In constructor CircularA dependency = " + circularB);
    }

    @PostConstruct
    public void init() {
        System.out.println("In init CircularA dependency = " + circularB);
    }

    @Autowired
    public void setCircularB(CircularB circularB) {
        System.out.println("Injecting CircularB");
        this.circularB = circularB;
    }

    public CircularB getCircularB() {
        return circularB;
    }
}
