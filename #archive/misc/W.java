import java.util.Arrays;
import java.util.Random;

public class W
{
	public static void main(String[] args)
	{
		System.out.print("                                                                                ");
		Random r = new Random();
		while (true)
		{
			System.out.print('+');
			int num = 149 + r.nextInt(3) - 1;
			char[] print = new char[num];
			Arrays.fill(print, ' ');
			System.out.print(new String(print));
			try {Thread.sleep(10);}
			catch (Exception e) {}
		}
	}
}