package co.movio.rsasigner;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import org.spongycastle.jce.X509Principal;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.x509.X509V3CertificateGenerator;

class KeyStoreAdapterJKS implements KeyStoreAdapter {

    private static final String LOG_TAG = KeyStoreAdapterJKS.class.getName();

    private static final String KEY_STORE_NAME = "AndroidKeyStore";
    private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    private final KeyStore keyStore;
    private final Date validUntil;

    KeyStoreAdapterJKS() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_NAME);
            keyStore.load(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to initialize KeyStore [" + KEY_STORE_NAME + "]: " + e.getMessage(), e);
        }
        this.keyStore = keyStore;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 10000);
        this.validUntil = c.getTime();
    }

    @Override
    public KeyPair getKeyPair(String alias) throws Exception {
        Log.d(LOG_TAG, ">> getKeyPair([" + alias + "])");
        KeyPair result = null;
        if (keyStore != null) {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
            Certificate cert = keyStore.getCertificate(alias);

            if (privateKey != null && cert != null) {
                Log.d(LOG_TAG, "found key pair for alias [" + alias + "]");
                result = new KeyPair(cert.getPublicKey(), privateKey);
            }
        }
        Log.d(LOG_TAG, "<< getKeyPair([" + alias + "])");
        return result;
    }

    @Override
    public void setKeyPair(String alias, KeyPair pair) throws Exception {
        Log.d(LOG_TAG, ">> setKeyPair([" + alias + "], keyPair)");
        if (keyStore != null) {
            X509Certificate[] certChain = new X509Certificate[1];
            certChain[0] = generateCertificate(pair);
            keyStore.setKeyEntry(alias, pair.getPrivate(), null, certChain);
            Log.i(LOG_TAG, "set new key pair for alias [" + alias + "]");
        } else {
            Log.w(LOG_TAG, "no initialized keystore.");
        }
        Log.d(LOG_TAG, "<< setKeyPair([" + alias + "], keyPair)");
    }

    @Override
    public void deleteKeyPair(String alias) throws Exception {
        Log.d(LOG_TAG, ">> deleteKeyPair([" + alias + "])");
        if (keyStore != null) {
            keyStore.deleteEntry(alias);
            Log.i(LOG_TAG, "deleted key pair for alias [" + alias + "]");
        } else {
            Log.w(LOG_TAG, "no initialized keystore.");
        }
        Log.d(LOG_TAG, "<< deleteKeyPair([" + alias + "])");
    }

    private X509Certificate generateCertificate(KeyPair keyPair) throws Exception {
        X509V3CertificateGenerator cert = new X509V3CertificateGenerator();
        cert.setSerialNumber(BigInteger.valueOf(1));   //or generate a random number
        cert.setSubjectDN(new X509Principal("CN=localhost"));  //see examples to add O,OU etc
        cert.setIssuerDN(new X509Principal("CN=localhost")); //same since it is self-signed
        cert.setPublicKey(keyPair.getPublic());
        cert.setNotBefore(new Date());
        cert.setNotAfter(validUntil);
        cert.setSignatureAlgorithm("SHA1WithRSAEncryption");
        PrivateKey signingKey = keyPair.getPrivate();
        return cert.generate(signingKey, PROVIDER_NAME);
    }

    @Override
    public void close() {
    }
}
