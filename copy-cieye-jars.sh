#! /bin/sh

CIEYE_HOME=../ci-eye
CIEYE_PLUGIN=.
ARTIFACT_NAME="ci-eye-1.0.0-grundlefleck"

cp $CIEYE_HOME/build/${ARTIFACT_NAME}.jar $CIEYE_PLUGIN/vendor/lib
cp $CIEYE_HOME/build/${ARTIFACT_NAME}-sources.jar $CIEYE_PLUGIN/vendor/src

