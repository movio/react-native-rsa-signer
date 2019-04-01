# react-native-rsa-signer

The RSA Signer generates a 1024 bit RSA key and stores it in

* iOS: Keychain
* Android (API level >= 18): Keystore
* Android (API level <= 17): SQLite

Use Case:
The RSA key pair can be created in the app during user registration, the public key can be exported as X.509 PEM and uploaded to the server. The key pair can later be used to create SHA256withRSA signatures to authenticate the user via self signed JWS token.

## Installation

Add a reference to this git repo in target `package.json` with a specific version tag, e.g:

```json
  "react-native-rsa-signer": "git+https://github.com/movio/react-native-rsa-signer.git#1.1.0"
```

`npm install`

`$ react-native link react-native-rsa-signer`

This plugin uses Swift. If you don't use Swift in your iOS project, you need to add a `empty.swift` file in your project to activate the Swift compiling.

* open your project in Xcode
* right click your project's folder in the Project Navigator, select `New File...`
* select the `Swift File` template, click `Next`
* set file name to `empty.swift`, make sure your project is enabled in `Targets`, click `Create`
* remove all contents of file `empty.swift`
* in the `Build Settings` of your project, select `Always Embed Swift Standard Libraries` and set `Swift Language Version = Swift 4.0`

## Usage

### Sign a message
```javascript
import RNRsaSigner from 'react-native-rsa-signer';


// drop the current key pair for alias, generate a new pair, store it in the key chain and return the PEM string.
let pubKeyPem: Promise<String> = RNRsaSigner.regenerateKey(alias);

// retrieve POM string of the public key that is stored in the key chain with the provided alias
let pubKeyPem: Promise<String> = RNRsaSigner.getPublicKey(alias);

// sign a text with the private key that is stored in the key chain with the provided alias 
let signature: Promise<String> = RNRsaSigner.sign(alias, "my text to sign");

```

### Validate signature in Java/Scala
```scala
// register BouncyCastle JCE provider
java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())

// read user's key, e.g. stored in database, provided by user during sign up
val key: IntputStream = ???
val factory = java.security.KeyFactory.getInstance("RSA", "BC")
val pr = new org.bouncycastle.util.io.pem.PemReader(new java.io.InputStreamReader(key))
val pubKeySpec = new java.security.spec.X509EncodedKeySpec(pr.readPemObject().getContent())
val publicKey = factory.generatePublic(pubKeySpec).asInstanceOf[java.security.interfaces.RSAPublicKey]

// validate message
val message: byte[] = ???
val signature: byte[] = ???

val sig = java.security.Signature.getInstance("SHA256withRSA");
sig.initVerify(publicKey);
sig.update(message);
val isValid = sig.verify(signature)
```
