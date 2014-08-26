# for l10n, target Window menu by position rather than name
tell application "Google Chrome"
	set t to name of window 2
	tell application "System Events"
		tell process "Google Chrome"
			tell menu bar 1
				click menu item t of menu 1 of menu bar item -2
			end tell
		end tell
	end tell
	activate
end tell