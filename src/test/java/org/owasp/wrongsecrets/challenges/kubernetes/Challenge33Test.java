package org.owasp.wrongsecrets.challenges.kubernetes;

import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class Challenge33Test {

    @Test
    void getCiphertextforCrhis() throws Exception {
        String plaintext="This was a standardValue as SecureSecret";
        String cipherTextString = encrypt(encrypt(encrypt(plaintext)));
        String newPlainText= decrypt(decrypt(decrypt(cipherTextString)));

        System.out.println("Hi Chris, please use the code below in your getdata() this as your cipherTextString: " + cipherTextString);

        Assertions.assertThat(newPlainText).isEqualTo(plaintext);
    }

    private String decrypt(String cipherTextString) throws Exception{
        final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey decryptKey = new SecretKeySpec("Letsencryptnow!!".getBytes(StandardCharsets.UTF_8), "AES");
        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, Base64.decode(cipherTextString), 0, 12);
        decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
        return new String(decryptor.doFinal(Base64.decode(cipherTextString), 12, Base64.decode(cipherTextString).length - 12), StandardCharsets.UTF_8);
    }

    private String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        SecretKey secretKey = new SecretKeySpec("Letsencryptnow!!".getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return Base64.toBase64String(byteBuffer.array());
    }

}
