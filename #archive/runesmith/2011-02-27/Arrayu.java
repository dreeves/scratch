package runebot;

//version: July 23 2010

public class Arrayu
{
	public static     int[] fill(    int[] a,     int val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static    byte[] fill(   byte[] a,    byte val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static    char[] fill(   char[] a,    char val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static    long[] fill(   long[] a,    long val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static   float[] fill(  float[] a,   float val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static   short[] fill(  short[] a,   short val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static  double[] fill( double[] a,  double val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static  String[] fill( String[] a,  String val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	public static boolean[] fill(boolean[] a, boolean val) {for (int i = 0; i < a.length; i++) a[i] = val; return a;}
	
	public static     int[][] fill(    int[][] a,     int val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static    byte[][] fill(   byte[][] a,    byte val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static    char[][] fill(   char[][] a,    char val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static    long[][] fill(   long[][] a,    long val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static   float[][] fill(  float[][] a,   float val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static   short[][] fill(  short[][] a,   short val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static  double[][] fill( double[][] a,  double val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static  String[][] fill( String[][] a,  String val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	public static boolean[][] fill(boolean[][] a, boolean val) {for (int i = 0; i < a.length; i++) for (int j = 0; j < a[i].length; j++) a[i][j] = val; return a;}
	
	public static     int[][][] fill(    int[][][] a,     int val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static    byte[][][] fill(   byte[][][] a,    byte val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static    char[][][] fill(   char[][][] a,    char val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static    long[][][] fill(   long[][][] a,    long val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static   float[][][] fill(  float[][][] a,   float val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static   short[][][] fill(  short[][][] a,   short val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static  double[][][] fill( double[][][] a,  double val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static  String[][][] fill( String[][][] a,  String val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}
	public static boolean[][][] fill(boolean[][][] a, boolean val) {for(int i=0;i<a.length;i++) for(int j=0;j<a[i].length;j++) for(int k=0;k<a[i][j].length;k++) a[i][j][k] = val; return a;}

	public static     int[] copyOf(    int[] a) {    int[] ret = new     int[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static    byte[] copyOf(   byte[] a) {   byte[] ret = new    byte[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static    char[] copyOf(   char[] a) {   char[] ret = new    char[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static    long[] copyOf(   long[] a) {   long[] ret = new    long[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static   float[] copyOf(  float[] a) {  float[] ret = new   float[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static   short[] copyOf(  short[] a) {  short[] ret = new   short[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static  double[] copyOf( double[] a) { double[] ret = new  double[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static  String[] copyOf( String[] a) { String[] ret = new  String[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	public static boolean[] copyOf(boolean[] a) {boolean[] ret = new boolean[a.length]; S.ac(a, 0, ret, 0, a.length); return ret;}
	
	public static     int[][] copyOf(    int[][] a) {    int[][] ret = new     int[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new     int[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static    byte[][] copyOf(   byte[][] a) {   byte[][] ret = new    byte[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new    byte[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static    char[][] copyOf(   char[][] a) {   char[][] ret = new    char[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new    char[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static    long[][] copyOf(   long[][] a) {   long[][] ret = new    long[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new    long[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static   float[][] copyOf(  float[][] a) {  float[][] ret = new   float[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new   float[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static   short[][] copyOf(  short[][] a) {  short[][] ret = new   short[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new   short[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static  double[][] copyOf( double[][] a) { double[][] ret = new  double[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new  double[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static  String[][] copyOf( String[][] a) { String[][] ret = new  String[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new  String[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}
	public static boolean[][] copyOf(boolean[][] a) {boolean[][] ret = new boolean[a.length][]; for (int i = 0; i < a.length; i++) {ret[i] = new boolean[a[i].length]; S.ac(a[i], 0, ret[i], 0, a[i].length);} return ret;}

		//quicksort
	public static void sort(int[] arr)
		{sort(arr, 0, arr.length);}
	public static void sort(int[] arr, int first, int last)
	{
		if (first >= last) return;

		//aquires pivot value
		int pivot = last;
		int k = arr[first];
		int i = first + 1;
		while (i < pivot)
		{
			while (arr[i] < k && i < pivot)
				i++;
			while (arr[pivot] >= k && i < pivot)
				pivot--;
			swap(arr, i, pivot);
		}
		if (arr[pivot] >= k)
			pivot--;
		swap(arr, first, pivot);
		
		sort(arr, first, pivot - 1);
		sort(arr, pivot + 1, last);
	}
	
	private static void swap(int[] x, int a, int b)
		{int t = x[a]; x[a] = x[b]; x[b] = t;}
}