Pack=~/"Library/Application Support/Sublime Text 3/Packages"

rm -R Packages; mkdir Packages

cp -R "$Pack/Alpha" "Packages/Alpha"
cp -R "$Pack/User"  "Packages/User"

rm -R "Packages/User/Package Control.ca-bundle"
rm -R "Packages/User/Package Control.ca-list"
rm -R "Packages/User/Package Control.last-run"
rm -R "Packages/User/Package Control.cache"