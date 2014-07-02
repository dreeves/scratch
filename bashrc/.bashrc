export PATH="/usr/local/bin:$PATH:$HOME/ali/github/scratch/ζ₀:."
export PATH="$PATH:$HOME/.rvm/bin"; [[ -s "$HOME/.rvm/scripts/rvm" ]] && . "$HOME/.rvm/scripts/rvm" # load RVM

pause() { read -p 'Press [Enter] to continue . . .'; }; export -f pause
export mydir='cd $(dirname "${BASH_SOURCE[0]}")'
date_i() { date -u +"%Y-%m-%dT%H:%M:%SZ"; }; export -f date_i
ex() { chmod -R 755 "$1" &>/dev/null; }; export -f ex
exr() { ex "$1"; "$@"; }; export -f exr

f() { open .; }
x() { exit; }
sb() { "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl" $@; }
ar() { tar -cf "$1.tar" "$1"; xz "$1.tar"; }

export PS1='$(pwd)>'

shopt -s globstar

command_not_found_handle() {
	if [ -f "$1.sh" ]; then exr "$1.sh" ${@:2}
	elif [ -d "$1" ]; then local t=`pwd`; cd "$1"; exr "run" ${@:2}; cd "$t"
	elif [ "$1" = "$" ]; then
		if   [ -f "run"           ]; then exr "run"           ${@:2}
		elif [ -f "run.sh"        ]; then exr "run.sh"        ${@:2}
		elif [ -f "run.js"        ]; then exr "run.js"        ${@:2}
		elif [ -f "main"          ]; then exr "main"          ${@:2}
		elif [ -f "main.sh"       ]; then exr "main.sh"       ${@:2}
		elif [ -f "main.js"       ]; then exr "main.js"       ${@:2}
		elif [ -f "${PWD##*/}"    ]; then exr "${PWD##*/}"    ${@:2}
		elif [ -f "${PWD##*/}.sh" ]; then exr "${PWD##*/}.sh" ${@:2}
		elif [ -f "${PWD##*/}.js" ]; then exr "${PWD##*/}.js" ${@:2}
		else echo "-bash: $1: command not found"
		fi
	else echo "-bash: $1: command not found"
	fi
	}