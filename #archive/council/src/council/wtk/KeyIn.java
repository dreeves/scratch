import org.lwjgl.input.Keyboard;

/**a wrapper for org.lwjgl.input.Keyboard*/
class {
		//-1 = not down, positive N = Nth tick on which the button is down
		//so 0 is like ONCE, and 5 means there have been 5 previous consecutive ticks in which it's been down
	static priv int[] key = Arr.fill(new int[Keyboard.KEYBOARD_SIZE], -1);
	static priv @Getter bool capsLock = false;
	
	routine poll() {
		arrFill(key, Keyboard.isKeyDown(i)? key[i] + 1 : -1);
		if (once(58)) capsLock ^= true;
	}

	static bool down(int keyCode) {return ticksDown(keyCode) >= 0;}
	static bool once(int keyCode) {return ticksDown(keyCode) == 0;}
	static int ticksDown(int keyCode) {
		if (keyCode == -2) return S.max(key[29], key[157]); //l and r ctrl
		else if (keyCode == -3) return S.max(key[42], key[54]); //l and r shift
		return key[keyCode];
	}
	static bool down(String keyName) {return ticksDown(keyName) >= 0;}
	static bool once(String keyName) {return ticksDown(keyName) == 0;}	
	static int ticksDown(String keyName) {return ticksDown(idxFromName(keyName));}
	
	static int idxFromName(String keyName) {
		keyName = keyName.toUpperCase();
		if ("CONTROL".equals(keyName)) return -2;
		if ("SHIFT".equals(keyName)) return -3;
		if ("NUMPAD.".equals(keyName)) keyName = "DECIMAL";
		int ret = Keyboard.getKeyIndex(keyName);
		if (ret == Keyboard.KEY_NONE) pln(keyName, "isn't a real key name, is it?");
		return ret;
	}
	
	static String nameFromIdx(int key) {
		if (key == -2) return "ctrl";
		if (key == -3) return "shift";
		val ret = Keyboard.getKeyName(key);
		if (ret == null) {pln(key, "isn't a real key idx, is it?"); return "null";}
		if ("ESCAPE".equals(ret)) return "esc";
		return ret.toLowerCase();
	}
	
	static char next() {
		loop {
			if (!Keyboard.next()) return 0;
			if (Keyboard.getEventKey() != 0 && Keyboard.getEventKeyState()) {
				char ch = Keyboard.getEventCharacter();
				if (ch != 0) return ch;
			}
		}
	}
}
	/*const char[] keyTranslate = new char[256];
	static {
		//1-7 & 11-16 & 18-31 & 33-38 & 40-43 & 58 & 60-64 & 94 & 96-126
		char[] kt = keyTranslate;
		for (int i =  2; i <= 11; i++) kt[i] = "1234567890".charAt(i-2);
		for (int i = 16; i <= 25; i++) kt[i] = "QWERTYUIOP".charAt(i-16);
		for (int i = 30; i <= 38; i++) kt[i] = "ASDFGHJKL".charAt(i-30);
		for (int i = 44; i <= 50; i++) kt[i] = "ZXCVBNM".charAt(i-44);
		for (int i = 71; i <= 83; i++) kt[i] = "789-456+1230.".charAt(i-71); //numpad
		kt[0] = 0; //CHAR_NONE -> NUL (null)
		kt[57] = ' ';  //KEY_SPACE      ->   32
		kt[40] = '\''; //KEY_APOSTROPHE -> ' 39
		kt[51] = ',';  //KEY_COMMA      -> , 44
		kt[12] = '-';  //KEY_MINUS      -> - 45
		kt[52] = '.';  //KEY_PERIOD     -> . 46
		kt[53] = '/';  //KEY_SLASH      -> / 47
		kt[39] = ';';  //KEY_SEMICOLON  -> ; 59
		kt[13] = '=';  //KEY_EQUALS     -> = 61
		kt[26] = '[';  //KEY_LBRACKET   -> [ 91
		kt[43] = '\\'; //KEY_BACKSLASH  -> \ 92
		kt[27] = ']';  //KEY_RBRACKET   -> ] 93
		kt[41] = '`';  //KEY_GRAVE      -> ` 96
		kt[55]  = '*'; //NUMPAD* -> *
		
		//! no! these numpads have to be non-shiftable!
		kt[141] = '='; //NUMPAD= -> =
		kt[179] = ','; //NUMPAD, -> ,
		kt[181] = '/'; //NUMPAD/ -> /
		
		kt[14] = 8; //KEY_BACK -> BS (backspace)
		kt[211] = 127; //KEY_DELETE -> DEL
		kt[15] = '\t'; //KEY_TAB -> \t
		kt[28] = kt[156] = 10; //KEY_RETURN & KEY_NUMPADENTER -> LF (line feed)
		kt[42] = kt[54] = 17; //KEY_LSHIFT & KEY_RSHIFT -> 17
	}
	
	static char applyShift(char c) {
		if ('A' <= c && c <= 'Z') return (char)(c + 'a' - 'A');
		if ('0' <= c && c <= '9') return ")!@#$%^&*(".charAt(c - '0');
		if ('[' <= c && c <= ']') return (char)(c + 32);
		switch (c) {
			case '\'': return '"';
			case ',' : return '<';
			case '-' : return '_';
			case '.' : return '>';
			case '/' : return '?';
			case ';' : return ':';
			case '=' : return '+';
			case '`' : return '~';
			default: return 0;
		}
	}*/
		/*routine flushEventQueue() {while (Keyboard.next());}
			//blocks until a key is pressed
		static int getNextKey() {
			int ret = -1;
			flushEventQueue();
			while (ret == -1) {
				Display.processMessages();					
				bool found = Keyboard.next();
				if (found) ret = Keyboard.getEventKey();
				else S.sleep(10);
			}            
			return ret;  
		}*/
/*                   
200: up arrow        
208: down arrow      
203: left arrow      
205: right arrow     
1: escape            
58: capslock         
69: numlock          
70: scrollock        
221: weird right-click key, "APPS"
197: pause           
210: insert          
199: home            
201: pgup            
209: pgdown          
207: end             
0: the numpad5 key with numlock off is this... odd. 0 is supposed to be for keys that are not real
59 - 68: F1 - F10    
87 - 88: F11 - F12   
*/                   