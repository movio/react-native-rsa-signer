package co.movio.rsasigner;

import java.lang.RuntimeException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class RNRsaSignerModule extends ReactContextBaseJavaModule {

    private static final String LOG_TAG = RNRsaSignerModule.class.getName();

    private static final String PROVIDER_NAME = "AndroidKeyStore";
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA;
    private static final int KEY_SIZE = 3072;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final KeyStoreAdapterJKS keyStoreAdapter;

    RNRsaSignerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.keyStoreAdapter = new KeyStoreAdapterJKS();
    }

    @Override
    public String getName() {
        return "RNRsaSigner";
    }

    @ReactMethod
    public void getPublicKey(String alias, Promise promise) {
        try {
            PublicKey publicKey = this.keyStoreAdapter.getPublicKey(alias);
            if (publicKey == null) {
                promise.resolve(null);
            } else {
                String pem = toPem(publicKey);
                promise.resolve(pem);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to retrieve public key for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("getPublicKey", "Failed to retrieve public key: " + e.getMessage(), e);
        }
    }

    @ReactMethod
    public void regenerateKey(String alias, Promise promise) {
        try {
            deleteKeyIfExists(alias);
            KeyPair keyPair = generateKeyPair(alias);
            String pem = toPem(keyPair.getPublic());
            promise.resolve(pem);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to regenerate key for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("delete_key", "Failed to delete key: " + e.getMessage(), e);
        }
    }

    @ReactMethod
    public void sign(String alias, String data, Promise promise) {
        try {
            PrivateKey key = getPrivateKey(alias);
            String signature = Base64.encodeToString(sign(key, data), Base64.URL_SAFE);
            promise.resolve(signature);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to sign for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("sign", "Failed to sign: " + e.getMessage(), e);
        }
    }

    private void deleteKeyIfExists(String alias) throws Exception {
        keyStoreAdapter.deletePrivateKey(alias);
    }

    private PrivateKey getPrivateKey(String alias) throws Exception {
        PrivateKey key = keyStoreAdapter.getPrivateKey(alias);
        if (key == null) {
            throw new RuntimeException("Could not find a key for [" + alias + "] alias");
        }
        return key;
    }

    // This will automatically store the keypair in the Android's Keystore after generation.
    private KeyPair generateKeyPair(String alias) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
        gen.initialize(new KeyGenParameterSpec.Builder(
            alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY
        )
        .setDigests(KeyProperties.DIGEST_SHA256)
        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
        .setKeySize(KEY_SIZE)
        .build());
        return gen.genKeyPair();
    }

    private byte[] sign(PrivateKey privateKey, String data) throws Exception {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initSign(privateKey);
        sig.update(data.getBytes("UTF-8"));
        return sig.sign();
    }

    private String toPem(PublicKey key) throws Exception {
        String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        return "-----BEGIN RSA PUBLIC KEY-----\n" + encodedKey + "\n-----END RSA PUBLIC KEY-----";
    }
}
