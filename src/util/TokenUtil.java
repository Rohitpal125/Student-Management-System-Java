package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    // Login stays valid for 8 hours
    private static final long VALID_MILLIS = 8L * 60 * 60 * 1000;

    private static final byte[] KEY = loadKey();

    private static byte[] loadKey() {
        String secret = System.getenv("TOKEN_SECRET");

        if (secret == null || secret.length() < 32) {
            System.out.println("WARNING: TOKEN_SECRET is missing or shorter than 32 characters. "
                    + "Using a random key - everyone is logged out whenever the server restarts.");
            byte[] random = new byte[32];
            new SecureRandom().nextBytes(random);
            return random;
        }

        return secret.getBytes(StandardCharsets.UTF_8);
    }

    // Creates a token like:  <username>.<expiry time>.<signature>
    public static String create(String username) {
        long expires = System.currentTimeMillis() + VALID_MILLIS;

        String payload = encode(username.getBytes(StandardCharsets.UTF_8)) + "." + expires;

        return payload + "." + sign(payload);
    }

    // Returns the username if the token is real and not expired, otherwise null
    public static String verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payload = parts[0] + "." + parts[1];

            boolean signatureOk = MessageDigest.isEqual(
                    sign(payload).getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8));
            if (!signatureOk) return null;

            long expires = Long.parseLong(parts[1]);
            if (System.currentTimeMillis() > expires) return null;

            return new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);

        } catch (Exception e) {
            return null;
        }
    }

    private static String sign(String text) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(KEY, "HmacSHA256"));
            return encode(mac.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Token signing failed", e);
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}