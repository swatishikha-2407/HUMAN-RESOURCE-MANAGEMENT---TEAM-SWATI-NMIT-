package com.dayflow;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Dayflow frontend-only JavaFX application.
 *
 * This project implements the screens and interactions requested in the
 * frontend specification. It uses sample data stored in memory, so it does
 * not require a backend or database. Replace the mock actions with API calls
 * when the backend is ready.
 *
 * Demo accounts:
 *   Admin:    admin@dayflow.com / Admin123
 *   Employee: employee@dayflow.com / Employee123
 *
 * Demo email verification code: 123456
 */
public class DayflowApp extends Application {

    private static final String APP_NAME = "DAYFLOW";
    private static final String PRIMARY = "#2563eb";
    private static final String NAVY = "#172033";
    private static final String BACKGROUND = "#f4f7fb";
    private static final String TEXT = "#172033";
    private static final String MUTED = "#64748b";
    private static final String BORDER = "#e2e8f0";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[A-Za-z]{2,}$"
    );
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    private final Map<String, User> users = new LinkedHashMap<>();
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRecord> attendance = FXCollections.observableArrayList();
    private final ObservableList<LeaveRequest> leaveRequests = FXCollections.observableArrayList();
    private final ObservableList<PayrollRecord> payroll = FXCollections.observableArrayList();
    private final ObservableList<String> notifications = FXCollections.observableArrayList(
            "Monthly payroll for August 2026 has been processed.",
            "Two leave requests are waiting for HR approval.",
            "Remember to complete your emergency contact details.",
            "Company town hall is scheduled for Friday at 4:00 PM."
    );

    private final BorderPane applicationRoot = new BorderPane();
    private final StackPane rootStack = new StackPane();
    private Stage stage;
    private User currentUser;
    private Label globalStatusLabel;
    private boolean employeeCheckedIn;
    private LocalTime employeeCheckInTime;
    private LocalTime employeeCheckOutTime;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        seedMockData();
        rootStack.setAlignment(Pos.CENTER);
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + ";");
        rootStack.getChildren().setAll(buildLoginView());

        Scene scene = new Scene(rootStack, 1360, 820);
        stage.setTitle("Dayflow - Workforce Management");
        stage.setMinWidth(1050);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    private void seedMockData() {
        User admin = new User("HR-0001", "admin@dayflow.com", "Admin", "Priya Menon", "Admin123");
        admin.verified = true;
        User employee = new User("EMP-1024", "employee@dayflow.com", "Employee", "Ananya Sharma", "Employee123");
        employee.verified = true;
        employee.phone = "+91 98765 43210";
        employee.address = "Bangalore, Karnataka";
        users.put(admin.email, admin);
        users.put(employee.email, employee);

        employees.addAll(
                new Employee("EMP-1024", "Ananya Sharma", "Engineering", "Software Developer", "Active", "12 Jun 2025"),
                new Employee("EMP-1025", "Rahul Verma", "Marketing", "Marketing Executive", "Active", "03 Feb 2024"),
                new Employee("EMP-1026", "Priya Nair", "Human Resources", "HR Associate", "Active", "21 Aug 2023"),
                new Employee("EMP-1027", "Karan Mehta", "Finance", "Accountant", "On Leave", "15 Nov 2022"),
                new Employee("EMP-1028", "Meera Iyer", "Design", "Product Designer", "Active", "09 Jan 2026")
        );

        attendance.addAll(
                new AttendanceRecord("22 Aug 2026", "Ananya Sharma", "09:05 AM", "--", "Present"),
                new AttendanceRecord("22 Aug 2026", "Rahul Verma", "09:20 AM", "05:00 PM", "Present"),
                new AttendanceRecord("22 Aug 2026", "Priya Nair", "08:55 AM", "05:15 PM", "Present"),
                new AttendanceRecord("22 Aug 2026", "Karan Mehta", "--", "--", "On Leave"),
                new AttendanceRecord("22 Aug 2026", "Meera Iyer", "09:30 AM", "--", "Present")
        );

        leaveRequests.addAll(
                new LeaveRequest("LR-501", "Ananya Sharma", "Paid Leave", "22 Aug 2026", "24 Aug 2026", "Personal work", "Pending"),
                new LeaveRequest("LR-502", "Rahul Verma", "Sick Leave", "10 Aug 2026", "10 Aug 2026", "Medical appointment", "Approved"),
                new LeaveRequest("LR-503", "Karan Mehta", "Unpaid Leave", "15 Jul 2026", "16 Jul 2026", "Family emergency", "Rejected")
        );

        payroll.addAll(
                new PayrollRecord("Ananya Sharma", 40000, 5000, 2000),
                new PayrollRecord("Rahul Verma", 35000, 4000, 1500),
                new PayrollRecord("Priya Nair", 45000, 6000, 2500),
                new PayrollRecord("Karan Mehta", 38000, 4500, 1800),
                new PayrollRecord("Meera Iyer", 42000, 5500, 2200)
        );
    }

    // ---------------------------------------------------------------------
    // AUTHENTICATION
    // ---------------------------------------------------------------------

    private VBox buildLoginView() {
        Label brand = brandLabel();
        Label tagline = new Label("Every workday, perfectly aligned");
        tagline.setTextFill(Color.web(MUTED));

        Label title = authTitle("Welcome back");
        Label subtitle = new Label("Sign in to continue to your Dayflow workspace.");
        subtitle.setTextFill(Color.web(MUTED));
        subtitle.setWrapText(true);

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Label status = authStatusLabel();

        Button signInButton = primaryButton("Sign In");
        signInButton.setMaxWidth(Double.MAX_VALUE);
        signInButton.setDefaultButton(true);
        signInButton.setOnAction(event -> {
            String email = normalizeEmail(emailField.getText());
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                showError(status, "Please enter both email and password.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showError(status, "Please enter a valid email address.");
                return;
            }

            setButtonLoading(signInButton, "Signing in...");
            PauseTransition pause = new PauseTransition(Duration.millis(450));
            pause.setOnFinished(done -> {
                User user = users.get(email);
                if (user == null || !user.password.equals(password)) {
                    setButtonReady(signInButton, "Sign In");
                    showError(status, "Incorrect email or password.");
                    return;
                }
                if (!user.verified) {
                    setButtonReady(signInButton, "Sign In");
                    switchTo(buildVerificationView(user.email));
                    return;
                }
                currentUser = user;
                showAppPage("Dashboard", user.isAdmin() ? buildAdminDashboard() : buildEmployeeDashboard());
            });
            pause.play();
        });

        Button demoAdmin = linkButton("Use demo admin account");
        demoAdmin.setOnAction(event -> {
            emailField.setText("admin@dayflow.com");
            passwordField.setText("Admin123");
            showSuccess(status, "Demo admin credentials filled in.");
        });
        Button demoEmployee = linkButton("Use demo employee account");
        demoEmployee.setOnAction(event -> {
            emailField.setText("employee@dayflow.com");
            passwordField.setText("Employee123");
            showSuccess(status, "Demo employee credentials filled in.");
        });

        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
        forgotPassword.setOnAction(event -> showError(status, "Password reset will be connected to the backend later."));
        Hyperlink signUp = new Hyperlink("Do not have an account? Sign up");
        signUp.setOnAction(event -> switchTo(buildSignUpView()));

        VBox form = authCard(brand, tagline, title, subtitle, fieldLabel("Email", emailField),
                fieldLabel("Password", passwordField), signInButton, forgotPassword, status,
                new Separator(), demoAdmin, demoEmployee, signUp);
        return centered(form);
    }

    private VBox buildSignUpView() {
        Label brand = brandLabel();
        Label tagline = new Label("Create your Dayflow account");
        tagline.setTextFill(Color.web(MUTED));
        Label title = authTitle("Create account");
        Label subtitle = new Label("Register as an Employee or HR administrator.");
        subtitle.setTextFill(Color.web(MUTED));
        subtitle.setWrapText(true);

        TextField employeeId = new TextField();
        employeeId.setPromptText("Employee ID");
        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 6 characters");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("Employee", "Admin / HR"));
        roleBox.getSelectionModel().selectFirst();
        Label status = authStatusLabel();

        Button createButton = primaryButton("Create Account");
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.setDefaultButton(true);
        createButton.setOnAction(event -> {
            String id = employeeId.getText().trim();
            String email = normalizeEmail(emailField.getText());
            String password = passwordField.getText();
            String confirmation = confirmField.getText();
            if (id.isEmpty() || email.isEmpty() || password.isEmpty() || confirmation.isEmpty()) {
                showError(status, "All fields are required.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showError(status, "Please enter a valid email address.");
                return;
            }
            if (password.length() < 6) {
                showError(status, "Password must contain at least 6 characters.");
                return;
            }
            if (!password.equals(confirmation)) {
                showError(status, "Passwords do not match.");
                return;
            }
            if (users.containsKey(email)) {
                showError(status, "An account with this email already exists.");
                return;
            }
            String role = roleBox.getValue().startsWith("Admin") ? "Admin" : "Employee";
            User user = new User(id, email, role, email.substring(0, email.indexOf('@')), password);
            users.put(email, user);
            switchTo(buildVerificationView(email));
        });

        Hyperlink login = new Hyperlink("Already have an account? Sign in");
        login.setOnAction(event -> switchTo(buildLoginView()));

        VBox form = authCard(brand, tagline, title, subtitle,
                fieldLabel("Employee ID", employeeId),
                fieldLabel("Email", emailField),
                fieldLabel("Password", passwordField),
                fieldLabel("Confirm Password", confirmField),
                fieldLabel("Role", roleBox), createButton, status, login);
        return centered(form);
    }

    private VBox buildVerificationView(String email) {
        Label brand = brandLabel();
        Label title = authTitle("Verify your email");
        Label info = new Label("A verification message would be sent to:\n" + email
                + "\n\nFor this frontend demo, use code 123456.");
        info.setTextFill(Color.web(MUTED));
        info.setWrapText(true);
        TextField codeField = new TextField();
        codeField.setPromptText("6-digit verification code");
        Label status = authStatusLabel();

        Button verifyButton = primaryButton("Verify Email");
        verifyButton.setMaxWidth(Double.MAX_VALUE);
        verifyButton.setOnAction(event -> {
            if (!"123456".equals(codeField.getText().trim())) {
                showError(status, "Invalid verification code. Use 123456 for this demo.");
                return;
            }
            User user = users.get(email);
            if (user != null) {
                user.verified = true;
            }
            switchTo(buildLoginView());
        });

        Button resendButton = secondaryButton("Resend Code");
        resendButton.setMaxWidth(Double.MAX_VALUE);
        resendButton.setOnAction(event -> showSuccess(status, "Verification code resent. Demo code: 123456."));
        Hyperlink back = new Hyperlink("Back to sign in");
        back.setOnAction(event -> switchTo(buildLoginView()));
        return centered(authCard(brand, title, info, fieldLabel("Verification Code", codeField),
                verifyButton, resendButton, status, back));
    }

    private void switchTo(Node view) {
        rootStack.getChildren().setAll(view);
    }

    private VBox authCard(Node... nodes) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(410);
        card.setPadding(new Insets(30));
        card.setStyle(cardStyle());
        card.getChildren().addAll(nodes);
        return card;
    }

    private VBox centered(Node node) {
        VBox wrapper = new VBox(node);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(25));
        return wrapper;
    }

    private Label brandLabel() {
        Label label = new Label(APP_NAME);
        label.setTextFill(Color.web(PRIMARY));
        label.setFont(Font.font("System", FontWeight.BOLD, 28));
        return label;
    }

    private Label authTitle(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(TEXT));
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        return label;
    }

    private Label authStatusLabel() {
        Label label = new Label();
        label.setWrapText(true);
        label.setMaxWidth(350);
        return label;
    }

    // ---------------------------------------------------------------------
    // COMMON APPLICATION SHELL
    // ---------------------------------------------------------------------

    private void showAppPage(String title, Node content) {
        applicationRoot.setLeft(buildSidebar(currentUser != null && currentUser.isAdmin()));
        applicationRoot.setTop(buildHeader(title));
        applicationRoot.setCenter(content);
        applicationRoot.setStyle("-fx-background-color: " + BACKGROUND + ";");
        rootStack.getChildren().setAll(applicationRoot);
    }

    private HBox buildHeader(String title) {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label pageTitle = new Label(title);
        pageTitle.setTextFill(Color.web(TEXT));
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        globalStatusLabel = new Label("Ready");
        globalStatusLabel.setTextFill(Color.web(MUTED));
        globalStatusLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        Label online = new Label("● Online");
        online.setTextFill(Color.web("#16a34a"));
        online.setFont(Font.font("System", FontWeight.BOLD, 11));
        Label name = new Label(currentUser == null ? "Guest" : currentUser.name);
        name.setTextFill(Color.web(TEXT));
        name.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label role = new Label(currentUser == null ? "" : currentUser.role);
        role.setTextFill(Color.web(MUTED));
        role.setFont(Font.font("System", FontWeight.NORMAL, 10));
        VBox account = new VBox(2, name, role);
        account.setAlignment(Pos.CENTER_RIGHT);
        Label avatar = avatarLabel(currentUser == null ? "?" : initials(currentUser.name), 38);
        header.getChildren().addAll(pageTitle, spacer, globalStatusLabel, online, new Separator(), account, avatar);
        return header;
    }

    private VBox buildSidebar(boolean admin) {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(245);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        Label brand = new Label(APP_NAME);
        brand.setTextFill(Color.WHITE);
        brand.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label sub = new Label(admin ? "ADMIN WORKSPACE" : "EMPLOYEE WORKSPACE");
        sub.setTextFill(Color.web("#94a8c7"));
        sub.setFont(Font.font("System", FontWeight.BOLD, 10));
        VBox brandBox = new VBox(4, brand, sub);
        brandBox.setPadding(new Insets(0, 0, 28, 10));

        Label menu = new Label("NAVIGATION");
        menu.setTextFill(Color.web("#7890b4"));
        menu.setFont(Font.font("System", FontWeight.BOLD, 10));
        menu.setPadding(new Insets(0, 0, 5, 10));
        sidebar.getChildren().addAll(brandBox, menu);

        if (admin) {
            sidebar.getChildren().addAll(
                    navButton("Dashboard", "▦", () -> showAppPage("Admin Dashboard", buildAdminDashboard())),
                    navButton("Employee Management", "♙", () -> showAppPage("Employee Management", buildEmployeeManagement())),
                    navButton("Admin Attendance", "◷", () -> showAppPage("Admin Attendance", buildAdminAttendance())),
                    navButton("Leave Approval", "✓", () -> showAppPage("Leave Approval", buildLeaveApproval())),
                    navButton("Payroll", "₹", () -> showAppPage("Payroll", buildPayroll())),
                    navButton("Analytics / Reports", "▥", () -> showAppPage("Analytics / Reports", buildAnalytics())),
                    navButton("Notifications", "•", () -> showAppPage("Notifications", buildNotifications()))
            );
        } else {
            sidebar.getChildren().addAll(
                    navButton("Dashboard", "▦", () -> showAppPage("Dashboard", buildEmployeeDashboard())),
                    navButton("Profile", "♙", () -> showAppPage("My Profile", buildEmployeeProfile())),
                    navButton("Attendance", "◷", () -> showAppPage("My Attendance", buildEmployeeAttendance())),
                    navButton("Leave Requests", "✓", () -> showAppPage("Leave Requests", buildEmployeeLeave())),
                    navButton("Salary", "₹", () -> showAppPage("My Salary", buildEmployeeSalary())),
                    navButton("Notifications", "•", () -> showAppPage("Notifications", buildNotifications()))
            );
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        sidebar.getChildren().add(navButton("Logout", "↪", this::logout));
        return sidebar;
    }

    private Button navButton(String text, String icon, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setTextFill(Color.web("#9eb2d0"));
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        iconLabel.setMinWidth(25);
        Label textLabel = new Label(text);
        textLabel.setTextFill(Color.web("#d9e3f1"));
        textLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        HBox graphic = new HBox(9, iconLabel, textLabel);
        graphic.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(graphic);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(11, 12, 11, 12));
        setNavStyle(button, false);
        button.setOnMouseEntered(event -> setNavStyle(button, true));
        button.setOnMouseExited(event -> setNavStyle(button, false));
        button.setOnAction(event -> action.run());
        return button;
    }

    private void setNavStyle(Button button, boolean hover) {
        button.setStyle(hover
                ? "-fx-background-color: #263653; -fx-background-radius: 7;"
                : "-fx-background-color: transparent; -fx-background-radius: 7;");
    }

    private void logout() {
        currentUser = null;
        showSuccessMessage("You have been logged out.");
        rootStack.getChildren().setAll(buildLoginView());
    }

    // ---------------------------------------------------------------------
    // EMPLOYEE PAGES
    // ---------------------------------------------------------------------

    private ScrollPane buildEmployeeDashboard() {
        VBox page = pageContainer();
        page.getChildren().addAll(
                pageIntro("Good morning, " + firstName(currentUser.name), "Here is your workday overview."),
                employeeMetricCards(),
                sectionHeader("Quick access", "Open a workspace section"),
                employeeQuickActions(),
                sectionHeader("Recent activity", "Latest updates from Dayflow"),
                notificationList(3)
        );
        return scrollable(page);
    }

    private HBox employeeMetricCards() {
        long present = attendance.stream().filter(item -> item.employee.get().equals(currentUser.name)
                && "Present".equals(item.status.get())).count();
        long pending = leaveRequests.stream().filter(item -> item.employee.get().equals(currentUser.name)
                && "Pending".equals(item.status.get())).count();
        PayrollRecord ownPayroll = payroll.stream().filter(item -> item.employee.get().equals(currentUser.name)).findFirst().orElse(null);
        String net = ownPayroll == null ? "₹--" : formatCurrency(ownPayroll.netPay());
        return new HBox(16,
                metricCard("Attendance", present > 0 ? "Present" : "Not marked", "Today", "#16a34a"),
                metricCard("Leave Requests", String.valueOf(pending), "Pending approval", "#f59e0b"),
                metricCard("Net Salary", net, "Current month", "#7c3aed"),
                metricCard("Notifications", String.valueOf(notifications.size()), "Unread updates", PRIMARY)
        );
    }

    private HBox employeeQuickActions() {
        HBox row = new HBox(16,
                quickActionCard("Profile", "View and edit contact details", "♙", () -> showAppPage("My Profile", buildEmployeeProfile())),
                quickActionCard("Attendance", "Check in or check out", "◷", () -> showAppPage("My Attendance", buildEmployeeAttendance())),
                quickActionCard("Leave Requests", "Apply for time off", "✓", () -> showAppPage("Leave Requests", buildEmployeeLeave())),
                quickActionCard("Logout", "End your current session", "↪", this::logout)
        );
        return row;
    }

    private VBox buildEmployeeProfile() {
        VBox page = pageContainer();
        Label picture = avatarLabel(initials(currentUser.name), 88);
        Label pictureName = new Label(currentUser.pictureFile == null ? "No profile picture selected" : currentUser.pictureFile);
        pictureName.setTextFill(Color.web(MUTED));
        Button upload = secondaryButton("Upload Profile Picture");
        upload.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose profile picture");
            chooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                currentUser.pictureFile = file.getName();
                pictureName.setText(file.getName());
                showSuccessMessage("Profile picture selected. Save your profile to persist it later.");
            }
        });
        VBox pictureBox = new VBox(8, picture, pictureName, upload);
        pictureBox.setAlignment(Pos.CENTER);

        TextField phone = new TextField(currentUser.phone);
        TextField address = new TextField(currentUser.address);
        Button save = primaryButton("Save Changes");
        save.setOnAction(event -> {
            if (phone.getText().trim().isEmpty() || address.getText().trim().isEmpty()) {
                showErrorMessage("Phone and address cannot be empty.");
                return;
            }
            currentUser.phone = phone.getText().trim();
            currentUser.address = address.getText().trim();
            showSuccessMessage("Profile changes saved in this demo session.");
        });

        VBox contactForm = whiteCard(
                sectionHeader("Editable contact details", "Only contact information can be changed"),
                fieldLabel("Phone", phone), fieldLabel("Address", address), save
        );
        VBox jobCard = whiteCard(
                sectionHeader("Job details", "Read-only employment information"),
                infoRow("Employee ID", currentUser.employeeId),
                infoRow("Email", currentUser.email),
                infoRow("Department", employeeDepartment(currentUser.name)),
                infoRow("Designation", employeeDesignation(currentUser.name)),
                infoRow("Joining date", employeeJoiningDate(currentUser.name))
        );
        VBox salaryCard = whiteCard(
                sectionHeader("Salary structure", "Read-only payroll summary"),
                infoRow("Basic salary", "₹40,000"),
                infoRow("Allowances", "₹5,000"),
                infoRow("Deductions", "₹2,000"),
                infoRow("Net salary", "₹43,000")
        );
        HBox top = new HBox(18, pictureBox, contactForm);
        HBox.setHgrow(contactForm, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("My Profile", "View your personal, job, salary, and document information."), top, jobCard, salaryCard);
        return page;
    }

    private VBox buildEmployeeAttendance() {
        VBox page = pageContainer();
        Label currentStatus = new Label(employeeCheckedIn ? "PRESENT" : "NOT CHECKED IN");
        currentStatus.setFont(Font.font("System", FontWeight.BOLD, 18));
        currentStatus.setTextFill(Color.web(employeeCheckedIn ? "#16a34a" : MUTED));
        Label checkIn = new Label(employeeCheckInTime == null ? "--" : employeeCheckInTime.format(TIME_FORMAT));
        Label checkOut = new Label(employeeCheckOutTime == null ? "--" : employeeCheckOutTime.format(TIME_FORMAT));
        Button action = primaryButton(employeeCheckedIn && employeeCheckOutTime == null ? "Check Out" : "Check In");
        action.setOnAction(event -> {
            if (!employeeCheckedIn) {
                employeeCheckedIn = true;
                employeeCheckInTime = LocalTime.now();
                currentStatus.setText("PRESENT");
                currentStatus.setTextFill(Color.web("#16a34a"));
                checkIn.setText(employeeCheckInTime.format(TIME_FORMAT));
                action.setText("Check Out");
                showSuccessMessage("Check-in successful.");
            } else if (employeeCheckOutTime == null) {
                employeeCheckOutTime = LocalTime.now();
                checkOut.setText(employeeCheckOutTime.format(TIME_FORMAT));
                action.setDisable(true);
                showSuccessMessage("Check-out successful.");
            } else {
                showErrorMessage("Your attendance is already complete for today.");
            }
        });
        VBox today = whiteCard(
                sectionHeader("Today's status", "Attendance action"), currentStatus,
                infoRowNode("Check-in", checkIn), infoRowNode("Check-out", checkOut), action
        );

        TableView<AttendanceRecord> table = new TableView<>(employeeAttendanceItems());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Date", item -> item.date),
                textColumn("Check-in", item -> item.checkIn),
                textColumn("Check-out", item -> item.checkOut),
                textColumn("Status", item -> item.status)
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("My Attendance", "Review daily and weekly attendance records."), today,
                sectionHeader("This week", "Your attendance history"), table);
        return page;
    }

    private VBox buildEmployeeLeave() {
        VBox page = pageContainer();
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Paid Leave", "Sick Leave", "Unpaid Leave"));
        type.getSelectionModel().selectFirst();
        DatePicker from = new DatePicker(LocalDate.now());
        DatePicker to = new DatePicker(LocalDate.now());
        TextArea reason = new TextArea();
        reason.setPromptText("Explain the reason for your leave request...");
        reason.setPrefRowCount(3);
        Label formStatus = new Label();
        formStatus.setWrapText(true);

        Button submit = primaryButton("Submit Request");
        submit.setOnAction(event -> {
            if (from.getValue() == null || to.getValue() == null || reason.getText().trim().isEmpty()) {
                showError(formStatus, "Leave type, dates, and remarks are required.");
                return;
            }
            if (to.getValue().isBefore(from.getValue())) {
                showError(formStatus, "End date cannot be before start date.");
                return;
            }
            leaveRequests.add(new LeaveRequest("LR-" + (500 + leaveRequests.size() + 1), currentUser.name,
                    type.getValue(), from.getValue().toString(), to.getValue().toString(), reason.getText().trim(), "Pending"));
            showSuccess(formStatus, "Leave request submitted successfully.");
            reason.clear();
        });

        VBox form = whiteCard(sectionHeader("Leave request", "Submit a paid, sick, or unpaid leave request"),
                fieldLabel("Leave Type", type), fieldLabel("Start Date", from), fieldLabel("End Date", to),
                fieldLabel("Reason / Remarks", reason), submit, formStatus);
        TableView<LeaveRequest> table = new TableView<>(employeeLeaveItems());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Type", item -> item.leaveType),
                textColumn("From", item -> item.from),
                textColumn("To", item -> item.to),
                textColumn("Status", item -> item.status)
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("Leave Requests", "Apply for leave and track your previous requests."), form,
                sectionHeader("My leave requests", "Current and historical requests"), table);
        return page;
    }

    private VBox buildEmployeeSalary() {
        PayrollRecord record = payroll.stream().filter(item -> item.employee.get().equals(currentUser.name)).findFirst().orElse(null);
        VBox page = pageContainer();
        page.getChildren().add(pageIntro("My Salary", "Your payroll information is read-only."));
        page.getChildren().add(whiteCard(sectionHeader("Salary structure", "August 2026 payroll"),
                infoRow("Basic salary", record == null ? "₹--" : formatCurrency(record.basic)),
                infoRow("Allowances", record == null ? "₹--" : formatCurrency(record.allowances)),
                infoRow("Deductions", record == null ? "₹--" : formatCurrency(record.deductions)),
                infoRow("Net salary", record == null ? "₹--" : formatCurrency(record.netPay()))
        ));
        Button slip = secondaryButton("View Salary Slip");
        slip.setOnAction(event -> showSuccessMessage("Salary slip preview is ready in this frontend demo."));
        page.getChildren().add(slip);
        return page;
    }

    // ---------------------------------------------------------------------
    // ADMIN / HR PAGES
    // ---------------------------------------------------------------------

    private ScrollPane buildAdminDashboard() {
        VBox page = pageContainer();
        long active = employees.stream().filter(item -> "Active".equals(item.status.get())).count();
        long present = attendance.stream().filter(item -> "Present".equals(item.status.get())).count();
        long onLeave = attendance.stream().filter(item -> "On Leave".equals(item.status.get())).count();
        long pending = leaveRequests.stream().filter(item -> "Pending".equals(item.status.get())).count();
        page.getChildren().addAll(
                pageIntro("Admin Dashboard", "Monitor people operations from one place."),
                new HBox(16,
                        metricCard("Total Employees", String.valueOf(employees.size()), active + " active", PRIMARY),
                        metricCard("Present Today", String.valueOf(present), "Of " + employees.size() + " employees", "#16a34a"),
                        metricCard("On Leave", String.valueOf(onLeave), "Today's records", "#f59e0b"),
                        metricCard("Pending Requests", String.valueOf(pending), "Needs approval", "#dc2626")
                ),
                sectionHeader("Operations overview", "Attendance, approvals, and payroll"),
                adminOverviewPanels(),
                sectionHeader("Shortcuts", "Open frequently used admin pages"),
                new HBox(16,
                        quickActionCard("Employees", "Search and manage employee records", "♙", () -> showAppPage("Employee Management", buildEmployeeManagement())),
                        quickActionCard("Leave Approval", "Review pending requests", "✓", () -> showAppPage("Leave Approval", buildLeaveApproval())),
                        quickActionCard("Reports", "Review workforce analytics", "▥", () -> showAppPage("Analytics / Reports", buildAnalytics()))
                )
        );
        return scrollable(page);
    }

    private HBox adminOverviewPanels() {
        VBox attendancePanel = whiteCard(sectionHeader("Attendance overview", "Current daily distribution"),
                progressRow("Present", 3, employees.size(), "#16a34a"),
                progressRow("On Leave", 1, employees.size(), "#f59e0b"),
                progressRow("Absent", 1, employees.size(), "#dc2626")
        );
        VBox leavePanel = whiteCard(sectionHeader("Pending leave requests", "Latest requests awaiting review"));
        leaveRequests.stream().filter(item -> "Pending".equals(item.status.get())).limit(3)
                .forEach(item -> leavePanel.getChildren().add(activityRow(item.employee.get(), item.leaveType.get(), item.status.get())));
        HBox row = new HBox(16, attendancePanel, leavePanel);
        HBox.setHgrow(attendancePanel, Priority.ALWAYS);
        HBox.setHgrow(leavePanel, Priority.ALWAYS);
        return row;
    }

    private VBox buildEmployeeManagement() {
        VBox page = pageContainer();
        TextField search = new TextField();
        search.setPromptText("Search employee, ID, department...");
        search.setPrefWidth(350);
        Button add = primaryButton("+ Add Employee");
        add.setOnAction(event -> showSuccessMessage("Employee form opened. Connect this action to your employee API."));
        Button export = secondaryButton("Export CSV");
        export.setOnAction(event -> showSuccessMessage("Employee data prepared for export."));
        HBox toolbar = new HBox(10, search, add, export);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<Employee> table = new TableView<>(employees);
        table.setPlaceholder(new Label("No employees found"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("ID", item -> item.id),
                textColumn("Name", item -> item.name),
                textColumn("Department", item -> item.department),
                textColumn("Designation", item -> item.designation),
                textColumn("Status", item -> item.status),
                textColumn("Joining Date", item -> item.joiningDate)
        );
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
            table.setItems(query.isEmpty() ? employees : FXCollections.observableArrayList(employees.stream()
                    .filter(item -> item.name.get().toLowerCase(Locale.ROOT).contains(query)
                            || item.id.get().toLowerCase(Locale.ROOT).contains(query)
                            || item.department.get().toLowerCase(Locale.ROOT).contains(query))
                    .toList()));
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        Button view = secondaryButton("View Selected Employee");
        view.setOnAction(event -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showErrorMessage("Select an employee first.");
                return;
            }
            showSuccessMessage("Viewing profile for " + selected.name.get() + ".");
        });
        page.getChildren().addAll(pageIntro("Employee Management", "Search, review, and maintain employee records."), toolbar, table, view);
        return page;
    }

    private VBox buildAdminAttendance() {
        VBox page = pageContainer();
        TextField search = new TextField();
        search.setPromptText("Search employee...");
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("All statuses", "Present", "On Leave", "Absent"));
        status.getSelectionModel().selectFirst();
        Button report = secondaryButton("Download Report");
        report.setOnAction(event -> showSuccessMessage("Attendance report prepared for download."));
        HBox toolbar = new HBox(10, search, status, report);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<AttendanceRecord> table = new TableView<>(attendance);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Date", item -> item.date), textColumn("Employee", item -> item.employee),
                textColumn("Check-in", item -> item.checkIn), textColumn("Check-out", item -> item.checkOut),
                textColumn("Status", item -> item.status)
        );
        Runnable filter = () -> {
            String name = search.getText() == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
            String selectedStatus = status.getValue();
            table.setItems(FXCollections.observableArrayList(attendance.stream().filter(item ->
                    (name.isEmpty() || item.employee.get().toLowerCase(Locale.ROOT).contains(name))
                            && (selectedStatus == null || "All statuses".equals(selectedStatus)
                            || selectedStatus.equals(item.status.get()))).toList()));
        };
        search.textProperty().addListener((observable, oldValue, newValue) -> filter.run());
        status.valueProperty().addListener((observable, oldValue, newValue) -> filter.run());
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("Admin Attendance", "View attendance records for the entire organization."), toolbar, table);
        return page;
    }

    private VBox buildLeaveApproval() {
        VBox page = pageContainer();
        TableView<LeaveRequest> table = new TableView<>(leaveRequests);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Request ID", item -> item.requestId), textColumn("Employee", item -> item.employee),
                textColumn("Type", item -> item.leaveType), textColumn("From", item -> item.from),
                textColumn("To", item -> item.to), textColumn("Status", item -> item.status)
        );
        TextArea comment = new TextArea();
        comment.setPromptText("Optional HR comment...");
        comment.setPrefRowCount(2);
        Button approve = primaryButton("Approve");
        Button reject = dangerButton("Reject");
        approve.setOnAction(event -> updateLeaveStatus(table, comment, "Approved"));
        reject.setOnAction(event -> updateLeaveStatus(table, comment, "Rejected"));
        HBox actions = new HBox(10, approve, reject);
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("Leave Approval", "Approve or reject employee leave requests."), table,
                fieldLabel("Comment", comment), actions);
        return page;
    }

    private void updateLeaveStatus(TableView<LeaveRequest> table, TextArea comment, String status) {
        LeaveRequest selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorMessage("Select a leave request first.");
            return;
        }
        selected.status.set(status);
        selected.comment.set(comment.getText().trim());
        table.refresh();
        showSuccessMessage(selected.requestId.get() + " marked as " + status + ".");
    }

    private VBox buildPayroll() {
        VBox page = pageContainer();
        TableView<PayrollRecord> table = new TableView<>(payroll);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                textColumn("Employee", item -> item.employee),
                textColumn("Basic", item -> new SimpleStringProperty(formatCurrency(item.basic))),
                textColumn("Allowance", item -> new SimpleStringProperty(formatCurrency(item.allowances))),
                textColumn("Deduction", item -> new SimpleStringProperty(formatCurrency(item.deductions))),
                textColumn("Net", item -> new SimpleStringProperty(formatCurrency(item.netPay()))),
                textColumn("Status", item -> new SimpleStringProperty("Ready"))
        );

        TextField basic = new TextField();
        TextField allowance = new TextField();
        TextField deduction = new TextField();
        Label selectedLabel = new Label("Select an employee to edit salary values.");
        selectedLabel.setTextFill(Color.web(MUTED));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null) {
                selectedLabel.setText("Editing salary for " + selected.employee.get());
                basic.setText(String.valueOf(selected.basic));
                allowance.setText(String.valueOf(selected.allowances));
                deduction.setText(String.valueOf(selected.deductions));
            }
        });
        Button save = primaryButton("Save Salary Changes");
        save.setOnAction(event -> {
            PayrollRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showErrorMessage("Select an employee first.");
                return;
            }
            try {
                selected.basic = parseAmount(basic.getText());
                selected.allowances = parseAmount(allowance.getText());
                selected.deductions = parseAmount(deduction.getText());
                table.refresh();
                showSuccessMessage("Salary structure updated for " + selected.employee.get() + ".");
            } catch (NumberFormatException exception) {
                showErrorMessage("Enter valid whole-number salary amounts.");
            }
        });
        HBox editor = new HBox(10, fieldLabel("Basic", basic), fieldLabel("Allowance", allowance),
                fieldLabel("Deduction", deduction), save);
        editor.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(table, Priority.ALWAYS);
        page.getChildren().addAll(pageIntro("Payroll", "View and update employee salary structures."), table, selectedLabel, editor);
        return page;
    }

    private VBox buildAnalytics() {
        VBox page = pageContainer();
        page.getChildren().addAll(pageIntro("Analytics / Reports", "Understand attendance, leave, and workforce trends."),
                new HBox(16,
                        metricCard("Total Employees", String.valueOf(employees.size()), "Current headcount", PRIMARY),
                        metricCard("Present", "" + attendance.stream().filter(item -> "Present".equals(item.status.get())).count(), "Today", "#16a34a"),
                        metricCard("Absent", "1", "Today", "#dc2626"),
                        metricCard("On Leave", "1", "Today", "#f59e0b")
                ));
        GridPane reports = new GridPane();
        reports.setHgap(16);
        reports.setVgap(16);
        reports.add(reportCard("Attendance rate", "94.6%", "▲ 2.4% from last month", "#16a34a"), 0, 0);
        reports.add(reportCard("Leave utilization", "18.2%", "Within annual policy", PRIMARY), 1, 0);
        reports.add(reportCard("Average tenure", "2.8 years", "Across active employees", "#7c3aed"), 0, 1);
        reports.add(reportCard("Open positions", "12", "4 new this month", "#f59e0b"), 1, 1);
        page.getChildren().add(reports);
        VBox department = whiteCard(sectionHeader("Headcount by department", "Current employee distribution"),
                progressRow("Engineering", 1, employees.size(), PRIMARY),
                progressRow("Marketing", 1, employees.size(), "#7c3aed"),
                progressRow("Human Resources", 1, employees.size(), "#16a34a"),
                progressRow("Finance", 1, employees.size(), "#f59e0b"),
                progressRow("Design", 1, employees.size(), "#db2777")
        );
        Button generate = primaryButton("Generate Monthly Report");
        generate.setOnAction(event -> showSuccessMessage("Monthly analytics report generated successfully."));
        page.getChildren().addAll(department, generate);
        return page;
    }

    private VBox reportCard(String title, String value, String detail, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(MUTED));
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT));
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

    private VBox buildNotifications() {
        VBox page = pageContainer();
        page.getChildren().addAll(pageIntro("Notifications", "Important updates and alerts from Dayflow."), notificationList(notifications.size()));
        return page;
    }

    // ---------------------------------------------------------------------
    // REUSABLE UI COMPONENTS
    // ---------------------------------------------------------------------

    private VBox pageContainer() {
        VBox page = new VBox(18);
        page.setPadding(new Insets(26));
        page.setFillWidth(true);
        return page;
    }

    private VBox pageIntro(String title, String subtitle) {
        Label heading = new Label(title);
        heading.setTextFill(Color.web(TEXT));
        heading.setFont(Font.font("System", FontWeight.BOLD, 23));
        Label sub = new Label(subtitle);
        sub.setTextFill(Color.web(MUTED));
        sub.setFont(Font.font("System", FontWeight.NORMAL, 13));
        return new VBox(5, heading, sub);
    }

    private HBox sectionHeader(String title, String subtitle) {
        Label heading = new Label(title);
        heading.setTextFill(Color.web(TEXT));
        heading.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label sub = new Label(subtitle);
        sub.setTextFill(Color.web(MUTED));
        sub.setFont(Font.font("System", FontWeight.NORMAL, 11));
        return new HBox(new VBox(3, heading, sub));
    }

    private VBox metricCard(String title, String value, String detail, String accent) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(MUTED));
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT));
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label detailLabel = new Label(detail);
        detailLabel.setTextFill(Color.web(accent));
        detailLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        VBox card = new VBox(9, titleLabel, valueLabel, detailLabel);
        card.setPadding(new Insets(18));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 10; -fx-border-width: 1 1 1 4;"
                + "-fx-effect: dropshadow(gaussian, rgba(23,32,51,0.06), 8, 0, 0, 2);");
        return card;
    }

    private VBox quickActionCard(String title, String subtitle, String icon, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setTextFill(Color.web(PRIMARY));
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label heading = new Label(title);
        heading.setTextFill(Color.web(TEXT));
        heading.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label description = new Label(subtitle);
        description.setTextFill(Color.web(MUTED));
        description.setWrapText(true);
        Button open = secondaryButton("Open");
        open.setOnAction(event -> action.run());
        VBox card = new VBox(9, iconLabel, heading, description, open);
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setStyle(cardStyle());
        return card;
    }

    private VBox whiteCard(Node... nodes) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle(cardStyle());
        box.getChildren().addAll(nodes);
        return box;
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(23,32,51,0.06), 8, 0, 0, 2);";
    }

    private HBox fieldLabel(String labelText, Node input) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web(MUTED));
        label.setFont(Font.font("System", FontWeight.BOLD, 11));
        VBox group = new VBox(5, label, input);
        if (input instanceof TextArea) {
            VBox.setVgrow(input, Priority.NEVER);
        }
        return new HBox(group);
    }

    private HBox infoRowNode(String labelText, Node valueNode) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web(MUTED));
        label.setPrefWidth(150);
        HBox row = new HBox(12, label, valueNode);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        return row;
    }

    private HBox infoRow(String labelText, String value) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web(MUTED));
        label.setPrefWidth(150);
        Label data = new Label(value);
        data.setTextFill(Color.web(TEXT));
        data.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox row = new HBox(12, label, data);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        return row;
    }

    private HBox progressRow(String labelText, int value, int total, String color) {
        Label name = new Label(labelText);
        name.setTextFill(Color.web(TEXT));
        name.setFont(Font.font("System", FontWeight.BOLD, 12));
        Region track = new Region();
        track.setPrefHeight(8);
        track.setStyle("-fx-background-color: #e9eef5; -fx-background-radius: 4;");
        Region fill = new Region();
        fill.setPrefHeight(8);
        fill.setPrefWidth(Math.max(18, 260.0 * value / Math.max(1, total)));
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        HBox.setHgrow(bar, Priority.ALWAYS);
        Label count = new Label(String.valueOf(value));
        count.setTextFill(Color.web(MUTED));
        HBox row = new HBox(12, name, bar, count);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox activityRow(String person, String detail, String status) {
        Label name = new Label(person);
        name.setTextFill(Color.web(TEXT));
        name.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label type = new Label(detail);
        type.setTextFill(Color.web(MUTED));
        type.setFont(Font.font("System", FontWeight.NORMAL, 11));
        VBox details = new VBox(3, name, type);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(10, details, spacer, statusBadge(status));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 0, 7, 0));
        return row;
    }

    private VBox notificationList(int limit) {
        VBox list = whiteCard();
        notifications.stream().limit(limit).forEach(item -> {
            Label dot = new Label("●");
            dot.setTextFill(Color.web(PRIMARY));
            Label text = new Label(item);
            text.setTextFill(Color.web(TEXT));
            text.setWrapText(true);
            HBox row = new HBox(10, dot, text);
            row.setAlignment(Pos.CENTER_LEFT);
            list.getChildren().add(row);
        });
        return list;
    }

    private Label statusBadge(String status) {
        Label badge = new Label(status);
        badge.setFont(Font.font("System", FontWeight.BOLD, 10));
        if ("Approved".equals(status) || "Present".equals(status) || "Active".equals(status)) {
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

    private Label avatarLabel(String text, double size) {
        Label avatar = new Label(text);
        avatar.setMinSize(size, size);
        avatar.setPrefSize(size, size);
        avatar.setAlignment(Pos.CENTER);
        avatar.setTextFill(Color.WHITE);
        avatar.setFont(Font.font("System", FontWeight.BOLD, Math.max(12, size / 3.1)));
        avatar.setStyle("-fx-background-color: " + PRIMARY + "; -fx-background-radius: " + size / 2 + ";");
        return avatar;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setPadding(new Insets(10, 15, 10, 15));
        button.setStyle("-fx-background-color: " + PRIMARY + "; -fx-background-radius: 6;");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.web(TEXT));
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setPadding(new Insets(9, 14, 9, 14));
        button.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;");
        return button;
    }

    private Button dangerButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setPadding(new Insets(10, 15, 10, 15));
        button.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 6;");
        return button;
    }

    private Button linkButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.web(PRIMARY));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return button;
    }

    private <T> TableColumn<T, String> textColumn(String title, PropertyGetter<T> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> getter.get(data.getValue()));
        return column;
    }

    private ScrollPane scrollable(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private String firstName(String fullName) {
        String value = fullName == null ? "User" : fullName.trim();
        int index = value.indexOf(' ');
        return index > 0 ? value.substring(0, index) : value;
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase(Locale.ROOT);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private void setButtonLoading(Button button, String text) {
        button.setText(text);
        button.setDisable(true);
    }

    private void setButtonReady(Button button, String text) {
        button.setText(text);
        button.setDisable(false);
    }

    private void showError(Label label, String message) {
        label.setTextFill(Color.web("#b91c1c"));
        label.setText(message);
    }

    private void showSuccess(Label label, String message) {
        label.setTextFill(Color.web("#15803d"));
        label.setText(message);
    }

    private void showErrorMessage(String message) {
        if (globalStatusLabel != null) {
            globalStatusLabel.setTextFill(Color.web("#b91c1c"));
            globalStatusLabel.setText("Error: " + message);
        }
    }

    private void showSuccessMessage(String message) {
        if (globalStatusLabel != null) {
            globalStatusLabel.setTextFill(Color.web("#15803d"));
            globalStatusLabel.setText("Success: " + message);
        }
    }

    private long parseAmount(String value) {
        return Long.parseLong(value.trim().replace(",", ""));
    }

    private String formatCurrency(long amount) {
        return String.format(Locale.US, "₹%,d", amount);
    }

    private String employeeDepartment(String name) {
        return employees.stream().filter(item -> item.name.get().equals(name)).map(item -> item.department.get()).findFirst().orElse("Engineering");
    }

    private String employeeDesignation(String name) {
        return employees.stream().filter(item -> item.name.get().equals(name)).map(item -> item.designation.get()).findFirst().orElse("Software Developer");
    }

    private String employeeJoiningDate(String name) {
        return employees.stream().filter(item -> item.name.get().equals(name)).map(item -> item.joiningDate.get()).findFirst().orElse("12 Jun 2025");
    }

    private ObservableList<AttendanceRecord> employeeAttendanceItems() {
        return FXCollections.observableArrayList(attendance.stream()
                .filter(item -> item.employee.get().equals(currentUser.name)).toList());
    }

    private ObservableList<LeaveRequest> employeeLeaveItems() {
        return FXCollections.observableArrayList(leaveRequests.stream()
                .filter(item -> item.employee.get().equals(currentUser.name)).toList());
    }

    @FunctionalInterface
    private interface PropertyGetter<T> {
        ObservableValue<String> get(T value);
    }

    // ---------------------------------------------------------------------
    // DATA MODELS
    // ---------------------------------------------------------------------

    private static class User {
        private final String employeeId;
        private final String email;
        private final String role;
        private final String name;
        private final String password;
        private boolean verified;
        private String phone = "Not provided";
        private String address = "Not provided";
        private String pictureFile;

        private User(String employeeId, String email, String role, String name, String password) {
            this.employeeId = employeeId;
            this.email = email;
            this.role = role;
            this.name = name;
            this.password = password;
        }

        private boolean isAdmin() {
            return "Admin".equals(role);
        }
    }

    private static class Employee {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty department;
        private final SimpleStringProperty designation;
        private final SimpleStringProperty status;
        private final SimpleStringProperty joiningDate;

        private Employee(String id, String name, String department, String designation, String status, String joiningDate) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
            this.designation = new SimpleStringProperty(designation);
            this.status = new SimpleStringProperty(status);
            this.joiningDate = new SimpleStringProperty(joiningDate);
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
        private final SimpleStringProperty reason;
        private final SimpleStringProperty status;
        private final SimpleStringProperty comment;

        private LeaveRequest(String requestId, String employee, String leaveType, String from, String to, String reason, String status) {
            this.requestId = new SimpleStringProperty(requestId);
            this.employee = new SimpleStringProperty(employee);
            this.leaveType = new SimpleStringProperty(leaveType);
            this.from = new SimpleStringProperty(from);
            this.to = new SimpleStringProperty(to);
            this.reason = new SimpleStringProperty(reason);
            this.status = new SimpleStringProperty(status);
            this.comment = new SimpleStringProperty("");
        }
    }

    private static class PayrollRecord {
        private final SimpleStringProperty employee;
        private long basic;
        private long allowances;
        private long deductions;

        private PayrollRecord(String employee, long basic, long allowances, long deductions) {
            this.employee = new SimpleStringProperty(employee);
            this.basic = basic;
            this.allowances = allowances;
            this.deductions = deductions;
        }

        private long netPay() {
            return basic + allowances - deductions;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
