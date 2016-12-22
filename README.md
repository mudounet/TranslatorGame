# Translator Game

This little game is aimed at improving translation, by filling holes, until correct return is given.

## Main features

Directory is created by the game once `android.permission.WRITE_EXTERNAL_STORAGE` is given, in my case `TranslatorGame`, at the root of either internal or external storage. This directory contains all statistics for lessons, so they are not lost when upgrading this application.

Also, a file to debug application is generated at every application startup (translator-game-log.txt).

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

### V_1.1 :

Basic application which can handle several lessons. Also, keyboard is managed correctly, as well as access to `WRITE_EXTERNAL_STORAGE` is asked at startup. There is still anomalies when these rights are not placed correctly.

### V_1.0 :

Basic application with most features already available. Installation is still a rough process however.