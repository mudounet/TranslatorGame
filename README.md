# Translator Game

This little game is aimed at improving translation, by filling holes, until correct return is given.

## Installation

Currently, installation is not fully automatized. So, it is necessary to copy lessons.xml (copy is present into directory `translatorGame\src\main\assets`) into your Android device, in directory created by the game, in my case `TranslatorGame`, at the root of either internal or external storage.

Of course, it doesn't work unless `android.permission.WRITE_EXTERNAL_STORAGE` is given.

## Note for developers

### Signing application

Due to inclusion of #3, it is necessary to add following `gradle.properties` to `~/.gradle` path:
```
    RELEASE_STORE_FILE={path to your keystore, relative to gradle.build file calling these variables}
    RELEASE_STORE_PASSWORD=*****
    RELEASE_KEY_ALIAS=*****
    RELEASE_KEY_PASSWORD=*****
```


in order to be able to sign this application.

## History

### V_1.0 :

Basic application with most features already available. Installation is still a rough process however.