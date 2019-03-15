package ru.iteco.springl.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Animal {

    String name;
    String type;

    public String getName() {
        return name;
    }

    public Animal setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Animal setType(String type) {
        this.type = type;
        return this;
    }
}
