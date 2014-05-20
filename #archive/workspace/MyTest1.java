import java.util.Random;
import java.awt.Rectangle;

public class MyTest1
{
	public static void main(String[] args)
	{
		
		Random gen1=new Random();
		Random gen2=new Random();
		Random gen3=new Random();
		Random gen4=new Random();
		
		Random gen5=new Random();
		Random gen6=new Random();
		Random gen7=new Random();
		Random gen8=new Random();
		
		Rectangle box1 = new Rectangle
		(
		gen1.nextInt(20)
	,	gen2.nextInt(20)
	,	gen3.nextInt(20)*2
	,	gen4.nextInt(20)*2
		);
		
		System.out.print("(");
		System.out.print(box1.getX());
		System.out.print(", ");
		System.out.print(box1.getY());
		System.out.print(", ");
		System.out.print(box1.getHeight());
		System.out.print(", ");
		System.out.print(box1.getWidth());
		System.out.println(")");
		
		Rectangle box2 = new Rectangle
		(
		gen5.nextInt(20)
	,	gen6.nextInt(20)
	,	gen7.nextInt(20)*2
	,	gen8.nextInt(20)*2
		);
		
		System.out.print("(");
		System.out.print(box2.getX());
		System.out.print(", ");
		System.out.print(box2.getY());
		System.out.print(", ");
		System.out.print(box2.getHeight());
		System.out.print(", ");
		System.out.print(box2.getWidth());
		System.out.println(")");
		
		Rectangle box3 = box1.intersection(box2);
		
		System.out.print("(");
		System.out.print(box3.getX());
		System.out.print(", ");
		System.out.print(box3.getY());
		System.out.print(", ");
		System.out.print(box3.getHeight());
		System.out.print(", ");
		System.out.print(box3.getWidth());
		System.out.println(")");
		System.out.println(" .  . ");
		System.out.println("| -- |");
		System.out.print("+----+");
		
	}
}