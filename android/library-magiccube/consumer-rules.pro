# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature

#保留jar包中的第三方包不被混淆
-ignorewarnings
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

#保护注解
-keepattributes Signature,*Annotation*,*JavascriptInterface*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep public class * extends android.app.backup.BackupAgentHelper               # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference                      # 保持哪些类不被混淆

#保护类方法
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep public class * extends androidx.versionedparcelable.VersionedParcelable {
  <init>();
}
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    *;
  public static final android.os.Parcelable$Creator *;
}

-keep public class * implements java.io.Serializable{
public protected private *;
}
#保留自定义View的get和set方法
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
# 指定的类成员被保留
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
 }
 #保留R文件的静态成员
 -keepclassmembers class **.R$* {
     public static <fields>;
 }
-keep class **.R$* {*;}

-keep public class * extends android.view.View

-keep class com.hellobike.magiccube.v2.net.request.*{*;}
-keep class com.hellobike.magiccube.v2.js.bridges.global.*{*;}
-keep class com.hellobike.magiccube.v2.js.bridges.model.*{*;}

-keep class com.quickjs.** {*; }
-keep class com.facebook.** {*; }
