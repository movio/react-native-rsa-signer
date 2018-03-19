package co.movio.rsasigner;

import java.io.Closeable;
import java.security.KeyPair;

import android.content.Context;
import android.os.Build;

interface KeyStoreAdapter extends Closeable {
    KeyPair getKeyPair(String alias) throws Exception;
    void setKeyPair(String alias, KeyPair keyPair) throws Exception;
    void deleteKeyPair(String alias) throws Exception;

    class Factory {
        static KeyStoreAdapter getInstance(Context context) {
            // Keystore was introduced in Android 4.3 (API level 18)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return new KeyStoreAdapterJKS();
            } else {
                return new KeyStoreAdapterSQLite(context);
            }
        }
    }
}
