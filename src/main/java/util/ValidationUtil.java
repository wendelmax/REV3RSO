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

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$");
    
    private static final Pattern CPF_PATTERN = 
            Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");
    
    private static final Pattern CNPJ_PATTERN = 
            Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    
    /**
     * Valida um endereço de e-mail.
     * 
     * @param email E-mail a ser validado
     * @return true se o e-mail for válido, false caso contrário
     */
    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
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
        if (StringUtils.isBlank(cnpj)) {
            return false;
        }
        
        // Verifica o formato
        if (!CNPJ_PATTERN.matcher(cnpj).matches()) {
            return false;
        }
        
        // Remove caracteres não numéricos
        cnpj = StringUtils.getDigits(cnpj);
        
        // Verifica se todos os dígitos são iguais
        if (StringUtils.containsOnly(cnpj, cnpj.charAt(0))) {
            return false;
        }
        
        // Calcula o primeiro dígito verificador
        int sum = 0;
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights1[i];
        }
        
        int remainder = sum % 11;
        int digit1 = remainder < 2 ? 0 : 11 - remainder;
        
        if (digit1 != (cnpj.charAt(12) - '0')) {
            return false;
        }
        
        // Calcula o segundo dígito verificador
        sum = 0;
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights2[i];
        }
        
        remainder = sum % 11;
        int digit2 = remainder < 2 ? 0 : 11 - remainder;
        
        return digit2 == (cnpj.charAt(13) - '0');
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
}
