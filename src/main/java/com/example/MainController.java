package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller for Student management UI.
 */
public class MainController {

    private static final int PAGE_SIZE = 10;

    @FXML
    private TextField matriculeField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField ageField;

    @FXML
    private ComboBox<Departement> departementCombo;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final ObservableList<Departement> departements = FXCollections.observableArrayList();
    private Student selectedStudent;
    private int currentPage = 0;
    private int totalRecords = 0;
    private boolean showingSearchResults = false;

    @FXML
    public void initialize() {
        DepartementDAO.initDatabase();
        StudentDAO.initDatabase();

        departements.setAll(DepartementDAO.findAll());
        if (departementCombo != null) {
            departementCombo.setItems(departements);
        }

        studentTable.setItems(studentList);
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedStudent = newSelection;
            if (selectedStudent != null) {
                loadStudentToForm(selectedStudent);
            }
        });

        loadTotalRecords();
        loadPage(0);
        updatePaginationButtons();
    }

    @FXML
    private void handleAdd() {
        if (!validationFields()) {
            return;
        }

        Student student = buildStudentFromForm();
        boolean saved = StudentDAO.insert(student);
        if (!saved) {
            showAlert("Error", "Failed to save student. Ensure the matricule is unique and try again.");
            return;
        }

        loadTotalRecords();
        currentPage = getLastPageIndex();
        showingSearchResults = false;
        loadPage(currentPage);
        updatePaginationButtons();
        clearFields();
        showAlert("Success", "Student added successfully!");
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null) {
            showAlert("Warning", "Please select a student from the table to update.");
            return;
        }
        if (!validationFields()) {
            return;
        }

        selectedStudent.setNom(nomField.getText().trim());
        selectedStudent.setAdresse(adresseField.getText().trim());
        selectedStudent.setAge(Integer.parseInt(ageField.getText().trim()));
        selectedStudent.setDepartement(departementCombo.getValue());
        boolean updated = StudentDAO.update(selectedStudent);
        if (!updated) {
            showAlert("Error", "Failed to update student in database.");
            return;
        }

        reloadDataAfterMutation();
        showAlert("Success", "Student updated successfully!");
    }

    @FXML
    private void handleDelete() {
        if (selectedStudent == null) {
            showAlert("Warning", "Please select a student from the table to delete.");
            return;
        }
        boolean deleted = StudentDAO.delete(selectedStudent.getMatricule());
        if (!deleted) {
            showAlert("Error", "Failed to delete student from database.");
            return;
        }

        reloadDataAfterMutation();
        showAlert("Success", "Student deleted successfully!");
    }

    @FXML
    private void handleShow() {
        if (selectedStudent == null) {
            showAlert("Info", "Select a student from the table to view details.");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Matricule: ").append(selectedStudent.getMatricule()).append("\n");
        details.append("Nom: ").append(selectedStudent.getNom()).append("\n");
        details.append("Adresse: ").append(selectedStudent.getAdresse()).append("\n");
        details.append("Age: ").append(selectedStudent.getAge()).append("\n");
        Departement departement = selectedStudent.getDepartement();
        details.append("Departement: ").append(departement != null ? departement.getNom() : "-");

        showAlert("Student Details", details.toString());
    }

    @FXML
    private void handleNewStudent() {
        clearFields();
    }

    @FXML
    private void handleAfficher() {
        showingSearchResults = false;
        if (searchField != null) {
            searchField.clear();
        }
        loadTotalRecords();
        loadPage(0);
        updatePaginationButtons();
    }

    @FXML
    private void handleSearch() {
        if (searchField == null) {
            return;
        }
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            handleAfficher();
            return;
        }
        showingSearchResults = true;
        studentList.setAll(StudentDAO.search(keyword));
        currentPage = 0;
        updatePaginationButtons();
        if (studentList.isEmpty()) {
            showAlert("Info", "No students found for the search criteria.");
        }
    }

    @FXML
    private void handlePrevPage() {
        if (showingSearchResults || currentPage <= 0) {
            return;
        }
        loadPage(currentPage - 1);
        updatePaginationButtons();
    }

    @FXML
    private void handleNextPage() {
        if (showingSearchResults || (currentPage + 1) * PAGE_SIZE >= totalRecords) {
            return;
        }
        loadPage(currentPage + 1);
        updatePaginationButtons();
    }

    private Student buildStudentFromForm() {
        String matricule = matriculeField.getText().trim();
        String nom = nomField.getText().trim();
        String adresse = adresseField.getText().trim();
        int age = Integer.parseInt(ageField.getText().trim());
        Departement departement = departementCombo.getValue();
        return new Student(matricule, nom, adresse, age, departement);
    }

    private void loadStudentToForm(Student student) {
        matriculeField.setText(student.getMatricule());
        nomField.setText(student.getNom());
        adresseField.setText(student.getAdresse());
        ageField.setText(String.valueOf(student.getAge()));
        Departement studentDepartement = student.getDepartement();
        if (studentDepartement != null) {
            departements.stream()
                    .filter(dep -> dep.getId() == studentDepartement.getId())
                    .findFirst()
                    .ifPresent(dep -> departementCombo.getSelectionModel().select(dep));
        } else {
            departementCombo.getSelectionModel().clearSelection();
        }
        matriculeField.setDisable(true);
    }

    private void clearFields() {
        matriculeField.clear();
        nomField.clear();
        adresseField.clear();
        ageField.clear();
        departementCombo.getSelectionModel().clearSelection();
        selectedStudent = null;
        studentTable.getSelectionModel().clearSelection();
        matriculeField.setDisable(false);
    }

    private boolean validationFields() {
        if (matriculeField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Matricule cannot be empty.");
            return false;
        }
        if (nomField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Nom cannot be empty.");
            return false;
        }
        if (adresseField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Adresse cannot be empty.");
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Age cannot be empty.");
            return false;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 0) {
                showAlert("Validation Error", "Age must be positive.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Age must be a valid number.");
            return false;
        }
        if (departementCombo.getValue() == null) {
            showAlert("Validation Error", "Please select a departement.");
            return false;
        }
        return true;
    }

    private void loadTotalRecords() {
        totalRecords = StudentDAO.countAll();
    }

    private void loadPage(int pageIndex) {
        if (showingSearchResults) {
            return;
        }
        if (totalRecords == 0) {
            studentList.clear();
            currentPage = 0;
            return;
        }
        int maxPageIndex = getLastPageIndex();
        pageIndex = Math.max(0, Math.min(pageIndex, maxPageIndex));
        int offset = pageIndex * PAGE_SIZE;
        studentList.setAll(StudentDAO.findPage(PAGE_SIZE, offset));
        currentPage = pageIndex;
    }

    private int getLastPageIndex() {
        if (totalRecords == 0) {
            return 0;
        }
        return (totalRecords - 1) / PAGE_SIZE;
    }

    private void updatePaginationButtons() {
        boolean disablePaging = showingSearchResults || totalRecords == 0;
        if (prevPageButton != null) {
            prevPageButton.setDisable(disablePaging || currentPage == 0);
        }
        if (nextPageButton != null) {
            boolean onLastPage = disablePaging || ((currentPage + 1) * PAGE_SIZE >= totalRecords);
            nextPageButton.setDisable(onLastPage);
        }
    }

    private void reloadDataAfterMutation() {
        loadTotalRecords();
        if (currentPage > getLastPageIndex()) {
            currentPage = getLastPageIndex();
        }
        showingSearchResults = false;
        loadPage(currentPage);
        updatePaginationButtons();
        clearFields();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
