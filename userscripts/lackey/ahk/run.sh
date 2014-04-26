#!/usr/bin/env bash
eval $mydir

t="bin/ahk.ahk"
u="bin/temp"

cp global_keys.ahk.c "$t"
perl -p -i -e 's/\{/{NEWLINE /g' "$t"
perl -p -i -e 's/\}/ NEWLINE}/g' "$t"
perl -p -i -e 's/(?<!\\)‹/{/g' "$t"
perl -p -i -e 's/(?<!\\)›/}/g' "$t"
perl -p -i -e 's/\\‹/‹/g' "$t"
perl -p -i -e 's/\\›/›/g' "$t"
perl -p -i -e 's/ ?; ?/ NEWLINE /g' "$t"
perl -p -i -e 's/###/HASH/g' "$t"
cpp -P "$t" -o "$u" ; cp "$u" "$t"
perl -p -i -e 's/ ?NEWLINE ?/\n/g' "$t"
perl -p -i -e 's/HASH/#/g' "$t"
perl -p -i -e 's/COMMA/,/g' "$t"
perl -p -i -e 's/BSEMICOLON/`;/g' "$t"
perl -p -i -e 's/SEMICOLON/;/g' "$t"
perl -p -i -e "s/QUOTE/'/g" "$t"
perl -p -i -e 's/\\\\/\\/g' "$t"
python 'unicode_filter.py' "$t"

#! should probably be bash
hstart64 //NOCONSOLE "AutoHotkey bin/ahk.ahk"