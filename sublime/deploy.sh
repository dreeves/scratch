Pack=~/"Library/Application Support/Sublime Text 3/Packages"

generate.js

mkdir -p tmp/User
cp -R "Packages/User/Package Control.ca-bundle" tmp/User
cp -R "Packages/User/Package Control.ca-list"   tmp/User
cp -R "Packages/User/Package Control.last-run"  tmp/User
cp -R "Packages/User/Package Control.cache"     tmp/User

rm -R "$Pack/Alpha"
rm -R "$Pack/User"

cp -R Packages/ "$Pack"

cp -R tmp/User "$Pack/User"
rm -R tmp