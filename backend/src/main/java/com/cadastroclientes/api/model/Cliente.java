package com.cadastroclientes.api.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "logotipo", columnDefinition = "VARBINARY(MAX)")
    private byte[] logotipo;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Logradouro> logradouros = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Método auxiliar para adicionar logradouro
    public void addLogradouro(Logradouro logradouro) {
        logradouros.add(logradouro);
        logradouro.setCliente(this);
    }

    // Método auxiliar para remover logradouro
    public void removeLogradouro(Logradouro logradouro) {
        logradouros.remove(logradouro);
        logradouro.setCliente(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getLogotipo() {
        return logotipo;
    }

    public void setLogotipo(byte[] logotipo) {
        this.logotipo = logotipo;
    }

    public List<Logradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<Logradouro> logradouros) {
        this.logradouros = logradouros;
    }
}
