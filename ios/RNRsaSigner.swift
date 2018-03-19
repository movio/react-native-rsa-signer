import Foundation
import Security

@objc(RNRsaSigner)
class RNRsaSigner: NSObject {

  let keySize = 1024

  @objc func getPublicKey(_ alias: String, resolver resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {

    guard !alias.isEmpty
      else { return reject("no_alias", "Alias is empty", nil) }

    if let heimdall = Heimdall(tagPrefix: alias, keySize: keySize) {
      if let publicKeyX509 = heimdall.publicKeyDataX509() {
        let encoded = publicKeyX509.base64EncodedString()

        var result: String = ""
        result.append("-----BEGIN PUBLIC KEY-----\n")
        let charCount = encoded.count
        for (i, c) in encoded.enumerated() {
          result.append(c)
          if (i % 64 == 63 || i+1 == charCount) {
            result.append("\n")
          }
        }
        result.append("-----END PUBLIC KEY-----")
        return resolve(result)
      } else {
        return reject("get_public_key", "Failed to get X.509 public key", nil)
      }
    } else {
      return reject("init_heimdall", "Failed to init Heimdall", nil)
    }
  }

  @objc func regenerateKey(_ alias: String, resolver resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {

    guard !alias.isEmpty
      else { return reject("no_alias", "Alias is empty", nil) }

    if let heimdall = Heimdall(tagPrefix: alias, keySize: keySize) {
      if heimdall.regenerate() {
        return getPublicKey(alias, resolver: resolve, rejecter: reject)
      } else {
        return reject("regenerate_key", "Failed to regenerate key", nil)
      }
    } else {
      return reject("init_heimdall", "Failed to init Heimdall", nil)
    }
  }

  @objc func sign(_ alias: String, data: String, resolver resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {

    guard !alias.isEmpty
      else { return reject("no_alias", "Alias is empty", nil) }

    guard !data.isEmpty
      else { return reject("no_data", "Data is empty", nil) }

    if let heimdall = Heimdall(tagPrefix: alias, keySize: 1024) {
      if let signature = heimdall.sign(data, urlEncode: true) {
        return resolve(signature)
      } else {
        return reject("sign_error", "Failed to sign", nil)
      }
    } else {
      return reject("init_heimdall", "Failed to init Heimdall", nil)
    }
  }
}
