package com.pluk.fiveballs.utils;

import static com.google.firebase.crashlytics.internal.common.CommonUtils.sha1;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SecurityManager {

    private static final String TAG = "SecurityManager";
    private static String secret = "fivemore2010uy24";

    private static SecurityManager instance;

    private SecurityManager() {
    }

    public static SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
         }
        return instance;
    }

    public String getApiKey(int score) {
        return resolve(String.valueOf(score));
    }

    public String resolve(String dataOriginal) {
        String data = secret + dataOriginal + "fiveballs";
        String encrypted = encrypt(data);
        return encrypted;
    }

    private String encrypt(String dataOriginal) {
        return sha1(dataOriginal);
    }

    @Nullable
    private String old(int score) {
        String texto = "fiveballs" + score;

        String claveEncriptada;
        try {
            claveEncriptada = SHA1(secret + score + "fiveballs");
            Log.i(TAG, "La clave encriptada es: " + claveEncriptada);

            return URLEncoder.encode(claveEncriptada);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchALgorithmExceptin " + e.getLocalizedMessage());
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    private String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public byte[] encrypt(String text, String password) {
        try {
            byte[] input = text.getBytes();
            byte[] key = password.getBytes();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return cipher.doFinal(input);

        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

}