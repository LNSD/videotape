#!/bin/bash -e

STYLESHEET_NAME=asciidoctor

if [ ! -z $1 ]; then
  STYLESHEET_NAME=$1
fi

asciidoctor -a linkcss \
            -a stylesheet=${STYLESHEET_NAME}.css \
            -a stylesdir=./stylesheets \
            -a source-highlighter=highlightjs \
            sample.adoc

compass watch sass/videotape.scss
