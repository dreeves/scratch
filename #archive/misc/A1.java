import java.util.Scanner;
public class A
{
public static void main(String[] args)
{
	Scanner input = new Scanner(System.in);
	print("Input z coord: ");
	float z = input.nextFloat();
	print("Input y coord: ");
	float y = input.nextFloat();
	print("Input x coord: ");
	float x = input.nextFloat();
	float greatest = (x>y? (x>z? x : z) : (y>z? y : z));
	float coef = 1 / greatest;
	float currz = 0;
	float curry = 0;
	float currx = 0;
	for (int k = 0; k <= Math.round(greatest); k++)
	{
		System.out.printf("%.2f %.2f %.2f\t", currz, curry, currx);
		for (int i = 0; i < (int)x + 2; i++)
		if (y >= x)
			if (i == Math.round(currx))
				print(Math.round(currz) + "");
			else
				print(" ");
		else
			if (i == Math.round(curry))
				print(Math.round(currz) + "");
			else
				print(" ");
		println("");
		currz += z*coef;
		curry += y*coef;
		currx += x*coef;
	}
}
public static void print(String printy)
{System.out.print(printy);}
public static void println(String printy)
{System.out.println(printy);}
}