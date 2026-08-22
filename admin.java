import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * Standalone JavaFX admin dashboard frontend demo.
 *
 * This application uses in-memory sample data only. It does not require a
 * backend, database, authentication service, or network connection.
 *
 * Compile:
 * javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls AdminDashboardApp.java
 *
 * Run:
 * java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls AdminDashboardApp
 */
public class AdminDashboardApp extends Application {

    private static final String PAGE_BACKGROUND = "#f4f7fb";
    private static final String NAV_BACKGROUND = "#172033";
    private static final String PRIMARY = "#2563eb";
    private static final String TEXT_DARK = "#172033";
    private static final String TEXT_MUTED = "#64748b";

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final Label pageHeading = new Label("Dashboard");
    private final Label statusLabel = new Label("Ready");
    private final ObservableList<Employee> employees = FXCollections.observableArrayList(
            new Employee("EMP-1001", "Aarav Sharma", "Engineering", "Software Engineer", "Active"),
            new Employee("EMP-1002", "Maya Patel", "Design", "Product Designer", "Active"),
            new Employee("EMP-1003", "Rohan Mehta", "Finance", "Accountant", "On Leave"),
            new Employee("EMP-1004", "Anika Singh", "Human Resources", "HR Manager", "Active"),
            new Employee("EMP-1005", "Kabir Verma", "Engineering", "QA Engineer", "Inactive")
    );

    private final ObservableList<AttendanceRecord> attendance = FXCollections.observableArrayList(
            new AttendanceRecord("2026-08-22", "Aarav Sharma", "09:02 AM", "06:05 PM", "Present"),
            new AttendanceRecord("2026-08-22", "Maya Patel", "09:18 AM", "06:10 PM", "Present"),
            new AttendanceRecord("2026-08-22", "Rohan Mehta", "--", "--", "On Leave"),
            new AttendanceRecord("2026-08-22", "Anika Singh", "08:55 AM", "05:45 PM", "Present"),
            new AttendanceRecord("2026-08-22", "Kabir Verma", "--", "--", "Absent")
    );

    private final ObservableList<LeaveRequest> leaveRequests = FXCollections.observableArrayList(
            new LeaveRequest("LR-2401", "Rohan Mehta", "Sick Leave", "2026-08-20", "2026-08-22", "Pending"),
            new LeaveRequest("LR-2402", "Maya Patel", "Vacation", "2026-09-01", "2026-09-03", "Pending"),
            new LeaveRequest("LR-2403", "Aarav Sharma", "Casual Leave", "2026-08-28", "2026-08-28", "Approved"),
            new LeaveRequest("LR-2404", "Kabir Verma", "Personal Leave", "2026-08-25", "2026-08-26", "Rejected")
    );

    private final ObservableList<PayrollRecord> payroll = FXCollections.observableArrayList(
            new PayrollRecord("August 2026", "128", "₹76,80,000", "₹9,42,000", "₹67,38,000", "Processed"),
            new PayrollRecord("July 2026", "127", "₹76,20,000", "₹9,18,000", "₹67,02,000", "Processed"),
            new PayrollRecord("June 2026", "126", "₹75,60,000", "₹9,05,000", "₹66,55,000", "Processed")
    );

    @Override
    public void start(Stage stage) {
        root.setLeft(buildSidebar());
        root.setTop(buildHeader());
        contentArea.setPadding(new Insets(24));
        contentArea.setStyle("-fx-background-color: " + PAGE_BACKGROUND + ";");
        root.setCenter(contentArea);
        showPage("Dashboard", buildDashboard());

        Scene scene = new Scene(root, 1280, 760);
        stage.setTitle("Admin Dashboard");
        stage.setMinWidth(980);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(238);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setStyle("-fx-background-color: " + NAV_BACKGROUND + ";");

        Label brand = new Label("WORKFORCE\nADMIN");
        brand.setTextFill(Color.WHITE);
        brand.setFont(Font.font("System", FontWeight.BOLD, 20));
        brand.setPadding(new Insets(0, 0, 28, 10));

        Label menuLabel = new Label("MAIN MENU");
        menuLabel.setTextFill(Color.web("#8da2c0"));
        menuLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        menuLabel.setPadding(new Insets(0, 0, 6, 10));

        sidebar.getChildren().addAll(
                brand,
                menuLabel,
                navigationButton("Dashboard", "▦", () -> showPage("Dashboard", buildDashboard())),
                navigationButton("Employee Management", "♙", () -> showPage("Employee Management", buildEmployeeManagement())),
                navigationButton("Admin Attendance", "◷", () -> showPage("Admin Attendance", buildAttendance())),
                navigationButton("Leave Approval", "✓", () -> showPage("Leave Approval", buildLeaveApproval())),
                navigationButton("Payroll", "₹", () -> showPage("Payroll", buildPayroll())),
                navigationButton("Analytics / Reports", "▥", () -> showPage("Analytics / Reports", buildAnalytics()))
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        sidebar.getChildren().add(navigationButton("Logout", "↪", this::logout));
        return sidebar;
    }

    private Button navigationButton(String text, String icon, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setTextFill(Color.web("#9fb3d0"));
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        iconLabel.setMinWidth(26);

        Label textLabel = new Label(text);
        textLabel.setTextFill(Color.web("#d9e3f1"));
        textLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));

        HBox content = new HBox(9, iconLabel, textLabel);
        content.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(11, 12, 11, 12));
        setNavigationStyle(button, false);
        button.setOnMouseEntered(event -> setNavigationStyle(button, true));
        button.setOnMouseExited(event -> setNavigationStyle(button, false));
        button.setOnAction(event -> action.run());
        return button;
    }

    private void setNavigationStyle(Button button, boolean hover) {
        button.setStyle(hover
                ? "-fx-background-color: #263653; -fx-background-radius: 7;"
                : "-fx-background-color: transparent; -fx-background-radius: 7;");
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e5eaf1; -fx-border-width: 0 0 1 0;");

        pageHeading.setFont(Font.font("System", FontWeight.BOLD, 22));
        pageHeading.setTextFill(Color.web(TEXT_DARK));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusText = new Label("●  System online");
        statusText.setTextFill(Color.web("#16a34a"));
        statusText.setFont(Font.font("System", FontWeight.NORMAL, 12));

        Label adminName = new Label("Admin User");
        adminName.setTextFill(Color.web(TEXT_DARK));
        adminName.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label adminRole = new Label("Administrator");
        adminRole.setTextFill(Color.web(TEXT_MUTED));
        adminRole.setFont(Font.font("System", FontWeight.NORMAL, 11));

        VBox profile = new VBox(2, adminName, adminRole);
        profile.setAlignment(Pos.CENTER_RIGHT);

        Label avatar = new Label("AU");
        avatar.setTextFill(Color.WHITE);
        avatar.setFont(Font.font("System", FontWeight.BOLD, 12));
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(36, 36);
        avatar.setStyle("-fx-background-color: " + PRIMARY + "; -fx-background-radius: 18;");

        header.getChildren().addAll(pageHeading, spacer, statusText, new Separator(), profile, avatar);
        return header;
    }

    private void showPage(String title, Node page) {
        pageHeading.setText(title);
        contentArea.getChildren().setAll(page);
    }

    private ScrollPane buildDashboard() {
        VBox page = pageContainer();
        page.getChildren().addAll(
                pageIntro("Good morning, Admin", "Here is an overview of your organization today."),
                metricCards(),
                sectionHeader("Today at a glance", "Live operational summary"),
                dashboardPanels()
        );
        return scrollable(page);
    }

    private HBox metricCards() {
        long activeEmployees = employees.stream().filter(e -> "Active".equals(e.status.get())).count();
        long presentToday = attendance.stream().filter(a -> "Present".equals(a.status.get())).count();
        long pendingLeaves = leaveRequests.stream().filter(l -> "Pending".equals(l.status.get())).count();

        HBox cards = new HBox(16,
                metricCard("Total Employees", String.valueOf(employees.size()), "+8.2%", "#2563eb"),
                metricCard("Present Today", presentToday + " / " + employees.size(), "" + activeEmployees + " active", "#16a34a"),
                metricCard("Pending Leave", String.valueOf(pendingLeaves), "Needs review", "#f59e0b"),
                metricCard("Monthly Payroll", "₹67.38L", "August 2026", "#7c3aed")
        );
        return cards;
    }

    private VBox metricCard(String title, String value, String detail, String accent) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_MUTED));
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT_DARK));
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label detailLabel = new Label(detail);
        detailLabel.setTextFill(Color.web(accent));
        detailLabel.setFont(Font.font("System", FontWeight.BOLD, 11));

        VBox card = new VBox(9, titleLabel, valueLabel, detailLabel);
        card.setPadding(new Insets(18));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-border-color: #e6ebf2; -fx-border-radius: 10; "
                + "-fx-border-width: 1 1 1 4; -fx-border-insets: 0 0 0 0; "
                + "-fx-effect: dropshadow(gaussian, rgba(23,32,51,0.06), 8, 0, 0, 2);");
        return card;
    }

    private HBox dashboardPanels() {
        VBox attendancePanel = new VBox(12);
        attendancePanel.setPadding(new Insets(18));
        attendancePanel.setStyle(cardStyle());
        attendancePanel.getChildren().add(sectionHeader("Attendance today", "By current status"));
        attendancePanel.getChildren().add(progressRow("Present", 3, employees.size(), "#16a34a"));
        attendancePanel.getChildren().add(progressRow("On Leave", 1, employees.size(), "#f59e0b"));
        attendancePanel.getChildren().add(progressRow("Absent", 1, employees.size(), "#ef4444"));

        VBox leavePanel = new VBox(12);
        leavePanel.setPadding(new Insets(18));
        leavePanel.setStyle(cardStyle());
        leavePanel.getChildren().add(sectionHeader("Recent leave requests", "Latest activity"));
        leaveRequests.stream().limit(3).forEach(request -> leavePanel.getChildren().add(
                activityRow(request.employee.get(), request.leaveType.get(), request.status.get())
        ));

        HBox panels = new HBox(16, attendancePanel, leavePanel);
        HBox.setHgrow(attendancePanel, Priority.ALWAYS);
        HBox.setHgrow(leavePanel, Priority.ALWAYS);
        attendancePanel.setPrefWidth(420);
        leavePanel.setPrefWidth(420);
        return panels;
    }

    private HBox progressRow(String label, int value, int total, String color) {
        Label name = new Label(label);
        name.setTextFill(Color.web(TEXT_DARK));
        name.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label count = new Label(value + " employees");
        count.setTextFill(Color.web(TEXT_MUTED));
        count.setFont(Font.font("System", FontWeight.NORMAL, 11));

        Region track = new Region();
        track.setPrefHeight(7);
        track.setStyle("-fx-background-color: #e9eef5; -fx-background-radius: 4;");
        Region fill = new Region();
        fill.setPrefHeight(7);
        fill.setPrefWidth(Math.max(20, 250.0 * value / total));
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        HBox.setHgrow(bar, Priority.ALWAYS);
        HBox row = new HBox(12, name, bar, count);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox activityRow(String name, String type, String status) {
        Label person = new Label(name);
        person.setTextFill(Color.web(TEXT_DARK));
        person.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label reason = new Label(type);
        reason.setTextFill(Color.web(TEXT_MUTED));
        reason.setFont(Font.font("System", FontWeight.NORMAL, 11));
        VBox details = new VBox(3, person, reason);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = statusBadge(status);
        HBox row = new HBox(10, details, spacer, badge);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        return row;
    }

    private VBox buildEmployeeManagement() {
        VBox page = pageContainer();
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, ID, department...");
        searchField.setPrefWidth(330);

        Button addButton = primaryButton("+ Add Employee");
        addButton.setOnAction(event -> showSuccess("Employee form opened. Connect this action to your backend later."));
        Button exportButton = secondaryButton("Export CSV");
        exportButton.setOnAction(event -> showSuccess("Employee data prepared for export."));
        HBox toolbar = new HBox(10, searchField, addButton, exportButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<Employee> table = new TableView<>(employees);
        table.setPlaceholder(new Label("No employees found"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Employee ID", employee -> employee.id),
                textColumn("Name", employee -> employee.name),
                textColumn("Department", employee -> employee.department),
                textColumn("Designation", employee -> employee.designation),
                textColumn("Status", employee -> employee.status)
        );
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.trim().toLowerCase();
            table.setItems(query.isEmpty()
                    ? employees
                    : FXCollections.observableArrayList(employees.stream().filter(employee ->
                            employee.name.get().toLowerCase().contains(query)
                                    || employee.id.get().toLowerCase().contains(query)
                                    || employee.department.get().toLowerCase().contains(query)
                    ).toList()));
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        page.getChildren().addAll(
                pageIntro("Employee Management", "View, search, and maintain employee records."),
                toolbar,
                table
        );
        return page;
    }

    private VBox buildAttendance() {
        VBox page = pageContainer();
        ComboBox<String> dateFilter = new ComboBox<>(FXCollections.observableArrayList(
                "Today - 22 Aug 2026", "Yesterday - 21 Aug 2026", "This week"
        ));
        dateFilter.getSelectionModel().selectFirst();
        Button downloadButton = secondaryButton("Download Report");
        downloadButton.setOnAction(event -> showSuccess("Attendance report prepared."));
        HBox toolbar = new HBox(10, dateFilter, downloadButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<AttendanceRecord> table = new TableView<>(attendance);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Date", item -> item.date),
                textColumn("Employee", item -> item.employee),
                textColumn("Check-in", item -> item.checkIn),
                textColumn("Check-out", item -> item.checkOut),
                textColumn("Status", item -> item.status)
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(
                pageIntro("Admin Attendance", "Monitor attendance and daily workforce presence."),
                toolbar,
                table
        );
        return page;
    }

    private VBox buildLeaveApproval() {
        VBox page = pageContainer();
        Label pendingLabel = new Label("" + leaveRequests.stream().filter(item -> "Pending".equals(item.status.get())).count() + " requests awaiting review");
        pendingLabel.setTextFill(Color.web(TEXT_MUTED));
        pendingLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        TableView<LeaveRequest> table = new TableView<>(leaveRequests);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Request ID", item -> item.requestId),
                textColumn("Employee", item -> item.employee),
                textColumn("Leave Type", item -> item.leaveType),
                textColumn("From", item -> item.from),
                textColumn("To", item -> item.to),
                textColumn("Status", item -> item.status)
        );

        Button approveButton = primaryButton("Approve Selected");
        approveButton.setOnAction(event -> updateSelectedLeave(table, "Approved"));
        Button rejectButton = dangerButton("Reject Selected");
        rejectButton.setOnAction(event -> updateSelectedLeave(table, "Rejected"));
        HBox actions = new HBox(10, approveButton, rejectButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(
                pageIntro("Leave Approval", "Review employee leave requests and update their status."),
                pendingLabel,
                table,
                actions
        );
        return page;
    }

    private void updateSelectedLeave(TableView<LeaveRequest> table, String newStatus) {
        LeaveRequest selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a leave request first.");
            return;
        }
        selected.status.set(newStatus);
        table.refresh();
        showSuccess("Leave request " + selected.requestId.get() + " marked as " + newStatus + ".");
    }

    private VBox buildPayroll() {
        VBox page = pageContainer();
        Button processButton = primaryButton("Process Current Payroll");
        processButton.setOnAction(event -> showSuccess("Payroll processing started. This demo does not connect to a bank or payroll service."));
        Button exportButton = secondaryButton("Export Payroll");
        exportButton.setOnAction(event -> showSuccess("Payroll report prepared for export."));
        HBox toolbar = new HBox(10, processButton, exportButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<PayrollRecord> table = new TableView<>(payroll);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Pay Period", item -> item.period),
                textColumn("Employees", item -> item.employeeCount),
                textColumn("Gross Pay", item -> item.grossPay),
                textColumn("Deductions", item -> item.deductions),
                textColumn("Net Pay", item -> item.netPay),
                textColumn("Status", item -> item.status)
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(
                pageIntro("Payroll", "Review payroll summaries and processing status."),
                toolbar,
                table
        );
        return page;
    }

    private VBox buildAnalytics() {
        VBox page = pageContainer();
        page.getChildren().add(pageIntro("Analytics / Reports", "High-level workforce insights based on demo data."));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.add(reportCard("Attendance Rate", "94.6%", "▲ 2.4% from last month", "#16a34a"), 0, 0);
        grid.add(reportCard("Leave Utilization", "18.2%", "Within annual policy", "#2563eb"), 1, 0);
        grid.add(reportCard("Average Tenure", "2.8 years", "Across active employees", "#7c3aed"), 0, 1);
        grid.add(reportCard("Open Positions", "12", "4 new this month", "#f59e0b"), 1, 1);
        page.getChildren().add(grid);

        VBox departmentCard = new VBox(12);
        departmentCard.setPadding(new Insets(18));
        departmentCard.setStyle(cardStyle());
        departmentCard.getChildren().add(sectionHeader("Headcount by department", "Current active workforce"));
        departmentCard.getChildren().add(progressRow("Engineering", 2, 5, "#2563eb"));
        departmentCard.getChildren().add(progressRow("Design", 1, 5, "#7c3aed"));
        departmentCard.getChildren().add(progressRow("Finance", 1, 5, "#16a34a"));
        departmentCard.getChildren().add(progressRow("Human Resources", 1, 5, "#f59e0b"));
        page.getChildren().add(departmentCard);

        Button reportButton = primaryButton("Generate Monthly Report");
        reportButton.setOnAction(event -> showSuccess("Monthly analytics report generated successfully."));
        page.getChildren().add(reportButton);
        return page;
    }

    private VBox reportCard(String title, String value, String detail, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_MUTED));
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT_DARK));
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        Label detailLabel = new Label(detail);
        detailLabel.setTextFill(Color.web(color));
        detailLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        VBox card = new VBox(8, titleLabel, valueLabel, detailLabel);
        card.setPadding(new Insets(18));
        card.setPrefWidth(300);
        card.setStyle(cardStyle());
        return card;
    }

    private VBox pageContainer() {
        VBox page = new VBox(18);
        page.setPadding(new Insets(4));
        page.setFillWidth(true);
        return page;
    }

    private VBox pageIntro(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_DARK));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setTextFill(Color.web(TEXT_MUTED));
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        return new VBox(5, titleLabel, subtitleLabel);
    }

    private HBox sectionHeader(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_DARK));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setTextFill(Color.web(TEXT_MUTED));
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        VBox text = new VBox(3, titleLabel, subtitleLabel);
        HBox row = new HBox(text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-border-color: #e6ebf2; -fx-border-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(23,32,51,0.06), 8, 0, 0, 2);";
    }

    private Label statusBadge(String status) {
        Label badge = new Label(status);
        badge.setFont(Font.font("System", FontWeight.BOLD, 10));
        if ("Approved".equals(status) || "Present".equals(status) || "Active".equals(status) || "Processed".equals(status)) {
            badge.setTextFill(Color.web("#15803d"));
            badge.setStyle("-fx-background-color: #dcfce7; -fx-background-radius: 12; -fx-padding: 5 9 5 9;");
        } else if ("Pending".equals(status) || "On Leave".equals(status)) {
            badge.setTextFill(Color.web("#b45309"));
            badge.setStyle("-fx-background-color: #fef3c7; -fx-background-radius: 12; -fx-padding: 5 9 5 9;");
        } else {
            badge.setTextFill(Color.web("#b91c1c"));
            badge.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 12; -fx-padding: 5 9 5 9;");
        }
        return badge;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setStyle("-fx-background-color: " + PRIMARY + "; -fx-background-radius: 6; -fx-padding: 10 14 10 14;");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.web(TEXT_DARK));
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 9 13 9 13;");
        return button;
    }

    private Button dangerButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 6; -fx-padding: 10 14 10 14;");
        return button;
    }

    private <T> TableColumn<T, String> textColumn(String title, PropertyGetter<T> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> getter.get(data.getValue()));
        return column;
    }

    private ScrollPane scrollable(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private void showSuccess(String message) {
        statusLabel.setTextFill(Color.web("#15803d"));
        statusLabel.setText("Success: " + message);
    }

    private void showError(String message) {
        statusLabel.setTextFill(Color.web("#b91c1c"));
        statusLabel.setText("Error: " + message);
    }

    private void logout() {
        statusLabel.setTextFill(Color.web("#b45309"));
        statusLabel.setText("Logged out of the demo dashboard.");
        showPage("Dashboard", buildDashboard());
    }

    @FunctionalInterface
    private interface PropertyGetter<T> {
        SimpleStringProperty get(T value);
    }

    private static class Employee {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty department;
        private final SimpleStringProperty designation;
        private final SimpleStringProperty status;

        private Employee(String id, String name, String department, String designation, String status) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
            this.designation = new SimpleStringProperty(designation);
            this.status = new SimpleStringProperty(status);
        }
    }

    private static class AttendanceRecord {
        private final SimpleStringProperty date;
        private final SimpleStringProperty employee;
        private final SimpleStringProperty checkIn;
        private final SimpleStringProperty checkOut;
        private final SimpleStringProperty status;

        private AttendanceRecord(String date, String employee, String checkIn, String checkOut, String status) {
            this.date = new SimpleStringProperty(date);
            this.employee = new SimpleStringProperty(employee);
            this.checkIn = new SimpleStringProperty(checkIn);
            this.checkOut = new SimpleStringProperty(checkOut);
            this.status = new SimpleStringProperty(status);
        }
    }

    private static class LeaveRequest {
        private final SimpleStringProperty requestId;
        private final SimpleStringProperty employee;
        private final SimpleStringProperty leaveType;
        private final SimpleStringProperty from;
        private final SimpleStringProperty to;
        private final SimpleStringProperty status;

        private LeaveRequest(String requestId, String employee, String leaveType, String from, String to, String status) {
            this.requestId = new SimpleStringProperty(requestId);
            this.employee = new SimpleStringProperty(employee);
            this.leaveType = new SimpleStringProperty(leaveType);
            this.from = new SimpleStringProperty(from);
            this.to = new SimpleStringProperty(to);
            this.status = new SimpleStringProperty(status);
        }
    }

    private static class PayrollRecord {
        private final SimpleStringProperty period;
        private final SimpleStringProperty employeeCount;
        private final SimpleStringProperty grossPay;
        private final SimpleStringProperty deductions;
        private final SimpleStringProperty netPay;
        private final SimpleStringProperty status;

        private PayrollRecord(String period, String employeeCount, String grossPay, String deductions, String netPay, String status) {
            this.period = new SimpleStringProperty(period);
            this.employeeCount = new SimpleStringProperty(employeeCount);
            this.grossPay = new SimpleStringProperty(grossPay);
            this.deductions = new SimpleStringProperty(deductions);
            this.netPay = new SimpleStringProperty(netPay);
            this.status = new SimpleStringProperty(status);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
