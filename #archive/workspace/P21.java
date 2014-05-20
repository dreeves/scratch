import java.awt.Rectangle;

public class P21
{
	public static void main(String[] args)
	{
		//Defines the rectangle
		Rectangle box = new Rectangle(8, 13, 12, 57);
		
		System.out.print("The area of this rectangle is ");
		//How can I make this print an integer?
		System.out.print(box.getWidth()*box.getHeight());
		System.out.println(".");
		
		/*Additional information about the
		rectangle follows, even though it is
		uneeded for this assignment*/
		System.out.print("Additionally, the upper-left coordinate is (");
		System.out.print(box.getX());
		System.out.print(", ");
		System.out.print(box.getY());
		System.out.print("), the width is ");
		System.out.print(box.getWidth());
		System.out.print(", and the height is ");
		System.out.print(box.getHeight());
		System.out.println(".");
		
		System.out.print("Also, the perimeter of this rectangle is ");
		System.out.print(2*(box.getWidth()+box.getHeight()));
		System.out.print(".");
	}
}