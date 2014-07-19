Pack=~/"Library/Application Support/Sublime Text 3/Packages"

generate.js

mkdir -p tmp/User
cp -R "$Pack/User/Package Control.ca-bundle" tmp/User &>/dev/null
cp -R "$Pack/User/Package Control.ca-list"   tmp/User &>/dev/null
cp -R "$Pack/User/Package Control.last-run"  tmp/User &>/dev/null
cp -R "$Pack/User/Package Control.cache"     tmp/User &>/dev/null

rm -R "$Pack/Alpha"
rm -R "$Pack/User"

cp -R Packages/ "$Pack"

cp -R tmp/User "$Pack"
rm -R tmp