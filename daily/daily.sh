#!/usr/bin/env bash
source ~/.bashrc
#eval $mydir

#-------- history/auto --------

#chrome="$USERPROFILE/AppData/Local/Google/Chrome/User Data" #windows
chrome=~/"Library/Application Support/Google/Chrome" #osx
cp "$chrome/Default/Bookmarks" ~/ali/history/auto/bookmarks/$(date_i).json

cp ~/Library/Spelling/LocalDictionary ~/ali/history/auto/dictionary/$(date_i).txt

cd ~/ali/github/scratch/spotiman; main.js backup

ls -Alo  /Applications > ~/"ali/history/auto/ls/$(date_i) :Applications.txt"
ls -AloR ~/ali         > ~/"ali/history/auto/ls/$(date_i) ~:ali.txt"
ls -AloR ~/Desktop     > ~/"ali/history/auto/ls/$(date_i) ~:Desktop.txt"
ls -AloR ~/Downloads   > ~/"ali/history/auto/ls/$(date_i) ~:Downloads.txt"

#------------ other -----------

rm ~/ali/**/.DS_STORE

cd ~/ali; git commit -a -m "automated"

#cd ../gitminder; ./run.sh