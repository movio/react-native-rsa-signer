declare function getPublicKey(alias: string): Promise<string>;

declare function regenerateKey(alias: string): Promise<string>;

declare function sign(alias: string, data: string): Promise<string>;

declare const RsaSigner: {
  /**
   * Returns the Public Key assotiated with the alias in X.509 PEM format.
   * Will return null if no key is found for this alias.
   */
  getPublicKey: typeof getPublicKey;

  /**
   * Deletes the key pair assiciated with the give alias (if existed) and
   * generates a new key pair.
   *
   * @param alias - the name for the key pair.
   *   You can have multiple key pairs stored. To retrieve one, you need to
   *   pass the same alias.
   *
   * @return Public Key in X.506 PEM format.
   */
  regenerateKey: typeof regenerateKey;

  /**
   * Signs `data` with the private key assosiated with alias.
   * Will throw error if there's no key with this alias.
   *
   * @return Signature as a string.
   */
  sign: typeof sign;
};

export default RsaSigner;
