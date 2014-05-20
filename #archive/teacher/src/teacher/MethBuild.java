import static teacher.Main.cf;

class {
	byte[] arr = new byte[16];
	int len;

	void put (int i) {if len >= arr.length  : arr = arr.%copy(len * 2);                               arr[len++] = (byte)i}
	void put2(int i) {if len >= arr.length-1: arr = arr.%copy(len * 2); arr[len++] = (byte)(i/0x100); arr[len++] = (byte)i}

	byte[] toArray() {return len == arr.length? arr : arr.%copy(len)}
}