#!/usr/bin/env bash
source ~/.bashrc
#eval $mydir

rm ~/ali/**/.DS_STORE

#chrome="$USERPROFILE/AppData/Local/Google/Chrome/User Data" #windows
chrome=~/"Library/Application Support/Google/Chrome" #osx
cp "$chrome/Default/Bookmarks" ~/ali/history/auto/bookmarks/$(date_i).json

cp ~/Library/Spelling/LocalDictionary ~/ali/history/auto/dictionary/$(date_i).txt

ls -Alo  /Applications > ~/"ali/history/auto/ls/$(date_i) :Applications.txt"
ls -AloR ~/ali         > ~/"ali/history/auto/ls/$(date_i) ~:ali.txt"
ls -AloR ~/Desktop     > ~/"ali/history/auto/ls/$(date_i) ~:Desktop.txt"
ls -AloR ~/Downloads   > ~/"ali/history/auto/ls/$(date_i) ~:Downloads.txt"

cd ~/ali; git commit -a -m "automated"

#cd ../gitminder; ./run.sh