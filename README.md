[Try web demo of latest master branch here](https://benmclean.github.io/Planet-Generator/html/build/dist/)

== Building and running ==

I am using IntelliJ IDEA on Windows 10.

Gradle for Android wants a local.properties in the project's root directory containing the path to your SDK, which for me was, "sdk.dir=C:\\\\Users\\\\MY_NAME_HERE\\\\AppData\\\\Local\\\\Android\\\\sdk" (double backslashes)

To run on desktop, you need a run configuration with main class "DesktopLauncher", the working directory of "android\assets" and the classpath of module "desktop". The ArtPacker utility is the same way except with main class "ArtPacker". (this utility is only for desktop)

To build for HTML5, run "gradlew.bat html:dist"
