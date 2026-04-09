#!/bin/sh

Z3_DIR="$(pwd)/baselines/accessanalyzer-reimpl/lib/z3-4.14.1/bin"
EXPECTED_VERSION="Z3 version 4.14.1 - 64 bit"

sudo cp "$Z3_DIR/libz3.so" "$Z3_DIR/libz3java.so" /usr/lib/
sudo cp "$Z3_DIR/z3" /usr/bin/

if [ ! -d "$HOME/.local/bin" ]; then
	sudo mkdir -p "$HOME/.local/bin"
fi
sudo cp "$Z3_DIR/z3" "$HOME/.local/bin/"

sudo ldconfig

echo "Copied Z3 files to:"
echo "- /usr/lib"
echo "- /usr/bin"
echo "- $HOME/.local/bin"

version_output="$(z3 -version 2>/dev/null | head -n1)"

if [ "$version_output" = "$EXPECTED_VERSION" ]; then
	echo "$version_output"
 echo "Z3 installation is correct."
else
 echo "Z3 version check failed."
	echo "Expected: $EXPECTED_VERSION"
	echo "Actual: ${version_output:-<empty>}"
	exit 1
fi