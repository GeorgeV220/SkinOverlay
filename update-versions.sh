find ./README.md -type f -exec sed -E -i "s/(<)?(version|small|:)([:' >]+)?([0-9]\.[0-9]\.[0-9](-[a-z0-9]+\.?[0-9]*)?)(['<]?)/\1\2\3$1\6/g" {} \;
