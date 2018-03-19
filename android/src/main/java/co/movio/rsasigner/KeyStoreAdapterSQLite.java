package co.movio.rsasigner;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;

class KeyStoreAdapterSQLite extends SQLiteOpenHelper implements KeyStoreAdapter {

    private static final String LOG_TAG = KeyStoreAdapterSQLite.class.getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KeyStore.db";
    private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    private final KeyFactory keyFactory;

    KeyStoreAdapterSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA", PROVIDER_NAME);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to initialize KeyFactory [" + PROVIDER_NAME + "]: " + e.getMessage(), e);
        }
        this.keyFactory = keyFactory;
    }

    @Override
    public KeyPair getKeyPair(String alias) throws Exception {
        Log.d(LOG_TAG, ">> getKeyPair([" + alias + "])");
        String[] columns = { Schema.COLUMN_PRIVATE_KEY, Schema.COLUMN_PUBLIC_KEY };
        String selection = Schema.COLUMN_ALIAS + " = ?";
        String[] selectionArgs = { alias };

        KeyPair result = null;

        Cursor cursor = getReadableDatabase().query(Schema.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, "found key pair for alias [" + alias + "]");
            byte[] privateKeyBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(Schema.COLUMN_PRIVATE_KEY));
            byte[] publicKeyBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(Schema.COLUMN_PUBLIC_KEY));
            Log.d(LOG_TAG, "private key has " + privateKeyBytes.length + " bytes, public key has " + publicKeyBytes.length + " bytes.");
            PrivateKey privateKey = readPrivateKey(privateKeyBytes);
            PublicKey publicKey = readPublicKey(publicKeyBytes);
            result = new KeyPair(publicKey, privateKey);
        }
        cursor.close();

        Log.d(LOG_TAG, "<< getKeyPair([" + alias + "])");
        return result;
    }

    @Override
    public void setKeyPair(String alias, KeyPair keyPair) throws Exception {
        Log.d(LOG_TAG, ">> setKeyPair([" + alias + "], keyPair)");

        byte[] privateKeyBytes = writePrivateKey(keyPair.getPrivate());
        byte[] publicKeyBytes = writePublicKey(keyPair.getPublic());
        Log.d(LOG_TAG, "private key has " + privateKeyBytes.length + " bytes, public key has " + publicKeyBytes.length + " bytes.");

        ContentValues values = new ContentValues();
        values.put(Schema.COLUMN_ALIAS, alias);
        values.put(Schema.COLUMN_PRIVATE_KEY, privateKeyBytes);
        values.put(Schema.COLUMN_PUBLIC_KEY, publicKeyBytes);

        getWritableDatabase().replace(Schema.TABLE_NAME, null, values);
        Log.i(LOG_TAG, "set new key pair for alias [" + alias + "]");
        Log.d(LOG_TAG, "<< setKeyPair([" + alias + "], keyPair)");
    }

    @Override
    public void deleteKeyPair(String alias) throws Exception {
        Log.d(LOG_TAG, ">> deleteKeyPair([" + alias + "])");
        String[] selectionArgs = { alias };
        int cnt = getWritableDatabase().delete(Schema.TABLE_NAME, Schema.COLUMN_ALIAS + "=?", selectionArgs);
        if (cnt > 0) {
            Log.i(LOG_TAG, "deleted key pair for alias [" + alias + "]");
        } else {
            Log.d(LOG_TAG, "found no key pair to delete for alias [" + alias + "]");
        }
        Log.d(LOG_TAG, "<< deleteKeyPair([" + alias + "])");
    }

    private byte[] writePrivateKey(PrivateKey key) throws Exception {
        return key.getEncoded();
    }

    private byte[] writePublicKey(PublicKey key) throws Exception {
        return key.getEncoded();
    }

    private PrivateKey readPrivateKey(byte[] data) throws Exception {
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(data));
    }

    private PublicKey readPublicKey(byte[] data) throws Exception {
        return keyFactory.generatePublic(new X509EncodedKeySpec(data));
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Schema.CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL(Schema.getUpgradeSql(oldVersion, newVersion));
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL(Schema.getDowngradeSql(oldVersion, newVersion));
    }

    private static class Schema {
        static String TABLE_NAME = "keystore";
        static String COLUMN_ALIAS = "alias";
        static String COLUMN_PRIVATE_KEY = "priv";
        static String COLUMN_PUBLIC_KEY = "pub";

        static String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        COLUMN_ALIAS +  " TEXT PRIMARY KEY, " +
                        COLUMN_PRIVATE_KEY + " BLOB, " +
                        COLUMN_PUBLIC_KEY + " BLOB)";
    }
}
