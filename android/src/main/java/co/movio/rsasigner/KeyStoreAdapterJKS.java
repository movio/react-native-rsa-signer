package co.movio.rsasigner;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyStore.PrivateKeyEntry;

import android.util.Log;

class KeyStoreAdapterJKS {

    private static final String LOG_TAG = KeyStoreAdapterJKS.class.getName();
    private static final String KEY_STORE_NAME = "AndroidKeyStore";

    private final KeyStore keyStore;

    KeyStoreAdapterJKS() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_NAME);
            keyStore.load(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to initialize KeyStore [" + KEY_STORE_NAME + "]: " + e.getMessage(), e);
        }
        this.keyStore = keyStore;
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        Log.d(LOG_TAG, ">> getPrivateKey([" + alias + "])");
        PrivateKey result = null;
        if (keyStore != null) {
            PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(alias, null);

            Log.d(LOG_TAG, "******** Public key=" + entry.getCertificate().getPublicKey().toString());

            result = entry.getPrivateKey();
            if (result != null) {
                Log.d(LOG_TAG, "found key pair for alias [" + alias + "]");
            }
        }
        Log.d(LOG_TAG, "<< getKeyPair([" + alias + "])");
        return result;
    }

    public PublicKey getPublicKey(String alias) throws Exception {
        Log.d(LOG_TAG, ">> getPublicKey([" + alias + "])");
        PublicKey result = null;

        if (keyStore != null) {
            PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(alias, null);
            result = entry.getCertificate().getPublicKey();
            if (result != null) {
                Log.d(LOG_TAG, "found key pair for alias [" + alias + "]");
            }
        }

        Log.d(LOG_TAG, "<< getPublicKey([" + alias + "])");

        return result;
    }

    public void deletePrivateKey(String alias) throws Exception {
        Log.d(LOG_TAG, ">> deletePrivateKey([" + alias + "])");
        if (keyStore != null) {
            keyStore.deleteEntry(alias);
            Log.i(LOG_TAG, "deleted key pair for alias [" + alias + "]");
        } else {
            Log.w(LOG_TAG, "no initialized keystore.");
        }
        Log.d(LOG_TAG, "<< deletePrivateKey([" + alias + "])");
    }
}
