#!/usr/bin/env bash
eval $mydir

files="`pwd`/files.js"

mount e:/ /e
ls /e &> /dev/null; if [ $? -ne 0 ]; then echo "no drive found. exiting."; exit 1; fi

dest="/e/mirror"

cd "$USERPROFILE"; rsync "ali" "$dest" -a --exclude "ali/code/#libs/python 2.7" --exclude "ali/history/screens" --exclude "ali/code/github" --update
cd "$USERPROFILE/AppData/Roaming/Sublime Text 3/Packages"; 7z a "$dest/misc/sublime_text.7z" > /dev/null
cd "$USERPROFILE/AppData/Roaming/.minecraft/saves"; 7z a "$dest/misc/minecraft_saves.7z" > /dev/null
cd "$USERPROFILE/AppData/Local/Zachtronics Industries/SpaceChem/save"; 7z a "$dest/misc/spacechem_save.7z" > /dev/null
cd "/c/Program Files/Paint.NET/Effects"; 7z a "$dest/misc/paint_net_effects.7z" > /dev/null
cd "$USERPROFILE/desktop"; eval 7z a "desktop.7z" $(node $files) > /dev/null; mv "desktop.7z" "$dest/misc/desktop.7z"

pause

# data outside of the automated backup:
#	files inside folders on desktop
#	skype logs
#	steam, but meh
#	spotify playlists
#	other stuff ?
#	private data in github repos ignored by gitignore