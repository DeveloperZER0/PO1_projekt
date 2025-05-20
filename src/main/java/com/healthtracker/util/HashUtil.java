package com.healthtracker.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {
    // public static String hash(String raw)
    // public static boolean verify(String raw, String hash)
    // – użyj BCrypt (np. import org.mindrot.jbcrypt.BCrypt)
    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean verify(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
