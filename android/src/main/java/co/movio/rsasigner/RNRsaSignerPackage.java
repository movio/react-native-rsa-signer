
package co.movio.rsasigner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.security.Security;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

import org.spongycastle.jce.provider.BouncyCastleProvider;

public class RNRsaSignerPackage implements ReactPackage {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      KeyStoreAdapter ksAdapter = KeyStoreAdapter.Factory.getInstance(reactContext);
      return Arrays.<NativeModule>asList(new RNRsaSignerModule(reactContext, ksAdapter));
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
      return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Collections.emptyList();
    }
}
