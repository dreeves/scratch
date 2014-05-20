import java.util.Scanner;

import java.io.File;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class JavaFinder
{
	private static void println(String printy) {System.out.println(printy);}
	private static void print(String printy) {System.out.print(printy);}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		print("Input relative directory to look in: ");
		String reldir = input.nextLine();
		println("");
		doIt(new File(reldir));
		println("\nTotal # of files: " + totalFiles);
		println("Total KLoC: " + (((double)totalLOC) / 1000));
		println("Average LoC: " + (totalLOC / totalFiles));
	}

	private static int totalLOC;
	private static int totalFiles;

	private static void doIt(File file) {
		for (File f : file.listFiles())
			if (f.isFile()) {
				String name = f.getPath();
				if (name.length() >= 5 && name.substring(name.length() - 5).equals(".java"))
					{println(name); totalLOC += jf.analyseForLOC(name); totalFiles++;}
			} else if (f.isDirectory()) {
				doIt(f);
			}
	}

	private static JavaFinder jf = new JavaFinder();
		/**Reads a UTF-8 text file and returns the linecount.*/
	private int analyseForLOC(String filename) {
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filename), "UTF-8")); //specifically, UTF-8 with BOM
			while (reader.readLine() != null) count++;
			reader.close();
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		return count;
	}
}