  class Complex
{
 double real_part,imag_part;

// a simple constructor
 Complex()
{
 real_part=0.0;
imag_part=0.0;  
}

 Complex(double r,double i)
{
real_part=r;
imag_part=i; 
}

// This class defines a complex number and
// extends the four intrinsic operators +, -, * and / to
// have complex operands

 Complex operatorplus( Complex z2)
{
 Complex c_add=new Complex();

c_add.real_part=real_part+z2.real_part;
c_add.imag_part=imag_part+z2.imag_part; 
return c_add; 
}

 Complex operatorminus( Complex z2)
{
 Complex c_sub=new Complex();

c_sub.real_part=real_part-z2.real_part;
c_sub.imag_part=imag_part-z2.imag_part; 
return c_sub; 
}

 Complex operatormult( Complex z2)
{
 Complex c_mult=new Complex();

c_mult.real_part=real_part*z2.real_part-
imag_part*z2.imag_part;
c_mult.imag_part=real_part*z2.imag_part+
imag_part*z2.real_part; 
return c_mult; 
}

 Complex operatordiv( Complex z2)
{
 Complex c_div=new Complex(); 

// Local variable to save calculating denominator twice
double denom;

// Calculate function result

denom=z2.real_part*z2.real_part+
z2.imag_part*z2.imag_part;
c_div.real_part=(real_part*z2.real_part+
imag_part*z2.imag_part)/denom;
c_div.imag_part=(z2.real_part*imag_part-
real_part*z2.imag_part)/denom; 
return c_div;  
}

public String toString()// what good would a class be without a toString method
{return"("+Double.toString(real_part)+","+Double.toString(imag_part)+")";    }
}

 class TestComplex
{
public static void main(String[]args)
{
 Complex u=new Complex(1.0,2.0);
 Complex v=new Complex(3.0,4.0);
 Complex w=new Complex(5.0,6.0);
 Complex z=new Complex();

System.out.println(" u= "+u);
System.out.println(" v= "+v);
System.out.println(" w= "+w);
z=(u.operatorplus(v));
System.out.println(" u+v= "+z);
z=(u.operatorminus(v));
System.out.println(" u-v= "+z);
z=(u.operatormult(v));
System.out.println(" u*v= "+z);
z=(u.operatordiv(v));
System.out.println(" u/v= "+z);

z=((((u.operatorplus(w))).operatordiv(v)).operatorplus((u.operatormult(z))));
System.out.println(" (u - w) / v + u * z = "+z);
}
}