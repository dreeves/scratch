export PATH="/usr/local/bin:$PATH:."
export PATH="$PATH:$HOME/.rvm/bin"; [[ -s "$HOME/.rvm/scripts/rvm" ]] && . "$HOME/.rvm/scripts/rvm" # load RVM

pause() { read -p 'Press [Enter] to continue . . .'; }; export -f pause
export mydir='cd $(dirname "${BASH_SOURCE[0]}")'
#slash_fwd()  { echo $(echo "$1" | sed 's/\\/\//g'); }; export -f slash_fwd
#slash_back() { echo $(echo "$1" | sed 's/\//\\/g'); }; export -f slash_back
f() { open .; }
x() { exit; }
sb() { "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl" $@; }
ex() { chmod -R 755 "$1" &>/dev/null; }

export PS1='$(pwd)>'
command_not_found_handle() {
	if [ -f "$1.sh" ]; then "$1.sh" ${@:2}
	elif [ -d "$1" ]; then local t=`pwd`; cd "$1"; "run" ${@:2}; cd "$t"
	elif [ "$1" = "$" ]; then "run" ${@:2}
	else echo "-bash: $1: command not found"
	fi
}