package me.r1ver.login.bukkit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA1Encrypt {

    private static final Logger LOGGER = Logger.getLogger("SHA1Encrypt");

    public static String encrypt(String input) {
        if (input == null) return null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar algoritmo SHA-1", e);
            return null;
        }
    }
}