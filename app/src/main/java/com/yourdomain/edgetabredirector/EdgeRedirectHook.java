package com.example.edgetabredirector;

import android.content.Intent;
import android.net.Uri;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EdgeRedirectHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.microsoft.emmx")) return;

        XposedBridge.log("EdgeTabRedirector loaded in " + lpparam.packageName);

        try {
            Class<?> cls = lpparam.classLoader.loadClass("android.content.Intent");
            XposedBridge.hookAllMethods(cls, "getDataString", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String result = (String) param.getResult();
                    if (result != null && result.contains("chrome-native://newtab")) {
                        Intent intent = (Intent) param.thisObject;
                        intent.setData(Uri.parse("chrome://newtab/"));
                        param.setResult("chrome://newtab/");
                        XposedBridge.log("Redirected to chrome://newtab/");
                    }
                }
            });
        } catch (Exception e) {
            XposedBridge.log("EdgeTabRedirector Error: " + e.getMessage());
        }
    }
}
