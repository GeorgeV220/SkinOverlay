#!/bin/bash

NEW_VERSION="$1"
if [ -z "$NEW_VERSION" ]; then
  echo "Usage: $0 <new-version>"
  exit 1
fi

find ./README.md -type f -exec sed -E -i "s/(<)?(version|small|:)([:' >]+)?([0-9]\.[0-9]\.[0-9](-[a-z0-9]+\.?[0-9]*)?)(['<]?)/\1\2\3$NEW_VERSION\6/g" {} \;


echo "Version updated to $NEW_VERSION."
