#!/bin/bash

# US Federal Income Tax Calculator - Compile & Run Script

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"
JAVAFX_DIR="$SCRIPT_DIR/javafx-sdk"

# --- Ensure JavaFX SDK is available ---
if [ ! -d "$JAVAFX_DIR" ] || [ -z "$(ls "$JAVAFX_DIR"/lib/*.jar 2>/dev/null)" ]; then
    echo "JavaFX SDK not found. Downloading..."

    # Detect architecture
    ARCH=$(uname -m)
    if [ "$ARCH" = "arm64" ]; then
        PLATFORM="osx-aarch64"
    else
        PLATFORM="osx-x64"
    fi

    JAVAFX_VERSION="21.0.2"
    URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_${PLATFORM}_bin-sdk.zip"

    TMPZIP="$SCRIPT_DIR/javafx-sdk.zip"
    echo "Downloading from $URL ..."
    curl -L -o "$TMPZIP" "$URL"

    if [ $? -ne 0 ]; then
        echo "Failed to download JavaFX SDK."
        rm -f "$TMPZIP"
        exit 1
    fi

    echo "Extracting..."
    unzip -qo "$TMPZIP" -d "$SCRIPT_DIR"
    rm -f "$TMPZIP"

    # The zip extracts to javafx-sdk-<version>; rename to javafx-sdk
    EXTRACTED=$(ls -d "$SCRIPT_DIR"/javafx-sdk-* 2>/dev/null | head -1)
    if [ -n "$EXTRACTED" ] && [ "$EXTRACTED" != "$JAVAFX_DIR" ]; then
        rm -rf "$JAVAFX_DIR"
        mv "$EXTRACTED" "$JAVAFX_DIR"
    fi

    if [ ! -d "$JAVAFX_DIR/lib" ]; then
        echo "JavaFX SDK extraction failed."
        exit 1
    fi

    echo "JavaFX SDK ready."
fi

JAVAFX_MODS="$JAVAFX_DIR/lib"

# Clean and create output directory
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Copy CSS styles to output
mkdir -p "$OUT_DIR/taxcalculator/styles"
cp "$SRC_DIR"/taxcalculator/styles/*.css "$OUT_DIR/taxcalculator/styles/" 2>/dev/null

echo "Compiling..."
javac --module-path "$JAVAFX_MODS" --add-modules javafx.controls,javafx.media \
    -d "$OUT_DIR" "$SRC_DIR"/taxcalculator/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Running Tax Calculator..."
java --module-path "$JAVAFX_MODS" --add-modules javafx.controls,javafx.media \
    --enable-native-access=javafx.graphics,javafx.media \
    -Dapp.dir="$SCRIPT_DIR" \
    -cp "$OUT_DIR" taxcalculator.Main
