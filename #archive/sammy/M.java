package sammy;

import lombok.*;

import java.util.List;
import java.util.ArrayList;

public class M
{
	private static final double[] mortalityTable = {
			0,
			0.006865, 0.000465, 0.000331, 0.000259, 0.000198, 0.000168, 0.000151, 0.000142, 0.000139, 0.000134, 0.000165, 0.000147, 0.000176, 0.000211,
			0.000257, 0.000339, 0.000534, 0.000660, 0.000863, 0.000925, 0.000956, 0.000965, 0.000987, 0.000953, 0.000955, 0.000920, 0.000962, 0.000949,
			0.000932, 0.000998, 0.001014, 0.001046, 0.001110, 0.001156, 0.001227, 0.001357, 0.001460, 0.001575, 0.001672, 0.001847, 0.002026, 0.002215,
			0.002412, 0.002550, 0.002847, 0.003011, 0.003371, 0.003591, 0.003839, 0.004178, 0.004494, 0.004804, 0.005200, 0.005365, 0.006056, 0.006333,
			0.007234, 0.007101, 0.008339, 0.009126, 0.010214, 0.010495, 0.011966, 0.012704, 0.014032, 0.015005, 0.016240,
		};
	private static final char[] vowels = {97, 101, 105, 111, 117};
	private static final char[] consonants = {98, 99, 100, 102, 103, 104, 107, 108, 109, 110, 112, 114, 115, 116, 122};
	
	public static void main(String[] args)
	{
		Person alice = new Person("Alice", 20 * 12, false);
		Person bobob = new Person("Bobob", 20 * 12, true);
		Person carol = new Person("Carol", 20 * 12, false);
		Person kitty = new Person("Kitty", 20 * 12, true);
		wed(danny, alice);
		wed(bobob, carol);
		Grid grid = new Grid();
		grid.add(alice);
		grid.add(bobob);
		grid.add(carol);
		grid.add(danny);
		while (true)
		{
			/*S.p("want to see another iteration? if you don't, type smth before pressing enter");
			String input = new java.util.Scanner(System.in).nextLine();
			if (!"".equals(input))
				break;*/
			S.sleep(150);
				
			for (Person p : grid)
				p.age += 1;
			
			List<Person> unwedGuys = new ArrayList<Person>();
			List<Person> unwedGirls = new ArrayList<Person>();
			for (Person p : grid)
			if (p.age >= 16 * 12 && p.age <= 30 * 12 && p.partner == null)
				(p.isMale? unwedGuys : unwedGirls).add(p);
			for (int i = 0; i < unwedGuys.size(); i++)
			for (int j = 0; j < unwedGirls.size(); j++)
			if (S.rand(14) == 0)
				{wed(unwedGuys.get(i), unwedGirls.get(j)); unwedGuys.remove(i); unwedGirls.remove(j); i--; j--; if (i < 0) break;}
			
			List<Person> babies = new ArrayList<Person>();
			for (Person p : grid)
			for (Person q : grid)
			if (p.partner == q)
			if (p.age >= 16 * 12 && p.age <= 40 * 12)
			if (q.age >= 16 * 12 && q.age <= 40 * 12)
			if (S.rand(7) == 0)
				babies.add(new Person(getRandomName(), 0, S.randBool()));
			for (Person p : babies)
				grid.add(p);

			for (Person p : grid)
			if (diesAtAge(p.age))
				grid.kill(p);
			
			S.p(grid.getScreen());
		}
	}
	
	static String getRandomName()
	{
		return new String(new char[]{(char)(randConsonant() - 32), randVowel(), randConsonant(), randVowel(), randConsonant()});
	}
	
	static boolean diesAtAge(int age)
	{
		age /= 12;
		if (age > 67 || S.rand() < mortalityTable[age])
			return true;
		else
			return false;
	}
	
	static void wed(Person a, Person b)
		{a.partner = b; b.partner = a;}
		
	static char randVowel() {return vowels[S.rand(vowels.length)];}
	static char randConsonant() {return consonants[S.rand(consonants.length)];}
}

class Person
{
	public String name;
	public int age;
	public Person partner;
	public boolean isMale;
	public Person(String _name, int _age, boolean _isMale)
		{name = _name; age = _age; isMale = _isMale;}
	public String toString()
		{return name + ", " + (isMale? "guy " : "girl") + ", " + age;}
}