import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Standalone JavaFX Employee Portal demo.
 *
 * This version intentionally uses in-memory data. It does not require a backend,
 * database, login service, or network connection. Data is lost when the app exits.
 *
 * Compile:
 * javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls EmployeeApp.java
 *
 * Run:
 * java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls EmployeeApp
 */
public class EmployeeApp extends Application {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();

    private String empName = "Aarav Sharma";
    private final String empId = "EMP-1042";
    private String empEmail = "aarav.sharma@company.com";
    private String empPhone = "+91 98765 43210";
    private final String empDept = "Engineering";
    private final String empDesignation = "Software Engineer";

    private final ObservableList<AttendanceRecord> attendanceData =
            FXCollections.observableArrayList(
                    new AttendanceRecord("2026-08-18", "Tuesday", "09:02 AM", "06:05 PM", "Present"),
                    new AttendanceRecord("2026-08-19", "Wednesday", "09:10 AM", "06:00 PM", "Present"),
                    new AttendanceRecord("2026-08-20", "Thursday", "--", "--", "Leave"),
                    new AttendanceRecord("2026-08-21", "Friday", "09:05 AM", "05:58 PM", "Present"),
                    new AttendanceRecord("2026-08-22", "Saturday", "09:20 AM", "--", "Present")
            );

    private final ObservableList<LeaveRecord> leaveData =
            FXCollections.observableArrayList(
                    new LeaveRecord("2026-07-14", "2026-07-15", "Sick Leave", "Approved"),
                    new LeaveRecord("2026-08-20", "2026-08-20", "Casual Leave", "Approved"),
                    new LeaveRecord("2026-09-01", "2026-09-03", "Vacation", "Pending")
            );

    private final ObservableList<SalaryRecord> salaryData =
            FXCollections.observableArrayList(
                    new SalaryRecord("June 2026", "₹60,000", "₹8,000", "₹4,500", "₹63,500"),
                    new SalaryRecord("July 2026", "₹60,000", "₹8,000", "₹4,500", "₹63,500"),
                    new SalaryRecord("August 2026", "₹60,000", "₹8,500", "₹4,700", "₹63,800")
            );

    private final ObservableList<String> notifications =
            FXCollections.observableArrayList(
                    "Your leave request for Sep 1-3 is pending approval.",
                    "Salary slip for August 2026 has been generated.",
                    "Reminder: Update your emergency contact details.",
                    "Company holiday on August 15 (Independence Day).",
                    "Your profile was updated successfully on Aug 10, 2026."
            );

    @Override
    public void start(Stage stage) {
        root.setStyle("-fx-background-color: #f4f6f8;");
        root.setLeft(buildSidebar());

        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().setAll(buildDashboard());
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Employee Portal");
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label logo = new Label("Employee Portal");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("System", FontWeight.BOLD, 16));
        logo.setPadding(new Insets(0, 0, 20, 4));
        logo.setWrapText(true);

        Button dashboardButton = navButton("Dashboard", () -> setContent(buildDashboard()));
        Button profileButton = navButton("Profile", () -> setContent(buildProfile()));
        Button attendanceButton = navButton("Attendance", () -> setContent(buildAttendance()));
        Button leaveButton = navButton("Leave", () -> setContent(buildLeave()));
        Button salaryButton = navButton("Salary", () -> setContent(buildSalary()));
        Button notificationsButton = navButton(
                "Notifications (" + notifications.size() + ")",
                () -> setContent(buildNotifications())
        );

        sidebar.getChildren().addAll(
                logo,
                dashboardButton,
                profileButton,
                attendanceButton,
                leaveButton,
                salaryButton,
                notificationsButton
        );
        return sidebar;
    }

    private Button navButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        setNormalNavStyle(button);
        button.setOnMouseEntered(event -> setHoverNavStyle(button));
        button.setOnMouseExited(event -> setNormalNavStyle(button));
        button.setOnAction(event -> action.run());
        return button;
    }

    private void setNormalNavStyle(Button button) {
        button.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; "
                        + "-fx-font-size: 13px; -fx-padding: 10 8 10 8;"
        );
    }

    private void setHoverNavStyle(Button button) {
        button.setStyle(
                "-fx-background-color: #34495e; -fx-text-fill: white; "
                        + "-fx-font-size: 13px; -fx-padding: 10 8 10 8; "
                        + "-fx-background-radius: 4;"
        );
    }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    private ScrollPane buildDashboard() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(10));

        Label title = pageTitle("Welcome back, " + firstName(empName));
        Label subtitle = new Label("Here is what is happening with your account today.");
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        long presentDays = attendanceData.stream()
                .filter(record -> "Present".equals(record.status.get()))
                .count();
        String latestSalary = salaryData.isEmpty()
                ? "--"
                : salaryData.get(salaryData.size() - 1).netPay.get();
        long pendingLeaves = leaveData.stream()
                .filter(record -> "Pending".equals(record.status.get()))
                .count();

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
                statCard("Attendance (last 5 days)", presentDays + " / " + attendanceData.size(), "#27ae60"),
                statCard("Pending Leave Requests", String.valueOf(pendingLeaves), "#e67e22"),
                statCard("Latest Net Salary", latestSalary, "#2980b9"),
                statCard("Notifications", String.valueOf(notifications.size()), "#8e44ad")
        );

        Label recentTitle = sectionTitle("Recent Notifications");
        VBox recentList = new VBox(6);
        notifications.stream()
                .limit(3)
                .forEach(notification -> recentList.getChildren().add(notificationRow(notification)));

        box.getChildren().addAll(title, subtitle, cards, recentTitle, recentList);
        return scrollable(box);
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setPrefWidth(190);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); "
                        + "-fx-border-color: " + color + "; -fx-border-width: 0 0 0 4;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label captionLabel = new Label(label);
        captionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        captionLabel.setWrapText(true);

        card.getChildren().addAll(valueLabel, captionLabel);
        return card;
    }

    private ScrollPane buildProfile() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(10));
        box.setMaxWidth(520);

        TextField nameField = labeledField(empName);
        TextField idField = labeledField(empId);
        TextField emailField = labeledField(empEmail);
        TextField phoneField = labeledField(empPhone);
        TextField deptField = labeledField(empDept);
        TextField designationField = labeledField(empDesignation);

        idField.setEditable(false);
        deptField.setEditable(false);
        designationField.setEditable(false);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty()) {
                showError(statusLabel, "Name cannot be empty.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showError(statusLabel, "Please enter a valid email address.");
                return;
            }

            empName = name;
            empEmail = email;
            empPhone = phoneField.getText().trim();
            showSuccess(statusLabel, "Profile updated successfully.");
        });

        VBox form = new VBox(10,
                labeledFieldBox("Full Name", nameField),
                labeledFieldBox("Employee ID", idField),
                labeledFieldBox("Email", emailField),
                labeledFieldBox("Phone", phoneField),
                labeledFieldBox("Department", deptField),
                labeledFieldBox("Designation", designationField),
                saveButton,
                statusLabel
        );
        form.setPadding(new Insets(16));
        form.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        box.getChildren().addAll(pageTitle("Employee Profile"), form);
        return scrollable(box);
    }

    private TextField labeledField(String initialValue) {
        return new TextField(initialValue);
    }

    private VBox labeledFieldBox(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        return new VBox(4, label, field);
    }

    private VBox buildAttendance() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(10));

        TableView<AttendanceRecord> table = new TableView<>(attendanceData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AttendanceRecord, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> data.getValue().date);
        TableColumn<AttendanceRecord, String> dayColumn = new TableColumn<>("Day");
        dayColumn.setCellValueFactory(data -> data.getValue().day);
        TableColumn<AttendanceRecord, String> inColumn = new TableColumn<>("Check-In");
        inColumn.setCellValueFactory(data -> data.getValue().checkIn);
        TableColumn<AttendanceRecord, String> outColumn = new TableColumn<>("Check-Out");
        outColumn.setCellValueFactory(data -> data.getValue().checkOut);
        TableColumn<AttendanceRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> data.getValue().status);

        table.getColumns().addAll(List.of(dateColumn, dayColumn, inColumn, outColumn, statusColumn));
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(pageTitle("Employee Attendance"), table);
        return box;
    }

    private VBox buildLeave() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(10));

        DatePicker fromPicker = new DatePicker(LocalDate.now());
        DatePicker toPicker = new DatePicker(LocalDate.now());
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "Sick Leave", "Casual Leave", "Vacation", "Unpaid Leave"
        ));
        typeBox.getSelectionModel().selectFirst();

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button applyButton = new Button("Apply for Leave");
        applyButton.setOnAction(event -> {
            LocalDate from = fromPicker.getValue();
            LocalDate to = toPicker.getValue();
            String type = typeBox.getValue();

            if (from == null || to == null) {
                showError(statusLabel, "Please select both start and end dates.");
                return;
            }
            if (to.isBefore(from)) {
                showError(statusLabel, "End date cannot be before start date.");
                return;
            }
            if (type == null || type.isBlank()) {
                showError(statusLabel, "Please select a leave type.");
                return;
            }

            leaveData.add(new LeaveRecord(from.toString(), to.toString(), type, "Pending"));
            showSuccess(statusLabel, "Leave request submitted for approval.");
        });

        HBox formRow = new HBox(10,
                labeledControlBox("From", fromPicker),
                labeledControlBox("To", toPicker),
                labeledControlBox("Type", typeBox),
                labeledControlBox("", applyButton)
        );
        formRow.setAlignment(Pos.BOTTOM_LEFT);
        formRow.setPadding(new Insets(16));
        formRow.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        TableView<LeaveRecord> table = new TableView<>(leaveData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LeaveRecord, String> fromColumn = new TableColumn<>("From");
        fromColumn.setCellValueFactory(data -> data.getValue().from);
        TableColumn<LeaveRecord, String> toColumn = new TableColumn<>("To");
        toColumn.setCellValueFactory(data -> data.getValue().to);
        TableColumn<LeaveRecord, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data -> data.getValue().type);
        TableColumn<LeaveRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> data.getValue().status);

        table.getColumns().addAll(List.of(fromColumn, toColumn, typeColumn, statusColumn));
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(pageTitle("Employee Leave"), formRow, statusLabel, table);
        return box;
    }

    private VBox buildSalary() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(10));

        TableView<SalaryRecord> table = new TableView<>(salaryData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SalaryRecord, String> monthColumn = new TableColumn<>("Month");
        monthColumn.setCellValueFactory(data -> data.getValue().month);
        TableColumn<SalaryRecord, String> basicColumn = new TableColumn<>("Basic Pay");
        basicColumn.setCellValueFactory(data -> data.getValue().basic);
        TableColumn<SalaryRecord, String> allowanceColumn = new TableColumn<>("Allowances");
        allowanceColumn.setCellValueFactory(data -> data.getValue().allowances);
        TableColumn<SalaryRecord, String> deductionColumn = new TableColumn<>("Deductions");
        deductionColumn.setCellValueFactory(data -> data.getValue().deductions);
        TableColumn<SalaryRecord, String> netColumn = new TableColumn<>("Net Pay");
        netColumn.setCellValueFactory(data -> data.getValue().netPay);

        table.getColumns().addAll(List.of(
                monthColumn, basicColumn, allowanceColumn, deductionColumn, netColumn
        ));
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(pageTitle("Employee Salary"), table);
        return box;
    }

    private ScrollPane buildNotifications() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().add(pageTitle("Notifications"));

        if (notifications.isEmpty()) {
            Label empty = new Label("You are all caught up. No notifications.");
            empty.setStyle("-fx-text-fill: #7f8c8d;");
            box.getChildren().add(empty);
        } else {
            for (String notification : notifications) {
                box.getChildren().add(notificationRow(notification));
            }
        }
        return scrollable(box);
    }

    private HBox notificationRow(String text) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: white; -fx-background-radius: 6; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 1);"
        );

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #2980b9; -fx-font-size: 10px;");
        Label message = new Label(text);
        message.setWrapText(true);
        HBox.setHgrow(message, Priority.ALWAYS);
        row.getChildren().addAll(dot, message);
        return row;
    }

    private ScrollPane scrollable(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private VBox labeledControlBox(String labelText, javafx.scene.Node control) {
        return new VBox(4, new Label(labelText), control);
    }

    private Label pageTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 22));
        return label;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 15));
        return label;
    }

    private void showError(Label label, String message) {
        label.setTextFill(Color.web("#c0392b"));
        label.setText(message);
    }

    private void showSuccess(Label label, String message) {
        label.setTextFill(Color.web("#27ae60"));
        label.setText(message);
    }

    private String firstName(String fullName) {
        String trimmed = fullName.trim();
        int firstSpace = trimmed.indexOf(' ');
        return firstSpace > 0 ? trimmed.substring(0, firstSpace) : trimmed;
    }

    public static class AttendanceRecord {
        final SimpleStringProperty date;
        final SimpleStringProperty day;
        final SimpleStringProperty checkIn;
        final SimpleStringProperty checkOut;
        final SimpleStringProperty status;

        AttendanceRecord(String date, String day, String checkIn, String checkOut, String status) {
            this.date = new SimpleStringProperty(date);
            this.day = new SimpleStringProperty(day);
            this.checkIn = new SimpleStringProperty(checkIn);
            this.checkOut = new SimpleStringProperty(checkOut);
            this.status = new SimpleStringProperty(status);
        }
    }

    public static class LeaveRecord {
        final SimpleStringProperty from;
        final SimpleStringProperty to;
        final SimpleStringProperty type;
        final SimpleStringProperty status;

        LeaveRecord(String from, String to, String type, String status) {
            this.from = new SimpleStringProperty(from);
            this.to = new SimpleStringProperty(to);
            this.type = new SimpleStringProperty(type);
            this.status = new SimpleStringProperty(status);
        }
    }

    public static class SalaryRecord {
        final SimpleStringProperty month;
        final SimpleStringProperty basic;
        final SimpleStringProperty allowances;
        final SimpleStringProperty deductions;
        final SimpleStringProperty netPay;

        SalaryRecord(String month, String basic, String allowances, String deductions, String netPay) {
            this.month = new SimpleStringProperty(month);
            this.basic = new SimpleStringProperty(basic);
            this.allowances = new SimpleStringProperty(allowances);
            this.deductions = new SimpleStringProperty(deductions);
            this.netPay = new SimpleStringProperty(netPay);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
