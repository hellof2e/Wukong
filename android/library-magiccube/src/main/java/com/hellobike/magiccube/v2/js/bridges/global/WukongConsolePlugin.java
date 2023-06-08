package com.hellobike.magiccube.v2.js.bridges.global;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.hellobike.magiccube.v2.configs.Constants;
import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
import com.quickjs.Plugin;

import java.util.HashMap;
import java.util.Map;

public class WukongConsolePlugin extends Plugin {

    public void setup(JSContext context) {
        JSObject console = context.addJavascriptInterface(this, "console");
        console.registerJavaMethod((receiver, args) -> {
            if (!args.getBoolean(0)) {
                this.error(args.getString(1));
            }

        }, "assert");
    }

    public void close(JSContext context) {
    }

    @JavascriptInterface
    public final void log(String msg) {
        this.println(3, msg);
    }

    @JavascriptInterface
    public final void info(String msg) {
        this.println(4, msg);
    }

    @JavascriptInterface
    public final void error(String msg) {
        this.println(6, msg);
    }

    @JavascriptInterface
    public final void warn(String msg) {
        this.println(5, msg);
    }

    public void println(int priority, String msg) {
        Log.println(priority, Constants.TAG, Thread.currentThread().getName() + " >> " + msg);
    }

    @JavascriptInterface
    public final void table(JSObject obj) {
        if (obj instanceof JSArray) {
            this.log(((JSArray) obj).toJSONArray().toString());
        } else if (obj != null) {
            this.log(obj.toJSONObject().toString());
        }
    }
}
