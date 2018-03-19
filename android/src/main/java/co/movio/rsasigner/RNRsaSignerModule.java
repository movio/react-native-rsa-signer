
package co.movio.rsasigner;

import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

public class RNRsaSignerModule extends ReactContextBaseJavaModule {

    private static final String LOG_TAG = RNRsaSignerModule.class.getName();

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;
    private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final KeyStoreAdapter keyStoreAdapter;

    RNRsaSignerModule(ReactApplicationContext reactContext, KeyStoreAdapter keyStoreAdapter) {
        super(reactContext);
        this.keyStoreAdapter = keyStoreAdapter;
    }

    @Override
    public String getName() {
        return "RNRsaSigner";
    }

    @ReactMethod
    public void getPublicKey(String alias, Promise promise) {
        try {
            KeyPair keyPair = getOrCreateKeyPair(alias);
            String pem = toPem(keyPair.getPublic());
            promise.resolve(pem);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to retrieve public key for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("get_public_key", "Failed to access key chain: " + e.getMessage(), e);
        }
    }

    @ReactMethod
    public void regenerateKey(String alias, Promise promise) {
        try {
            deleteKeyIfExists(alias);
            getPublicKey(alias, promise);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to regenerate key for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("delete_key", "Failed to delete key: " + e.getMessage(), e);
        }
    }

    @ReactMethod
    public void sign(String alias, String data, Promise promise) {
        try {
            KeyPair keyPair = getOrCreateKeyPair(alias);
            String signature = Base64.encodeToString(sign(keyPair.getPrivate(), data), Base64.URL_SAFE);
            Log.v(LOG_TAG, "Key: " + toPem(keyPair.getPublic()));
            Log.v(LOG_TAG, "Data: " + data);
            Log.v(LOG_TAG, "Signature:" + signature);
            promise.resolve(signature);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to sign for alias [" + alias + "]: " + e.getMessage(), e);
            promise.reject("sign", "Failed to sign: " + e.getMessage(), e);
        }
    }

    private void deleteKeyIfExists(String alias) throws Exception {
        keyStoreAdapter.deleteKeyPair(alias);
    }

    private KeyPair getOrCreateKeyPair(String alias) throws Exception {
        KeyPair pair = keyStoreAdapter.getKeyPair(alias);
        if (pair == null) {
            pair = generateKeyPair();
            keyStoreAdapter.setKeyPair(alias, pair);
        }
        return pair;
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
        gen.initialize(KEY_SIZE);
        return gen.genKeyPair();
    }

    private byte[] sign(PrivateKey privateKey, String data) throws Exception {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initSign(privateKey);
        sig.update(data.getBytes("UTF-8"));
        return sig.sign();
    }

    private String toPem(PublicKey key) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PemWriter pw = new PemWriter(stringWriter);
        pw.writeObject(new PemObject("RSA PUBLIC KEY", key.getEncoded()));
        pw.close();
        return stringWriter.toString();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        try {
            keyStoreAdapter.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to close KeyStoreAdapter: " + e.getMessage(), e);
        }
    }
}
