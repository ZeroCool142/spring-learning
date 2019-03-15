package ru.iteco.springl.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    @ManyToMany(targetEntity = Student.class, cascade = CascadeType.ALL)
    List<Student> students = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Lesson setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Lesson setName(String name) {
        this.name = name;
        return this;
    }

    public List<Student> getStudents() {
        return students;
    }

    public Lesson setStudents(List<Student> students) {
        this.students = students;
        return this;
    }

    public Lesson addStudent(Student student) {
        students.add(student);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id) &&
                Objects.equals(name, lesson.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
