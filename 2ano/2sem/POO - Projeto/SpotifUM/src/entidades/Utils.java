package entidades;


import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Utils {

    /**
     * Método que faz uma deep copy de uma estrtura Map
     * @param original Estrutura Map original
     * @return HashMap com os valores copiados
     */
    public static <T> Map<String, T> copyMap(Map<String, T> original) {
        if (original == null) return null;

        Map<String, T> novo = new HashMap<>();

        for (Map.Entry<String, T> entry : original.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();

            if (value == null) {
                novo.put(key, null);
                continue;
            }

            try {
                Method cloneMethod = value.getClass().getMethod("clone");
                @SuppressWarnings("unchecked")
                T clonedValue = (T) cloneMethod.invoke(value);
                novo.put(key, clonedValue);
            } catch (Exception e) {
                throw new RuntimeException("Falha ao clonar objeto com chave: " + key, e);
            }
        }

        return novo;
    }

    /**
     * Método que converte uma String do tipo dd/MM/yyyy num objeto LocalDateTime
     * @param str String a ser convertida
     * @return Estrutura LocalDateTime resultante da String
     */
    public static LocalDateTime stringtoDateTime(String str) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(str, formatter).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }


}