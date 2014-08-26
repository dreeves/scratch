#!/usr/bin/env bash
eval $mydir

cp lackey.icns ~/"Library/Keyboard Layouts/"
make_keylayout.js

make_karab.js alice.lackey > ~/"Library/Application Support/Karabiner/private.xml"
/Applications/Karabiner.app/Contents/Library/bin/karabiner reloadxml
/Applications/Karabiner.app/Contents/Library/bin/karabiner enable lackey