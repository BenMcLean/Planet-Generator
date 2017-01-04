[Try web demo of latest master branch here](https://benmclean.github.io/Planet-Generator/html/build/dist/)

# Building and running

I am using IntelliJ IDEA on Windows 10.

Gradle wants a local.properties in the project's root directory containing the path to your Android SDK, which for me was, "sdk.dir=C:\\\\Users\\\\MY_NAME_HERE\\\\AppData\\\\Local\\\\Android\\\\sdk" (double backslashes)

To run on desktop, you need a run configuration with main class "DesktopLauncher", the working directory of "android\assets" and the classpath of module "desktop". The ArtPacker utility is the same way except with main class "ArtPacker". (this utility is only for desktop)

To build for HTML5, run "gradlew.bat html:dist"

# Stuff I'm using

## Code
* [libGDX](https://libgdx.badlogicgames.com/)
* [libGDX-utils](https://bitbucket.org/dermetfan/libgdx-utils/)
* [SquidLib](https://github.com/SquidPony/SquidLib)
* [Joise](https://github.com/SudoPlayGames/Joise)

## Art
* http://www.squaregear.net/fonts/tiny.shtml
* http://opengameart.org/content/blowhard-2-blow-harder
* http://opengameart.org/content/blowhard-chronicles
* http://opengameart.org/content/27-bricks
* http://opengameart.org/content/16x16-emotes-for-rpgs-and-digital-pets

Looking into also adapting tiles from these sets:
* http://opengameart.org/content/16x16-8-bit-rpg-character-set
* http://opengameart.org/content/nes-style-rpg-characters
* http://opengameart.org/content/more-nes-style-rpg-characters
