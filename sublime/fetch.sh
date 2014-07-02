Pack=~/"Library/Application Support/Sublime Text 3/Packages"

rm -R Packages; mkdir Packages

cp -R "$Pack/Alpha" "Packages/Alpha"
cp -R "$Pack/User"  "Packages/User"

rm -R "Packages/User/Package Control.ca-bundle" &>/dev/null
rm -R "Packages/User/Package Control.ca-list"   &>/dev/null
rm -R "Packages/User/Package Control.last-run"  &>/dev/null
rm -R "Packages/User/Package Control.cache"     &>/dev/null