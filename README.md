<br/>

<div align="center">
    <a href="https://simonecorazza.github.io/MacroKeys/">
        <img src="https://simonecorazza.github.io/MacroKeys/img/macrokeys-logo.svg" alt="MacroKeys Logo" width="200" height="100">
    </a>
</div>

# MacroKeys Editor &middot; [![Build Status](https://travis-ci.org/SimoneCorazza/MacroKeysEditor.svg?branch=master)](https://travis-ci.org/SimoneCorazza/MacroKeysEditor) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.macrokeys%3Amacro-key-editor&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.macrokeys%3Amacro-key-editor)

Graphical editor for the keyboard of the app MacroKeys.

## Features

- Create a virtual keyboard to use in your [Android Device](https://github.com/SimoneCorazza/MacroKeysAndroid) to command your desktop PC using the [desktop server app](https://github.com/SimoneCorazza/MacroKeysServer)
- Pixel perfect accuracy: the keyboard you will make will perfectly match your device screen size
- Undo/Redo functionality

## Getting Started

### Prerequisites

This project is built with [maven](https://maven.apache.org/) so you need to install the [maven cli](https://maven.apache.org/download.cgi) to run it.

### Executing

To execute simply use

```
mvn exec:java
```

### Packaging

```
mvn package
```

The output `.jar` is in the `./target` directory.

## License

This project is licensed under the GPL License see the [LICENSE.md](LICENSE.md) file for details
