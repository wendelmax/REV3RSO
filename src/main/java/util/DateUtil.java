package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para manipulação de datas.
 */
public class DateUtil {
    
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    
    /**
     * Formata uma data utilizando o formato padrão (dd/MM/yyyy).
     * 
     * @param date Data a ser formatada
     * @return Data formatada como string
     */
    public static String format(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Formata uma data utilizando o formato especificado.
     * 
     * @param date Data a ser formatada
     * @param format Formato a ser utilizado
     * @return Data formatada como string
     */
    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    /**
     * Converte uma string para data utilizando o formato padrão (dd/MM/yyyy).
     * 
     * @param dateStr String contendo a data
     * @return Objeto Date
     * @throws ParseException Se a string não puder ser convertida
     */
    public static Date parse(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        sdf.setLenient(false);
        return sdf.parse(dateStr);
    }
    
    /**
     * Converte uma string para data utilizando o formato especificado.
     * 
     * @param dateStr String contendo a data
     * @param format Formato a ser utilizado
     * @return Objeto Date
     * @throws ParseException Se a string não puder ser convertida
     */
    public static Date parse(String dateStr, String format) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.parse(dateStr);
    }
    
    /**
     * Verifica se uma data é posterior à outra.
     * 
     * @param date1 Primeira data
     * @param date2 Segunda data
     * @return true se a primeira data for posterior à segunda, false caso contrário
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.after(date2);
    }
    
    /**
     * Verifica se uma data é anterior à outra.
     * 
     * @param date1 Primeira data
     * @param date2 Segunda data
     * @return true se a primeira data for anterior à segunda, false caso contrário
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.before(date2);
    }
    
    /**
     * Calcula a diferença em dias entre duas datas.
     * 
     * @param date1 Primeira data
     * @param date2 Segunda data
     * @return Número de dias entre as datas
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        
        LocalDate localDate1 = convertToLocalDate(date1);
        LocalDate localDate2 = convertToLocalDate(date2);
        
        return ChronoUnit.DAYS.between(localDate1, localDate2);
    }
    
    /**
     * Adiciona dias a uma data.
     * 
     * @param date Data base
     * @param days Número de dias a serem adicionados (positivo) ou subtraídos (negativo)
     * @return Nova data com os dias adicionados/subtraídos
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        
        LocalDate localDate = convertToLocalDate(date);
        LocalDate newLocalDate = localDate.plusDays(days);
        
        return convertToDate(newLocalDate);
    }
    
    /**
     * Converte um objeto Date para LocalDate.
     * 
     * @param date Data a ser convertida
     * @return LocalDate correspondente
     */
    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(DEFAULT_ZONE_ID)
                .toLocalDate();
    }
    
    /**
     * Converte um objeto Date para LocalDateTime.
     * 
     * @param date Data a ser convertida
     * @return LocalDateTime correspondente
     */
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(DEFAULT_ZONE_ID)
                .toLocalDateTime();
    }
    
    /**
     * Converte um objeto LocalDate para Date.
     * 
     * @param localDate LocalDate a ser convertido
     * @return Date correspondente
     */
    public static Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }
    
    /**
     * Converte um objeto LocalDateTime para Date.
     * 
     * @param localDateTime LocalDateTime a ser convertido
     * @return Date correspondente
     */
    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }
    
    /**
     * Retorna a data atual.
     * 
     * @return Data atual
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Formata um intervalo de tempo em formato amigável.
     * 
     * @param start Data de início
     * @param end Data de fim
     * @return String representando o intervalo de tempo
     */
    public static String formatTimeInterval(Date start, Date end) {
        if (start == null || end == null) {
            return "";
        }
        
        long diffInMillis = end.getTime() - start.getTime();
        long diffInSeconds = diffInMillis / 1000;
        
        if (diffInSeconds < 60) {
            return diffInSeconds + " segundos";
        }
        
        long diffInMinutes = diffInSeconds / 60;
        if (diffInMinutes < 60) {
            return diffInMinutes + " minutos";
        }
        
        long diffInHours = diffInMinutes / 60;
        if (diffInHours < 24) {
            return diffInHours + " horas";
        }
        
        long diffInDays = diffInHours / 24;
        if (diffInDays < 30) {
            return diffInDays + " dias";
        }
        
        long diffInMonths = diffInDays / 30;
        if (diffInMonths < 12) {
            return diffInMonths + " meses";
        }
        
        long diffInYears = diffInMonths / 12;
        return diffInYears + " anos";
    }

    /**
     * Formata uma data java.util.Date usando o padrão especificado.
     * 
     * @param data Data a ser formatada
     * @param pattern Padrão de formatação
     * @return Data formatada como string
     */
    public static String formatarData(Date data, String pattern) {
        if (data == null) return "";
        return new SimpleDateFormat(pattern).format(data);
    }
    
    /**
     * Formata uma data java.util.Date usando o padrão padrão "dd/MM/yyyy HH:mm".
     * 
     * @param data Data a ser formatada
     * @return Data formatada como string
     */
    public static String formatarData(Date data) {
        return formatarData(data, "dd/MM/yyyy HH:mm");
    }
    
    /**
     * Formata uma data java.time.LocalDateTime usando o padrão especificado.
     * 
     * @param data Data a ser formatada
     * @param pattern Padrão de formatação
     * @return Data formatada como string
     */
    public static String formatarData(LocalDateTime data, String pattern) {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Formata uma data java.time.LocalDateTime usando o padrão padrão "dd/MM/yyyy HH:mm".
     * 
     * @param data Data a ser formatada
     * @return Data formatada como string
     */
    public static String formatarData(LocalDateTime data) {
        return formatarData(data, "dd/MM/yyyy HH:mm");
    }
}
