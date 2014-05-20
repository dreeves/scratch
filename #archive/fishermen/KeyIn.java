package fishermen;

import lombok.*;

import org.lwjgl.input.Keyboard;

/**Keyboard Input - a wrapper for the Keyboard class.*/
public class KeyIn
{
	private static final int KEY_COUNT = Keyboard.KEYBOARD_SIZE;
	private static enum KeyState {RELEASED, PRESSED, ONCE} //not down, down for 2nd+ time, down for 1st time
	private static KeyState[] keys = new KeyState[KEY_COUNT]; //polled keyboard state
		{for (int i = 0; i < KEY_COUNT; i++) keys[i] = KeyState.RELEASED;}
	private static @Getter boolean capsLock = false;
	
	public static void poll()
	{
		for (int i = 0; i < KEY_COUNT; i++)
			if (Keyboard.isKeyDown(i))
				if (keys[i] == KeyState.RELEASED)
					keys[i] = KeyState.ONCE;
				else
					keys[i] = KeyState.PRESSED;
			else
				keys[i] = KeyState.RELEASED;
		if (once(58)) capsLock = !capsLock;
	}

	public static boolean down(int keyCode) {return keys[keyCode] == KeyState.ONCE || keys[keyCode] == KeyState.PRESSED;}
	public static boolean once(int keyCode) {return keys[keyCode] == KeyState.ONCE;}
	public static boolean down(String keyName) {return onceORdown(keyName.toUpperCase(), false);}
	public static boolean once(String keyName) {return onceORdown(keyName.toUpperCase(), true);}
	
	private static boolean onceORdown(String keyName, boolean itsOnce)
	{
		if ("CONTROL".equals(keyName))
			return onceORdown("LCONTROL", itsOnce) || onceORdown("RCONTROL", itsOnce);
		if ("SHIFT".equals(keyName))
			return onceORdown("LSHIFT", itsOnce) || onceORdown("RSHIFT", itsOnce);
		int i = Keyboard.getKeyIndex(keyName);
		if (i == Keyboard.KEY_NONE)
			{S.pln(keyName + " isn't a real key, is it?"); return false;}
		return itsOnce? once(i) : down(i);
	}
	
	public static char[] getLetters()
	{
		StringBuilder ret = new StringBuilder();
		
		if (once(57)) ret.append(' ');
		
		if (once(82)) ret.append('0'); //NUMPAD0
		if (once(79)) ret.append('1'); //NUMPAD1
		if (once(80)) ret.append('2'); //NUMPAD2
		if (once(81)) ret.append('3'); //NUMPAD3
		if (once(75)) ret.append('4'); //NUMPAD4
		if (once(76)) ret.append('5'); //NUMPAD5
		if (once(77)) ret.append('6'); //NUMPAD6
		if (once(71)) ret.append('7'); //NUMPAD7
		if (once(72)) ret.append('8'); //NUMPAD8
		if (once(73)) ret.append('9'); //NUMPAD9
		if (once(78)) ret.append('+'); //NUMPAD+
		if (once(181)) ret.append('/'); //NUMPAD/
		if (once(83)) ret.append('.'); //NUMPAD.
		if (once(74)) ret.append('-'); //NUMPAD-
		if (once(55)) ret.append('*'); //NUMPAD*

		boolean shift = down("shift");
		boolean caps = shift || isCapsLock();
		
		if (once( 2)) ret.append(shift? '!' : '1');
		if (once( 3)) ret.append(shift? '@' : '2');
		if (once( 4)) ret.append(shift? '#' : '3');
		if (once( 5)) ret.append(shift? '$' : '4');
		if (once( 6)) ret.append(shift? '%' : '5');
		if (once( 7)) ret.append(shift? '^' : '6');
		if (once( 8)) ret.append(shift? '&' : '7');
		if (once( 9)) ret.append(shift? '*' : '8');
		if (once(10)) ret.append(shift? '(' : '9');
		if (once(11)) ret.append(shift? ')' : '0');
		
		if (once(30)) ret.append(caps? 'A' : 'a');
		if (once(48)) ret.append(caps? 'B' : 'b');
		if (once(46)) ret.append(caps? 'C' : 'c');
		if (once(32)) ret.append(caps? 'D' : 'd');
		if (once(18)) ret.append(caps? 'E' : 'e');
		if (once(33)) ret.append(caps? 'F' : 'f');
		if (once(34)) ret.append(caps? 'G' : 'g');
		if (once(35)) ret.append(caps? 'H' : 'h');
		if (once(23)) ret.append(caps? 'I' : 'i');
		if (once(36)) ret.append(caps? 'J' : 'j');
		if (once(37)) ret.append(caps? 'K' : 'k');
		if (once(38)) ret.append(caps? 'L' : 'l');
		if (once(50)) ret.append(caps? 'M' : 'm');
		if (once(49)) ret.append(caps? 'N' : 'n');
		if (once(24)) ret.append(caps? 'O' : 'o');
		if (once(25)) ret.append(caps? 'P' : 'p');
		if (once(16)) ret.append(caps? 'Q' : 'q');
		if (once(19)) ret.append(caps? 'R' : 'r');
		if (once(31)) ret.append(caps? 'S' : 's');
		if (once(20)) ret.append(caps? 'T' : 't');
		if (once(22)) ret.append(caps? 'U' : 'u');
		if (once(47)) ret.append(caps? 'V' : 'v');
		if (once(17)) ret.append(caps? 'W' : 'w');
		if (once(45)) ret.append(caps? 'X' : 'x');
		if (once(21)) ret.append(caps? 'Y' : 'y');
		if (once(44)) ret.append(caps? 'Z' : 'z');
		
		if (once(41)) ret.append(shift? '`' : '~');
		if (once(12)) ret.append(shift? '_' : '-');
		if (once(13)) ret.append(shift? '+' : '=');
		if (once(26)) ret.append(shift? '{' : '[');
		if (once(27)) ret.append(shift? '}' : ']');
		if (once(43)) ret.append(shift? '|' : '\\');
		if (once(39)) ret.append(shift? ':' : ';');
		if (once(40)) ret.append(shift? '"' : '\'');		
		if (once(51)) ret.append(shift? '<' : ',');
		if (once(52)) ret.append(shift? '>' : '.');
		if (once(53)) ret.append(shift? '?' : '/');

		return ret.toString().toCharArray();
	}
}

/*
200: up arrow
208: down arrow
203: left arrow
205: right arrow
211: delete
15: tab
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