#!/usr/bin/env bash

npm version patch
npm publish .
npm install zeta0 -g
ln -s /usr/local/lib/node_modules/zeta0/ζ₀ /usr/local/bin &>/dev/null