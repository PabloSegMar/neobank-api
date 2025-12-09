package com.example.neo_bank.api.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Componente de seguridad para la encriptación de datos sensibles (PII) en reposo.
 * <p>
 * Implementa un {@link AttributeConverter} de JPA para cifrar automáticamente
 * el IBAN antes de guardarlo en la base de datos y descifrarlo al leerlo.
 * Utiliza el estándar <b>AES (Advanced Encryption Standard)</b>.
 * <p>
 * Nota: Esto asegura que incluso si la base de datos es comprometida, los datos bancarios
 * permanecen ilegibles sin la clave de aplicación.
 */
@Component
@Converter
public class IbanEncryptor implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "SecretoBanco1234".getBytes();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;

        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar datos", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            // Si falla (porque es un dato antiguo no encriptado), devolvemos el dato tal cual
            return dbData;
        }
    }
}
