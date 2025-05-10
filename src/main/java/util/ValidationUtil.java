package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe utilitária para validação de dados.
 * Utiliza o Apache Commons Lang para operações básicas.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$");
    
    private static final Pattern CPF_PATTERN = 
            Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$");
    
    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");
    
    /**
     * Valida um endereço de e-mail.
     * 
     * @param email E-mail a ser validado
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Valida um número de telefone no formato (XX) XXXX-XXXX ou (XX) XXXXX-XXXX.
     * 
     * @param phone Telefone a ser validado
     * @return true se o telefone for válido, false caso contrário
     */
    public static boolean isValidPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Valida um CPF no formato XXX.XXX.XXX-XX.
     * 
     * @param cpf CPF a ser validado
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValidCPF(String cpf) {
        if (StringUtils.isBlank(cpf)) {
            return false;
        }
        
        // Verifica o formato
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }
        
        // Remove caracteres não numéricos
        cpf = StringUtils.getDigits(cpf);
        
        // Verifica se todos os dígitos são iguais
        if (StringUtils.containsOnly(cpf, cpf.charAt(0))) {
            return false;
        }
        
        // Calcula os dígitos verificadores
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        
        int remainder = sum % 11;
        int digit1 = remainder < 2 ? 0 : 11 - remainder;
        
        if (digit1 != (cpf.charAt(9) - '0')) {
            return false;
        }
        
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        
        remainder = sum % 11;
        int digit2 = remainder < 2 ? 0 : 11 - remainder;
        
        return digit2 == (cpf.charAt(10) - '0');
    }
    
    /**
     * Valida um CNPJ no formato XX.XXX.XXX/XXXX-XX.
     * 
     * @param cnpj CNPJ a ser validado
     * @return true se o CNPJ for válido, false caso contrário
     */
    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || !CNPJ_PATTERN.matcher(cnpj).matches()) {
            return false;
        }
        
        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");
        
        // Verifica se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        // Validação do CNPJ
        int[] multiplicadores1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] multiplicadores2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * multiplicadores1[i];
        }
        
        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;
        
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * multiplicadores2[i];
        }
        
        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;
        
        return digito1 == Integer.parseInt(String.valueOf(cnpj.charAt(12))) &&
               digito2 == Integer.parseInt(String.valueOf(cnpj.charAt(13)));
    }
    
    /**
     * Verifica se uma string não está vazia.
     * 
     * @param str String a ser verificada
     * @return true se a string não for vazia, false caso contrário
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }
    
    /**
     * Verifica se um valor está dentro de um intervalo.
     * 
     * @param value Valor a ser verificado
     * @param min Valor mínimo (inclusive)
     * @param max Valor máximo (inclusive)
     * @return true se o valor estiver dentro do intervalo, false caso contrário
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Valida um conjunto de campos e retorna os erros encontrados.
     * 
     * @param validations Mapa contendo campo e resultado da validação
     * @return Mapa contendo campo e mensagem de erro (vazio se não houver erros)
     */
    public static Map<String, String> validateFields(Map<String, Boolean> validations) {
        Map<String, String> errors = new HashMap<>();
        
        for (Map.Entry<String, Boolean> entry : validations.entrySet()) {
            if (!entry.getValue()) {
                errors.put(entry.getKey(), "Campo inválido: " + entry.getKey());
            }
        }
        
        return errors;
    }

    public static boolean isValidCEP(String cep) {
        return cep != null && CEP_PATTERN.matcher(cep).matches();
    }
}
