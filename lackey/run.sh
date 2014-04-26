#!/usr/bin/env bash
eval $mydir

cp private.xml ~/"Library/Application Support/KeyRemap4MacBook/private.xml"
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" reloadxml
"/Applications/KeyRemap4MacBook.app/Contents/Applications/KeyRemap4MacBook_cli.app/Contents/MacOS/KeyRemap4MacBook_cli" enable lackey