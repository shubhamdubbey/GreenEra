package com.green_era.gardener_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "gardeners")
@Data
public class GardenerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "locality")
    private String locality;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "is_urgent_type")
    private boolean isUrgentType;
}
