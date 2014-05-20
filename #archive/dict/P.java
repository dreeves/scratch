import java.io.*;
import java.net.MalformedURLException;

import java.util.List;
import java.util.ArrayList;

public class P
{
	static String target = "reapers";//"wujywod";
	static int[] intTarget = intArrFromString(target);
	
	public static void main(String[] args) throws Exception
	{
		List<String> words = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(P.class.getResourceAsStream("newdict.txt"), "UTF-8"));
		
		System.out.println("it works: " + intArrEquals(intArrPlus(intArrFromString("aaaaaaa"), intArrFromString("bbbbbfb")), intArrFromString("cccccgc")));
		System.out.println("it works: " + intArrEquals(intArrPlus(intArrFromString("aaafaaa"), intArrFromString("bbbbbbb")), intArrFromString("ccchccc")));
		System.out.println("it works: " + intArrEquals(intArrPlus(intArrFromString("aaaacaa"), intArrFromString("bbbbfbb")), intArrFromString("ccccicc")));
		
		while (true)
		{
			String s = reader.readLine();
			if (s == null)
				break;
			words.add(s);
		}
		
		int size = words.size();
		int[][] dict = new int[size][];
		for (int i = 0; i < size; i++)
			dict[i] = intArrFromString(words.get(i));
		int[] targ = intTarget;
		
		for (int i = 0; i < dict.length; i++)
		{
			if (i % 1000 == 0)
				System.out.println("We're at " + i);
			for (int j = i + 1; j < dict.length; j++)
			if (intArrEquals(intArrPlus(dict[i], dict[j]), targ))
				System.out.println("match: " + words.get(i) + " + " + words.get(j));
		}
	}
	
	static int[] intArrPlus(int[] a, int[] b)
	{
		int[] ret = new int[7];
		ret[0] = a[0] + b[0];
		ret[1] = a[1] + b[1];
		ret[2] = a[2] + b[2];
		ret[3] = a[3] + b[3];
		ret[4] = a[4] + b[4];
		ret[5] = a[5] + b[5];
		ret[6] = a[6] + b[6];
		return ret;
	}
	
	static boolean intArrEquals(int[] a, int[] b)
	{
		if (a[0] != b[0]) return false;
		if (a[1] != b[1]) return false;
		if (a[2] != b[2]) return false;
		//if (a[3] != b[3]) return false;
		//if (a[4] != b[4]) return false;
		//if (a[5] != b[5]) return false;
		//if (a[6] != b[6]) return false;
		return true;
	}
	
	static int[] intArrFromString(String s)
	{
		char[] c = s.toCharArray();
		int[] ret = new int[7];
		for (int i = 0; i < 7; i++)
			ret[i] = c[i] - 96;
		return ret;
	}
}
		/*List<String> words = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(P.class.getResourceAsStream("newdict.txt"), "UTF-8"));
		
		while (true)
		{
			String s = reader.readLine();
			if (s == null)
				break;
			words.add(s);
		}
				
		int j = words.size() / 150;
		for (int i = 0; i < words.size(); i++)
		if (words.get(i).length() != 7)
			{if (i % j == 0) System.out.println("We're at " + i); words.remove(i); i--;}
		System.out.println("^_^");

		reader.close();
		
		PrintWriter out = new PrintWriter(new FileWriter("newdict.txt"));
		
		int k = words.size();
		for (int i = 0; i < k; i++)
		{
			if (i % (k / 30) == 0) System.out.println("Now we're at " + i);
			out.println(words.get(i));
		}
			
		out.close();*/