package ru.iteco.springl.model;

import javax.persistence.*;

@Entity
@Table(name = "animal_join")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AnimalJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_seq")
    Long id;
    String name;
    String type;

    public String getName() {
        return name;
    }

    public AnimalJoin setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public AnimalJoin setType(String type) {
        this.type = type;
        return this;
    }

    public Long getId() {
        return id;
    }

    public AnimalJoin setId(Long id) {
        this.id = id;
        return this;
    }
}
