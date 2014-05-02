#!/usr/bin/env bash
eval $mydir

cp lackey.icns ~/"Library/Keyboard Layouts/"
make_keylayout.js

make_krmb_xml.js alice.lackey > ~/"Library/Application Support/KeyRemap4MacBook/private.xml"
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" reloadxml
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" enable lackey