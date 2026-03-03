package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Domain model for Student records.
 */
public class Student {
    private final StringProperty matricule;
    private final StringProperty nom;
    private final StringProperty adresse;
    private final IntegerProperty age;
    private final ObjectProperty<Departement> departement;
    private final StringProperty departementName;

    public Student(String matricule, String nom, String adresse, int age, Departement departement) {
        this.matricule = new SimpleStringProperty(matricule);
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty(adresse);
        this.age = new SimpleIntegerProperty(age);
        this.departement = new SimpleObjectProperty<>();
        this.departementName = new SimpleStringProperty();
        setDepartement(departement);
    }

    public String getMatricule() {
        return matricule.get();
    }

    public StringProperty matriculeProperty() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule.set(matricule);
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

    public String getAdresse() {
        return adresse.get();
    }

    public StringProperty adresseProperty() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public Departement getDepartement() {
        return departement.get();
    }

    public ObjectProperty<Departement> departementProperty() {
        return departement;
    }

    public StringProperty departementNameProperty() {
        return departementName;
    }

    public String getDepartementName() {
        return departementName.get();
    }

    public void setDepartement(Departement departement) {
        Departement previous = this.departement.get();
        if (previous != null) {
            previous.unregisterStudent(this);
        }
        this.departement.set(departement);
        if (departement != null) {
            departement.registerStudent(this);
            departementName.set(departement.getNom());
        } else {
            departementName.set(null);
        }
    }
}
