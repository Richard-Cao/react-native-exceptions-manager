package com.richardcao.exceptionsmanager.react;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.JavascriptException;
import com.facebook.react.common.ReactConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caolicheng on 2016/9/29.
 */

public class ExceptionsManagerModule extends ReactContextBaseJavaModule {

    private final Context context;
    static private final Pattern mJsModuleIdPattern = Pattern.compile("(?:^|[/\\\\])(\\d+\\.js)$");

    public ExceptionsManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RKExceptionsManager";
    }

    @Override
    public boolean canOverrideExistingModule() {
        return true;
    }

    // If the file name of a stack frame is numeric (+ ".js"), we assume it's a lazily injected module
    // coming from a "random access bundle". We are using special source maps for these bundles, so
    // that we can symbolicate stack traces for multiple injected files with a single source map.
    // We have to include the module id in the stack for that, though. The ".js" suffix is kept to
    // avoid ambiguities between "module-id:line" and "line:column".
    static private String stackFrameToModuleId(ReadableMap frame) {
        if (frame.hasKey("file") &&
                !frame.isNull("file") &&
                frame.getType("file") == ReadableType.String) {
            final Matcher matcher = mJsModuleIdPattern.matcher(frame.getString("file"));
            if (matcher.find()) {
                return matcher.group(1) + ":";
            }
        }
        return "";
    }

    private String stackTraceToString(String message, ReadableArray stack) {
        StringBuilder stringBuilder = new StringBuilder(message).append(", stack:\n");
        for (int i = 0; i < stack.size(); i++) {
            ReadableMap frame = stack.getMap(i);
            stringBuilder
                    .append(frame.getString("methodName"))
                    .append("@")
                    .append(stackFrameToModuleId(frame))
                    .append(frame.getInt("lineNumber"));
            if (frame.hasKey("column") &&
                    !frame.isNull("column") &&
                    frame.getType("column") == ReadableType.Number) {
                stringBuilder
                        .append(":")
                        .append(frame.getInt("column"));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @ReactMethod
    public void reportFatalException(String title, ReadableArray details, int exceptionId) {
        showOrThrowError(title, details, exceptionId);
    }

    private void showOrThrowError(String title, ReadableArray details, int exceptionId) {
        Intent intent = new Intent();
        intent.setAction("com.richardcao.android.REACT_NATIVE_CRASH_REPORT_ACTION");
        intent.putExtra("JavascriptException", new JavascriptException(stackTraceToString(title, details)));
        context.sendBroadcast(intent);
    }

    @ReactMethod
    public void reportSoftException(String title, ReadableArray details, int exceptionId) {
        FLog.e(ReactConstants.TAG, stackTraceToString(title, details));
    }

    @ReactMethod
    public void updateExceptionMessage(String title, ReadableArray details, int exceptionId) {

    }

    @ReactMethod
    public void dismissRedbox() {

    }
}
