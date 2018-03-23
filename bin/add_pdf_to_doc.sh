#!/bin/bash

SOURCE=$1
TARGET=$2
SOURCEPNG=${SOURCE%.*}".png"

echo "Converting"
echo "  $SOURCE"
echo "to"
echo "  $SOURCEPNG"
echo "and moving it to"
echo "  $TARGET"

pdf2png "$SOURCE"
mv "$SOURCEPNG" "$TARGET"
