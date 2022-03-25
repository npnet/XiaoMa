package com.xiaoma.utils.encrypt;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

/**
 * Created by youthyj on 2018/9/10.
 */
public class RSAUtils {
    private RSAUtils() throws Exception {
        throw new Exception();
    }

    static final
    public String encryptByXMPublicKey(String data) throws Exception {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        RSAPublicKey publicKey = RSAPublicKeyHolder.getInstance().getPublicKey();
        return encryptByPublicKey(data, publicKey);
    }

    static final
    public String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        if (TextUtils.isEmpty(data) || publicKey == null) {
            return "";
        }
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int keyLength = publicKey.getModulus().bitLength() / 8;
        String[] datas = splitString(data, keyLength - 11);
        String result = "";
        for (String s : datas) {
            result += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return result;
    }

    static
    private String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    static
    public String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    public static final class RSAPublicKeyHolder {
        private static RSAPublicKeyHolder instance;
        private RSAPublicKey rsaPublicKey;

        public static RSAPublicKeyHolder getInstance() {
            if (instance == null) {
                synchronized (RSAPublicKeyHolder.class) {
                    if (instance == null) {
                        instance = new RSAPublicKeyHolder();
                    }
                }
            }
            return instance;
        }

        private RSAPublicKeyHolder() {
        }

        public boolean init(Context context) {
            if (context == null) {
                return false;
            }
            context = context.getApplicationContext();

            AssetManager assets = context.getApplicationContext().getAssets();
            try {
                InputStream in = assets.open("key/publicKey.key");
                ObjectInputStream oin = new ObjectInputStream(in);
                Object key = oin.readObject();
                oin.close();
                rsaPublicKey = (RSAPublicKey) key;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public RSAPublicKey getPublicKey() {
            if (rsaPublicKey == null) {
                try {
                    throw new Exception("getRSAPublicKey first");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return rsaPublicKey;
        }
    }
}
