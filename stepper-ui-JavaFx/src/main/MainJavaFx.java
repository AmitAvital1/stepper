import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainJavaFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        TreeTableView<Person> treeTableView = new TreeTableView<>();

        // Create columns
        TreeTableColumn<Person, String> firstNameColumn = new TreeTableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));

        TreeTableColumn<Person, String> lastNameColumn = new TreeTableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));

        TreeTableColumn<Person, String> ageColumn = new TreeTableColumn<>("Age");
        ageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("age"));

        // Set the root node
        TreeItem<Person> root = new TreeItem<>(new Person("John", "Doe", "30"));
        treeTableView.setRoot(root);

        // Add columns to the TreeTableView
        treeTableView.getColumns().addAll(firstNameColumn, lastNameColumn, ageColumn);

        // Create a button to add a new row
        Button addButton = new Button("Add Row");
        addButton.setOnAction(event -> {
            TreeItem<Person> newItem = new TreeItem<>(new Person("New", "Person", "25"));
            root.getChildren().add(newItem);
        });

        VBox rootLayout = new VBox(treeTableView, addButton);
        Scene scene = new Scene(rootLayout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Person {
        private String firstName;
        private String lastName;
        private String age;

        public Person(String firstName, String lastName, String age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getAge() {
            return age;
        }
    }
}

//package main;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.net.URL;
//
//public class MainJavaFx extends Application {
//    public static final String APP_FXML_INCLUDE_RESOURCE = "/app/resources/main/app.fxml";
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
//        fxmlLoader.setLocation(url);
//        Parent root = fxmlLoader.load(url.openStream());
//
//        Scene scene = new Scene(root, 600, 550);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}