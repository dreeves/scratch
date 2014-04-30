#!/usr/bin/env bash
eval $mydir

cp lackey.icns ~/"Library/Keyboard Layouts/"
make_keylayout.js

cp private.xml ~/"Library/Application Support/KeyRemap4MacBook/"
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" reloadxml
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" enable lackey