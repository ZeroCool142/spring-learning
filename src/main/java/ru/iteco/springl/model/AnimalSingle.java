package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Table(name = "animal_single")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ANIMAL_TYPE")
public abstract class AnimalSingle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "single_seq")
    Long id;
    String name;
    String type;

    public String getName() {
        return name;
    }

    public AnimalSingle setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public AnimalSingle setType(String type) {
        this.type = type;
        return this;
    }

    public Long getId() {
        return id;
    }

    public AnimalSingle setId(Long id) {
        this.id = id;
        return this;
    }
}
