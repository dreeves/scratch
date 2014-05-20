import java.awt.Rectangle;

public class P24
{
	public static void main(String[] args)
	{
		//Defines the rectangles
		Rectangle box1 = new Rectangle(20, 18, 34, 97);
		Rectangle box2 = new Rectangle(37, 14, 96, 22);

		//Prints info on box1
		System.out.print("The upper-left coordinate of the" +
				" first rectangle is (");
		System.out.print(box1.getX());
		System.out.print(", ");
		System.out.print(box1.getY());
		System.out.print("), the width is ");
		System.out.print(box1.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box1.getHeight());
		System.out.println(".");
		
		//Prints info on box2
		System.out.print("The upper-left coordinate of the" +
		" second rectangle is (");
		System.out.print(box2.getX());
		System.out.print(", ");
		System.out.print(box2.getY());
		System.out.print("), the width is ");
		System.out.print(box2.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box2.getHeight());
		System.out.println(".");
		
		//Defines box3 as the intersection of box1 and box2
		
		Rectangle box3 = box1.intersection(box2);
		
		//Prints info on box3
		System.out.print("The upper-left coordinate of the" +
		" intersection of the two rectangles is (");
		System.out.print(box3.getX());
		System.out.print(", ");
		System.out.print(box3.getY());
		System.out.print("), the width is ");
		System.out.print(box3.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box3.getHeight());
		System.out.println(".");
		
		System.out.println(" ");
		
		//Does like above, but with non-intersecting rectangles
		
		//Defines the rectangles
		Rectangle box4 = new Rectangle(10, 11, 15, 14);
		Rectangle box5 = new Rectangle(2, 3, 5, 4);

		//Prints info on box4
		System.out.print("The upper-left coordinate of the" +
				" first rectangle is (");
		System.out.print(box4.getX());
		System.out.print(", ");
		System.out.print(box4.getY());
		System.out.print("), the width is ");
		System.out.print(box4.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box4.getHeight());
		System.out.println(".");
		
		//Prints info on box5
		System.out.print("The upper-left coordinate of the" +
		" second rectangle is (");
		System.out.print(box5.getX());
		System.out.print(", ");
		System.out.print(box5.getY());
		System.out.print("), the width is ");
		System.out.print(box5.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box5.getHeight());
		System.out.println(".");
		
		//Defines box6 as the intersection of box4 and box5
		
		Rectangle box6 = box4.intersection(box5);
		
		//Prints info on box6
		System.out.print("The upper-left coordinate of the" +
		" intersection of the two rectangles is (");
		System.out.print(box6.getX());
		System.out.print(", ");
		System.out.print(box6.getY());
		System.out.print("), the width is ");
		System.out.print(box6.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box6.getHeight());
		System.out.println(".");
	}
}