package com.praticamodular.HospManager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {
    public static String normalizar(String cpf) {
        return cpf == null ? null : cpf.replaceAll("[^0-9]", "");
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        if (valor == null || !valor.matches("(?:[0-9]{11}|[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2})")) {
            return false;
        }
        String cpf = normalizar(valor);
        if (cpf.chars().distinct().count() == 1) return false;
        for (int tamanho = 9; tamanho <= 10; tamanho++) {
            int soma = 0;
            for (int i = 0; i < tamanho; i++) soma += (cpf.charAt(i) - '0') * (tamanho + 1 - i);
            int digito = 11 - soma % 11;
            if (digito >= 10) digito = 0;
            if (digito != cpf.charAt(tamanho) - '0') return false;
        }
        return true;
    }
}
