# react-native-rsa-signer

This package generates an RSA key and stores it in the OS vault. It can be used to sign payloads, such as a JWT token.

A common use case is for user authentication. The private key is stored in the OS vault and is secure. The public key (which is in X.509 PEM format) can be sent to the server/back-end. The app then creates a payload (such as a JWT token) and signs it with the private key. The server can validate the signature using the public key. Because only this specific device and app have access to the private key, the server can trust that the payload came from the user and no one else.

This was a simplified explanation of the authentication scenario. Take care when implementing security features!

## Technical info

The length of the RSA key is 3072. Currently, the package doesn't allow you to adjust it.

It stores the key pair in:

* iOS: Keychain
* Android (API level >= 18): Keystore
* Android (API level <= 17): SQLite

The signing algorithm is `SHA256withRSA`.

## Stability and maintenance

This package has been in use in production by Movio since 2018. So, it's quite stable. We may be a bit opinionated about what features we want to add or what PRs we accept, as we want to keep the stability in our products.

## Installation

It requires React Native >= 0.71.0

Simply install it using your package manager. For `npm`:

```
  npm install react-native-rsa-signer
```

For iOS, you need to `$ cd ios && pod install`.

This plugin uses Swift. If you don't use Swift in your iOS project, you need to add a `empty.swift` file in your project to activate the Swift compiling.

* open your project in Xcode
* right click your project's folder in the Project Navigator, select `New File...`
* select the `Swift File` template, click `Next`
* set file name to `empty.swift`, make sure your project is enabled in `Targets`, click `Create`
* remove all contents of file `empty.swift`
* in the `Build Settings` of your project, select `Always Embed Swift Standard Libraries` and set `Swift Language Version = Swift 4.0`

## API

`regenerateKey(alias: string): Promise<string>`

Deletes the old key pair (if existed) and generates a new key pair. Returns the public key in `X.506 PEM` format.

`alias` is the name for the key pair. You can have multiple key pairs stored. To retrieve one, you need to pass the same `alias`.

`getPublicKey(alias: string): Promise<string>`

Returns the Public Key assotiated with the `alias` in `X.509 PEM` format. If no key pair is existed for this `alias`, it automatically generates and stores a new one.

`sign(alias: string, data: string): Promise<string>`

Signs `data` with the private key assosiated with `alias`. Returns the signature in `base64` format.

## Usage

### Sign a message
```javascript
import RNRsaSigner from 'react-native-rsa-signer';

const alias = 'login-key'

// drop the current key pair for alias, generate a new pair, store it in the key chain and return the PEM string.
const pubKeyPem: Promise<string> = RNRsaSigner.regenerateKey(alias)

// Or you can retrieve and existing key previously stored with the same alias.
// const pubKeyPem: Promise<string> = RNRsaSigner.getPublicKey(alias);

// sign a text with the private key that is stored in the key chain with the provided alias.
const signature: Promise<string> = RNRsaSigner.sign(alias, 'my text to sign')

```

### Validate signature in Java/Scala

A sample code of how you can validate the signature using `Bouncy Castle` lib in Scala.

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

## Development Build

`npm install` then open `ios/RNRsaSigner.xcodeproj` in Xcode.

### Common build issues

Build problems to do with `Build input file cannot be found ... third-party/double-conversion-* :
Seems to happen for Xcode 10+: https://github.com/facebook/react-native/issues/21168

Fix by:

``` shell
$ cd node_modules/react-native/scripts && ./ios-install-third-party.sh && cd ../../../
$ cd node_modules/react-native/third-party/glog-0.3.4/ && ../../scripts/ios-configure-glog.sh && cd ../../../../
```
