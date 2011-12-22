#!/bin/sh

# You can access your bundled files at the following paths:
#
# "$1/Contents/Resources/bin"
# "$1/Contents/Resources/conf"
# "$1/Contents/Resources/lib"
# "$1/Contents/Resources/misc"
#
#
export TWINKLEBERRY_HOME=`echo $0 | sed s#/script##`
echo $TWINKLEBERRY_HOME
java -classpath "$TWINKLEBERRY_HOME/lib/*" -Dtwinkleberry.home=$TWINKLEBERRY_HOME twinkleberry.Main "$@" &
