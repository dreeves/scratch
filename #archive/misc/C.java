public class C
{
	public static void main(String[] args)
	{
		for (double d = 1.4446678; d <= 1.444668; d += 0.0000001)
		{
			double c = d;
			for (int i = 0; i < 100000000; i++)
				c = Math.pow(d, c);
			System.out.println(d + " " + c);
		}
	}
}