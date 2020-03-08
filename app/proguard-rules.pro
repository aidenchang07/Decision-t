# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
# 抛出異常時保留檔名與行數
-keepattributes SourceFile, LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
# 將文件來源重命名為“SourceFile”字符串
-renamesourcefileattribute SourceFile

# 不做預校驗，加速建置速度
-dontpreverify

# 保持泛型不被混淆
-keepattributes Signature

# 保持內部類不被混淆
-keepattributes InnerClasses