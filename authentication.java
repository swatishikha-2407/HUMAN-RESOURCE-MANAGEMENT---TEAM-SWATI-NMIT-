import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Simple JavaFX Authentication frontend demo:
 * 1. Login
 * 2. Sign Up
 * 3. Email verification / error states
 *
 * Run with a JavaFX SDK configured, e.g.:
 * javac --module-path <path-to-javafx-sdk>/lib --add-modules javafx.controls AuthApp.java
 * java  --module-path <path-to-javafx-sdk>/lib --add-modules javafx.controls AuthApp
 */
public class AuthApp extends Application {

    // Fake in-memory "database" of registered users: email -> password
    private final Map<String, String> registeredUsers = new HashMap<>();
    // Tracks which emails have been "verified"
    private final Map<String, Boolean> verifiedEmails = new HashMap<>();

    private StackPane rootStack;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    @Override
    public void start(Stage stage) {
        rootStack = new StackPane();
        rootStack.setPadding(new Insets(20));
        rootStack.getChildren().add(buildLoginView());

        Scene scene = new Scene(rootStack, 420, 480);
        stage.setTitle("Authentication Demo");
        stage.setScene(scene);
        stage.show();
    }

    // ---------------------------------------------------------------
    // LOGIN VIEW
    // ---------------------------------------------------------------
    private VBox buildLoginView() {
        Label title = sectionTitle("Login");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(statusLabel, "Please fill in both email and password.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showError(statusLabel, "That doesn't look like a valid email address.");
                return;
            }
            if (!registeredUsers.containsKey(email)) {
                showError(statusLabel, "No account found for this email. Please sign up.");
                return;
            }
            if (!registeredUsers.get(email).equals(password)) {
                showError(statusLabel, "Incorrect password. Please try again.");
                return;
            }
            if (!Boolean.TRUE.equals(verifiedEmails.get(email))) {
                showError(statusLabel, "Email not verified yet. Redirecting to verification...");
                switchTo(buildVerificationView(email));
                return;
            }

            showSuccess(statusLabel, "Login successful! Welcome back, " + email + ".");
        });

        Hyperlink toSignUp = new Hyperlink("Don't have an account? Sign up");
        toSignUp.setOnAction(e -> switchTo(buildSignUpView()));

        VBox box = formBox(title, emailField, passwordField, statusLabel, loginBtn, toSignUp);
        return box;
    }

    // ---------------------------------------------------------------
    // SIGN UP VIEW
    // ---------------------------------------------------------------
    private VBox buildSignUpView() {
        Label title = sectionTitle("Sign Up");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button signUpBtn = new Button("Create Account");
        signUpBtn.setDefaultButton(true);
        signUpBtn.setMaxWidth(Double.MAX_VALUE);
        signUpBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirm = confirmField.getText();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showError(statusLabel, "All fields are required.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showError(statusLabel, "Please enter a valid email address.");
                return;
            }
            if (registeredUsers.containsKey(email)) {
                showError(statusLabel, "An account with this email already exists.");
                return;
            }
            if (password.length() < 6) {
                showError(statusLabel, "Password must be at least 6 characters.");
                return;
            }
            if (!password.equals(confirm)) {
                showError(statusLabel, "Passwords do not match.");
                return;
            }

            registeredUsers.put(email, password);
            verifiedEmails.put(email, false);
            showSuccess(statusLabel, "Account created! Please verify your email.");
            switchTo(buildVerificationView(email));
        });

        Hyperlink toLogin = new Hyperlink("Already have an account? Log in");
        toLogin.setOnAction(e -> switchTo(buildLoginView()));

        return formBox(title, emailField, passwordField, confirmField, statusLabel, signUpBtn, toLogin);
    }

    // ---------------------------------------------------------------
    // EMAIL VERIFICATION / ERROR STATES VIEW
    // ---------------------------------------------------------------
    private VBox buildVerificationView(String email) {
        Label title = sectionTitle("Email Verification");

        Label info = new Label("A verification code was sent to:\n" + email);
        info.setWrapText(true);
        info.setStyle("-fx-font-size: 13px;");

        // Simulated correct code
        String correctCode = "123456";

        TextField codeField = new TextField();
        codeField.setPromptText("Enter 6-digit code");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button verifyBtn = new Button("Verify Email");
        verifyBtn.setMaxWidth(Double.MAX_VALUE);
        verifyBtn.setOnAction(e -> {
            String code = codeField.getText().trim();

            if (code.isEmpty()) {
                showError(statusLabel, "Please enter the verification code.");
                return;
            }
            if (!code.matches("\\d{6}")) {
                showError(statusLabel, "Code must be exactly 6 digits.");
                return;
            }
            if (!code.equals(correctCode)) {
                showError(statusLabel, "Invalid verification code. Please try again.");
                return;
            }

            verifiedEmails.put(email, true);
            showSuccess(statusLabel, "Email verified successfully!");
            switchTo(buildLoginView());
        });

        Button resendBtn = new Button("Resend Code");
        resendBtn.setMaxWidth(Double.MAX_VALUE);
        resendBtn.setOnAction(e ->
                showSuccess(statusLabel, "A new code has been sent to " + email + ". (Demo code: " + correctCode + ")"));

        Hyperlink backToLogin = new Hyperlink("Back to Login");
        backToLogin.setOnAction(e -> switchTo(buildLoginView()));

        return formBox(title, info, codeField, statusLabel, verifyBtn, resendBtn, backToLogin);
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private void switchTo(VBox view) {
        rootStack.getChildren().setAll(view);
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 22));
        return label;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web("#c0392b"));
    }

    private void showSuccess(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web("#27ae60"));
    }

    private VBox formBox(javafx.scene.Node... nodes) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(320);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);");
        box.getChildren().addAll(nodes);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}