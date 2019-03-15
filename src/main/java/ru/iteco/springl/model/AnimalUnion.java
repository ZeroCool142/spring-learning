package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AnimalUnion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "per_class_seq")
    Long id;
    String name;
    String type;

    public String getName() {
        return name;
    }

    public AnimalUnion setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public AnimalUnion setType(String type) {
        this.type = type;
        return this;
    }

    public Long getId() {
        return id;
    }

    public AnimalUnion setId(Long id) {
        this.id = id;
        return this;
    }
}
