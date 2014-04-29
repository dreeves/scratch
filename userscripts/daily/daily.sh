#!/usr/bin/env bash
eval $mydir

#chrome="$USERPROFILE/AppData/Local/Google/Chrome/User Data" #windows
chrome=~/"Library/Application Support/Google/Chrome" #osx

cp "$chrome/Default/Bookmarks" ~/"ali/history/bookmarks/`date \"+%Y-%m-%d %H.%M\"`"

cd ~/ali; git add -A .; git commit -m "automated"

#cd ../gitminder
#./run.sh