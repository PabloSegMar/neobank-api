package com.example.neo_bank.api.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity // 1. Convierte esta clase en una tabla SQL
@Table(name= "users")  // 2. Nombre de la tabla en la base de datos
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column (unique = true, nullable = false) // El email no puede repetirse ni estar vacio
    private String email;

    private String password;

    // Relación: Un usuario tiene muchas cuentas
    // "mappedBy" dice que la clave foránea está en la otra clase (Account)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch =  FetchType.LAZY)
    private List<Account> accounts;

    public User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
