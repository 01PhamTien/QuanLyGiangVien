package com.example.demo.students.model.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Hàng danh sách admin: chỉ các cột hiển thị trên bảng (mã, trạng thái, nhân thân, địa chỉ).
 */
public class StudentListRow {

    private final UUID id;
    private final String code;
    private final String status;
    private final String fullname;
    private final String gender;
    private final LocalDate dateOfBirth;
    private final LocalDate dateOfIssue;
    private final String personalIdentificationNumber;
    private final String cardPlace;
    private final String address;
    private final String currentAddress;

    public StudentListRow(
            UUID id,
            String code,
            String status,
            String fullname,
            String gender,
            LocalDate dateOfBirth,
            LocalDate dateOfIssue,
            String personalIdentificationNumber,
            String cardPlace,
            String address,
            String currentAddress) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.fullname = fullname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfIssue = dateOfIssue;
        this.personalIdentificationNumber = personalIdentificationNumber;
        this.cardPlace = cardPlace;
        this.address = address;
        this.currentAddress = currentAddress;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getFullname() {
        return fullname;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public String getCardPlace() {
        return cardPlace;
    }

    public String getAddress() {
        return address;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }
}
