package com.example.employeeattendance.entity;

public final class Enums {
    private Enums() {}

    public enum Role {
        ADMIN, HR, EMPLOYEE
    }

    public enum AttendanceStatus {
        PRESENT, ABSENT, HALF_DAY, LEAVE
    }
}
