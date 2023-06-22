declare function regenerateKey(alias: string): Promise<string>;

declare function getPublicKey(alias: string): Promise<string>;

declare function sign(alias: string, data: string): Promise<string>;

declare const RsaSigner: {
  regenerateKey: typeof regenerateKey;
  getPublicKey: typeof getPublicKey;
  sign: typeof sign;
};

export default RsaSigner;
