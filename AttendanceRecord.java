package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name="attendance_records",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","attendance_date"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id") private User employee;
    @Column(name="attendance_date",nullable=false) private LocalDate attendanceDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AttendanceStatus status;
    @Column(name="check_in") private LocalTime checkIn;
    @Column(name="check_out") private LocalTime checkOut;
    private String notes;
    public AttendanceRecord() {}
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public LocalDate getAttendanceDate(){return attendanceDate;} public void setAttendanceDate(LocalDate v){attendanceDate=v;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public LocalTime getCheckIn(){return checkIn;} public void setCheckIn(LocalTime v){checkIn=v;} public LocalTime getCheckOut(){return checkOut;} public void setCheckOut(LocalTime v){checkOut=v;} public void setNotes(String v){notes=v;}
}
