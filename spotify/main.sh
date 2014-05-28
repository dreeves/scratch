#!/usr/bin/env bash
eval $mydir

main.js $@
rm -r cache
rm -r settings