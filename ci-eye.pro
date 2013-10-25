-injars build/ci-eye-plugin-poc-in.jar
-outjars build/ci-eye-plugin-poc-out.jar
-libraryjars  <java.home>/lib/rt.jar
-libraryjars  <java.home>/lib/jsse.jar
-libraryjars vendor/buildlib/jsr305-2.0.0.jar

-dontoptimize
-dontobfuscate
-dontwarn sun.misc.Unsafe
-dontwarn org.apache.commons.logging.impl.*
-dontwarn org.apache.http.impl.auth.*

