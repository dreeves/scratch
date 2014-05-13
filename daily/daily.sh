#!/usr/bin/env bash
#eval $mydir

#chrome="$USERPROFILE/AppData/Local/Google/Chrome/User Data" #windows
chrome=~/"Library/Application Support/Google/Chrome" #osx

cp "$chrome/Default/Bookmarks" ~/ali/history/auto/bookmarks/$(date_i).json
cp ~/Library/Spelling/LocalDictionary ~/ali/history/auto/dictionary/$(date_i).txt

cd ~/ali; git add -A .; git commit -m "automated"

#cd ../gitminder
#./run.sh