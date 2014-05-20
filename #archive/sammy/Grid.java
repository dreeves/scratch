package sammy;

import lombok.*;

import java.util.List;
import java.util.ArrayList;

import java.util.Iterator;

public class Grid implements Iterable<Person>
{
	private final Person[][] people = new Person[10][3];
	
	public Iterator<Person> iterator()
		{return new GridIterator(people);}
		
	public void add(Person p)
	{
		boolean allFull = true;
		for (int x = 0; x < 10; x++)
		for (int y = 0; y < 3; y++)
		if (people[x][y] == null)
			allFull = false;
		if (!allFull)
		while (true)
		{
			int x = S.rand(10), y = S.rand(3);
			if (people[x][y] != null)
				continue;
			else
				{people[x][y] = p; break;}
		}
	}
	
	public void kill(Person p)
	{
		for (Person q : this)
		for (int x = 0; x < 10; x++)
		for (int y = 0; y < 3; y++)
		if (people[x][y] == p)
			{people[x][y] = null; if (p.partner != null) p.partner.partner = null;}
	}
	
	public char[] getScreen()
	{
		char[][] ret = Arrayu.fill(new char[24][80], ' ');
		for (int i = 0; i < 10; i++)
		for (int j = 0; j < 3; j++)
		if (people[i][j] != null)
		{
			char[][] pp = constructPersonPicture(people[i][j]);
			for (int x = 0; x < 7; x++)
			for (int y = 0; y < 7; y++)
				ret[j * 7 + y][i * 8 + x] = pp[y][x];
		}
		
		char[] reallyRet = new char[80 * 24];
		for (int i = 0; i < 24; i++)
			S.ac(ret[i], 0, reallyRet, i * 80, 80);
		return reallyRet;
	}
	
	private static char[][] constructPersonPicture(Person p)
	{
		char[][] ret = new char[][]
			{
				"-------".toCharArray(),
				("|" + p.name + "|").toCharArray(),
				("|" + (p.isMale? " guy " : "girl ") + "|").toCharArray(),
				"|     |".toCharArray(),
				(p.partner == null? "-------" : "|PRDNR|").toCharArray(),
				(p.partner == null? "       " : "|" + p.partner.name + "|").toCharArray(),
				(p.partner == null? "       " : "-------").toCharArray(),
			};
		String years = String.valueOf(p.age / 12);
		String months = String.valueOf(p.age % 12);
		while (years.length() < 2) years = ' ' + years;
		while (months.length() < 2) months = ' ' + months;
		ret[3] = ('|' + years + '.' + months + '|').toCharArray();
		return ret;
	}
}

class GridIterator implements Iterator<Person>
{
	private Person[] list;
	private int idx = 0;
	
	public GridIterator(Person[][] people)
	{
		List<Person> peeps = new ArrayList<Person>();
		for (int x = 0; x < 10; x++)
		for (int y = 0; y < 3; y++)
		if (people[x][y] != null)
			peeps.add(people[x][y]);
		list = peeps.toArray(new Person[0]);
	}
	
	public boolean hasNext() {return idx < list.length;}
	public Person next() {idx++; return list[idx - 1];}
	public void remove() {}
}