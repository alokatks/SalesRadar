package com.carsales.carsales_anaytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="car_sales")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CarSales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_number", unique = true)
    private String carNumber;

    private String brand;
    private String model;
    private String color;
    private int year;

    @Column(name = "date_of_purchase")
    private LocalDate dateOfPurchase;

    @Column(name = "time_of_purchase")
    private LocalTime timeOfPurchase;

    private long price;
    private double mileage;
    private int engine;
    private String fuelType;

    @Column(name = "payment_mode")
    private String paymentMode;
    private String state;
    private String city;
    private String customerName;

    @Column(name = "contact_number")
    private String contactNumber;
    private String email;

    @Column(name = "warranty_period")
    private int warrantyPeriod;

}
