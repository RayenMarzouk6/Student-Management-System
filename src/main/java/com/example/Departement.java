package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Departement entity used as a lookup and relationship target for students.
 */
public class Departement {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final ObservableList<Student> students = FXCollections.observableArrayList();

    public Departement(int id, String nom) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    ObservableList<Student> getStudents() {
        return students;
    }

    void registerStudent(Student student) {
        if (student != null && !students.contains(student)) {
            students.add(student);
        }
    }

    void unregisterStudent(Student student) {
        students.remove(student);
    }

    @Override
    public String toString() {
        return getNom();
    }
}
