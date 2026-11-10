package com.praticamodular.HospManager.model;

import org.springframework.data.annotation.Id;
import com.praticamodular.HospManager.exception.RegraNegocioException;


public abstract class Pessoa  {
    @Id protected String id;
    protected String nome;
    protected String telefone;
    protected String email;

    public void validarContato() {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new RegraNegocioException("E-mail inválido");
        }
        if (telefone == null || !telefone.matches("^[+()0-9 .-]+$")
                || !telefone.replaceAll("[^0-9]", "").matches("[0-9]{10,13}")) {
            throw new RegraNegocioException("Telefone inválido: informe de 10 a 13 dígitos");
        }
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
