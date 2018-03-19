# react-native-rsa-signer

## Getting started

`$ npm install react-native-rsa-signer --save`

### Mostly automatic installation

`$ react-native link react-native-rsa-signer`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-rsa-signer` and add `RNRsaSigner.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNRsaSigner.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
- Add `import co.movio.rsasigner.RNRsaSignerPackage;` to the imports at the top of the file
- Add `new RNRsaSignerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
```
include ':react-native-rsa-signer'
project(':react-native-rsa-signer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-rsa-signer
android')
```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
```
compile project(':react-native-rsa-signer')
```

## Usage
```javascript
import RNRsaSigner from 'react-native-rsa-signer';

// TODO: What to do with the module?
RNRsaSigner;
```
