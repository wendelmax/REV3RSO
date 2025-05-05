package util;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe que fornece métodos de extensão para uso em templates Qute.
 */
public class JavaExtensions {
    /**
     * Capitaliza cada palavra em uma string.
     * Usa o StringUtils do Apache Commons Lang para implementação mais robusta.
     * 
     * @param string String a ser capitalizada
     * @return String com cada palavra capitalizada
     */
    public static String capitalise(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return StringUtils.capitalize(string);
    }
    
    /**
     * Capitaliza cada palavra em uma string (incluindo palavras separadas por espaço).
     * 
     * @param string String a ser capitalizada
     * @return String com cada palavra capitalizada
     */
    public static String capitaliseWords(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        
        // Split the string by spaces, capitalize each word, and join them back together
        String[] words = string.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = StringUtils.capitalize(words[i]);
        }
        return String.join(" ", words);
    }
    
    /**
     * Trunca uma string ao tamanho especificado, adicionando reticências se necessário.
     * 
     * @param string String a ser truncada
     * @param maxLength Tamanho máximo
     * @return String truncada
     */
    public static String truncate(String string, int maxLength) {
        if (string == null) {
            return null;
        }
        return StringUtils.abbreviate(string, maxLength);
    }
}
