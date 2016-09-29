# react-native-exceptions-manager

[![GitHub license][license-image]][license-url]
[![NPM version][npm-image]][npm-url]
[![Dependency Status][david-image]][david-url]
[![Downloads][downloads-image]][npm-url]

React-Native JS Crash Reporter In Release Version(**Do not trigger native crash**).

# Requirement

>* **react-native** version >= 0.33

# Linking

## Android

- Add following lines into ```android/settings.gradle```

```
include ':react-native-exceptions-manager'
project(':react-native-exceptions-manager').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-exceptions-manager/android')
```

- Add following lines into your ```android/app/build.gradle``` in section ```dependencies```

```
compile project(':react-native-exceptions-manager')
```

- Add following lines into ```MainApplication.java```

```
import com.richardcao.exceptionsmanager.react.ExceptionsManager;
...

@Override
protected List<ReactPackage> getPackages() {
    List<ReactPackage> packages = Arrays.asList(
            new MainReactPackage(),
            ...);
    ArrayList<ReactPackage> packageList = new ArrayList<>(packages);
    if (!BuildConfig.DEBUG) {
        packageList.add(new ExceptionsManager());
    }
    return packageList;
}
```

- Create a class named ```ReactNativeJSCrashReceiver``` in it. This is needed to get js crash message from `react-native-exceptions-manager`.

```
public class ReactNativeJSCrashReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.richardcao.android.REACT_NATIVE_CRASH_REPORT_ACTION")) {
            Throwable throwable = (Throwable) intent.getSerializableExtra("JavascriptException");
            ...(handler or report js crash operate)
        }
    }
}
```

- Add ```ReactNativeJSCrashReceiver``` declare in your ```AndroidManifest.xml```

```
<application
    android:name=".MainApplication"
    android:allowBackup="true"
    ...>
    ...
    <receiver android:name=".ReactNativeJSCrashReceiver">
        <intent-filter>
            <action android:name="com.richardcao.android.REACT_NATIVE_CRASH_REPORT_ACTION" />
        </intent-filter>
    </receiver>
</application>
```

## iOS 

*//TODO*

## Who Use It
- [reading: iReading App Write In React-Native][reading-url]


### MIT Licensed


[license-image]: https://img.shields.io/badge/license-MIT-blue.svg
[license-url]: https://raw.githubusercontent.com/Richard-Cao/react-native-exceptions-manager/master/LICENSE
[npm-image]: https://img.shields.io/npm/v/react-native-exceptions-manager.svg?style=flat-square
[npm-url]: https://npmjs.org/package/react-native-exceptions-manager
[david-image]: http://img.shields.io/david/Richard-Cao/react-native-wechat.svg?style=flat-square
[david-url]: https://david-dm.org/Richard-Cao/react-native-wechat
[downloads-image]: http://img.shields.io/npm/dm/react-native-exceptions-manager.svg?style=flat-square
[reading-url]: https://github.com/attentiveness/reading