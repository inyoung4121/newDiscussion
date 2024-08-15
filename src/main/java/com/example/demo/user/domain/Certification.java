package com.example.demo.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Certification {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    private String email;
    @Column
    private String CertificationNumber;
    @Column
    private boolean CertificationStatus;

    @Builder
    public Certification(String email, String CertificationNumber, boolean CertificationStatus) {
        this.email = email;
        this.CertificationNumber = CertificationNumber;
        this.CertificationStatus = CertificationStatus;
    }
}
