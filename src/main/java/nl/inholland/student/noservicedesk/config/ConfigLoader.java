package nl.inholland.student.noservicedesk.config;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and decrypts AES-GCM encrypted configuration files.
 */
public class ConfigLoader {

    // GCM authentication tag length
    private static final int GCM_TAG_LENGTH = 128;
    // Size of IV used when encrypting
    private static final int IV_LENGTH = 12;

    /**
     * Loads and decrypts a config file from the classpath.
     *
     * @param encryptedClasspathPath Path to the encrypted config on classpath (e.g. "/config.enc")
     * @param aesKey 32-byte AES key
     * @return Properties object with decrypted values
     * @throws Exception if file missing, key invalid, or decryption fails
     */
    public static Properties loadEncryptedConfig(String encryptedClasspathPath, byte[] aesKey) throws Exception {

        // Read encrypted file from classpath
        InputStream is = ConfigLoader.class.getResourceAsStream(encryptedClasspathPath);
        if (is == null) {
            throw new IllegalStateException("Encrypted config file not found on classpath: " + encryptedClasspathPath);
        }

        byte[] fileBytes = is.readAllBytes();

        if (fileBytes.length < IV_LENGTH) {
            throw new IllegalStateException("Encrypted file is too short to contain IV + ciphertext.");
        }

        // Extract IV (first 12 bytes)
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(fileBytes, 0, iv, 0, IV_LENGTH);

        // Ciphertext (remaining bytes)
        byte[] ciphertext = new byte[fileBytes.length - IV_LENGTH];
        System.arraycopy(fileBytes, IV_LENGTH, ciphertext, 0, ciphertext.length);

        // Prepare AES key
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");

        // Decrypt AES/GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

        byte[] plaintext = cipher.doFinal(ciphertext);

        // Load properties
        Properties props = new Properties();
        props.load(new ByteArrayInputStream(plaintext));

        return props;
    }
}

