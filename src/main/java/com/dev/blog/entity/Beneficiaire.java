package com.dev.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Entity
@Table(name = "beneficiaires")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    private String besoins;

    private String situationFamiliale;

    private Integer nombreEnfants;

    @JsonIgnore
    @OneToMany(mappedBy = "beneficiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Distribution> distributions;
}