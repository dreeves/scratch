export PATH="/usr/local/bin:$PATH:."
export PATH="$PATH:$HOME/.rvm/bin"; [[ -s "$HOME/.rvm/scripts/rvm" ]] && . "$HOME/.rvm/scripts/rvm" # load RVM

pause() { read -p 'Press [Enter] to continue . . .'; }; export -f pause
export mydir='cd $(dirname "${BASH_SOURCE[0]}")'
date_i() { date -u +"%Y-%m-%dT%H:%M:%SZ"; }; export -f date_i

f() { open .; }
x() { exit; }
sb() { "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl" $@; }
ex() { chmod -R 755 "$1" &>/dev/null; }
ar() { tar -cf "$1.tar" "$1"; xz "$1.tar"; }

export PS1='$(pwd)>'

shopt -s globstar

command_not_found_handle() {
	if [ -f "$1.sh" ]; then "$1.sh" ${@:2}
	elif [ -d "$1" ]; then local t=`pwd`; cd "$1"; "run" ${@:2}; cd "$t"
	elif [ "$1" = "$" ]; then
		if   [ -f "run"           ]; then ex "run"          ; "run"           ${@:2}
		elif [ -f "run.sh"        ]; then ex "run.sh"       ; "run.sh"        ${@:2}
		elif [ -f "run.js"        ]; then ex "run.js"       ; "run.js"        ${@:2}
		elif [ -f "main"          ]; then ex "main"         ; "main"          ${@:2}
		elif [ -f "main.sh"       ]; then ex "main.sh"      ; "main.sh"       ${@:2}
		elif [ -f "main.js"       ]; then ex "main.js"      ; "main.js"       ${@:2}
		elif [ -f "${PWD##*/}"    ]; then ex "${PWD##*/}"   ; "${PWD##*/}"    ${@:2}
		elif [ -f "${PWD##*/}.sh" ]; then ex "${PWD##*/}.sh"; "${PWD##*/}.sh" ${@:2}
		elif [ -f "${PWD##*/}.js" ]; then ex "${PWD##*/}.js"; "${PWD##*/}.js" ${@:2}
		else echo "-bash: $1: command not found"
		fi
	else echo "-bash: $1: command not found"
	fi
	}