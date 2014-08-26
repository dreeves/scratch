// todo eh: consider making menukey sticky, like F8
// todo eh: fill out F8
// todo: run (F7) should kill any terminated run consoles
// todo: add " to url end
// todo: ≡/ should also work with text already on the clipboard, if copy copies nothing

// sound and music controls
AppsKey & Right::; if GetKeyState("shift") {MouseMove  1,  0, 0, R} else {Send ‹Volume_Up›  } return
AppsKey & Left::;  if GetKeyState("shift") {MouseMove -1,  0, 0, R} else {Send ‹Volume_Down›} return
AppsKey & Up::;    if GetKeyState("shift") {MouseMove  0, -1, 0, R} else {
	if WinExist(vlc) {WinActivate}
	else if (is_mute() and WinTitle(spotify) = "Spotify") {Send ‹Volume_Mute›}
	Send ‹Media_Play_Pause›} return
AppsKey & Down::;  if GetKeyState("shift") {MouseMove  0,  1, 0, R} else {
	if (!is_mute() and WinTitle(spotify) != "Spotify") {Send ‹Media_Play_Pause›}
	Send ‹Volume_Mute›} return
AppsKey & ,::; if WinExist(vlc) {WinActivate}; Send ‹Media_Prev›; return
AppsKey & .::; if WinExist(vlc) {WinActivate}; Send ‹Media_Next›; return
AppsKey &  RCtrl::; if WinExist(vlc) {WinActivate} else {Send ‹Launch_Media›} return
~RCtrl & AppsKey::; if WinExist(vlc) {WinActivate} else {Send ‹Launch_Media›} return
AppsKey & Numpad1::feeling_lucky(SubStr(WinTitle(spotify), StrLen("Spotify - ")+1) . " lyrics")

// run apps
AppsKey & S::; if WinExist(cmd) {WinActivate; if !GetKeyState("shift") {Send ‹Up›‹Enter›}; return} // else, execute next label
AppsKey & -::; t := current_directory(); Run bash -ci "cd \"%t%\"SEMICOLONbash"; return
F7::; t := current_directory(); Run bash -ci "cd \"%t%\"SEMICOLONrun.shSEMICOLONpause"; return
AppsKey & Enter::; if WinExist(calc) && !GetKeyState("shift") {WinActivate} else {Run calc} return
#define chrome_newtab(action) if WinExist(chrome) {WinActivate; Send ^t; action}
chrome(v) {chrome_newtab(paste(v); Send ‹Enter›)}
feeling_lucky(v) {chrome("http://www.google.com/search?sourceid=navclient&gfns=1&q=" . v)}
AppsKey & /::; chrome_newtab(); return
AppsKey & \::; chrome(copy()); return

// misc
AppsKey & LButton::; Send ‹LButton down›; KeyWait LButton; Send ‹LButton up›; // continue on next line
//~LButton & AppsKey::;
	v := copy();
	if (RegExMatch(v,"⏎([^\s⏎]+)",t)) {chrome("C:/Users/zii/ali/misc/linked/" . t1)}
	else if (RegExMatch(v,"https?://[^ )\\]]+",t)) {chrome(t)}
	else if (RegExMatch(v,"[\\w.-_]+@[\\w.-_]+\\.[\\w.-_]+",t)) {chrome("mailto:" . t)}
	else {chrome(v)} return

current_directory() {
	if WinActive(explorer) {...}
	else if WinActive(sublime) {...}
	else if (WinActive_desktop or WinActive(taskbar)) {...}
	"~/ali/code"}