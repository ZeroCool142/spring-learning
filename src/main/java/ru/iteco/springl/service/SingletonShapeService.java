package ru.iteco.springl.service;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.iteco.springl.components.Shape;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SingletonShapeService implements ShapeService {

    private final Shape shape;

    public SingletonShapeService(Shape shape) {
        System.out.println("Service constructor");
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    @Lookup
    @Override
    public Shape getRandomShape() {
        return null;
    }

    @PostConstruct
    public void init() {
        System.out.println("Service init() method");
    }

    @PreDestroy
    public void cleanUp() {
        System.out.println("Service cleanUp() method");
    }
}
