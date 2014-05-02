//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// greenspun's law //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
// everything here useful in clojure has been translated

// closures and function literals
static interface Fn0 <R> {R $();}
static interface Fn1 <R,A> {R $(A a);}
static <R,A> Fn1<R,A> fn1(final String name) {
	return new Fn1<R,A>(){public R $(A a){return $.$(a);} Fn1<R,A> $ = new Fn1<R,A>(){public R $(A v) {$ = fn1(jclass(v),name); return $.$(v);}};};}
static <R,A> Fn1<R,A> fn1(final Class c, String name) {
	{val m = jmethod1(c,name); if (m != null) {
		val s = jisStatic(m);
		return new Fn1<R,A>(){public R $(A a){try{return (R)(s? m.invoke(null,a) : m.invoke(a));}catch(Exception e){return throw_(e);}}};
	}}{val m = jfield(c,name); if (m != null) {
		return new Fn1<R,A>(){public R $(A a){try{return (R)m.get(a);}catch(Exception e){return throw_(e);}}};
	}}
	return fail();}

// java hacks
static <T> T throw_(Object v) {
	$.<RuntimeException>throw__(v instanceof Throwable? (Throwable)v : new Exception(""+v));
	return null;}
@SuppressWarnings("unchecked") static <T extends Throwable> void throw__(Throwable t) throws T {throw (T)t;}
static <T> T T(Object v) {return (T)v;}

// print
static String pretty(Object... v) {String r = ""; if (v.length > 0) {r += v[0]; for (int i = 1; i < v.length; i++) r += " "+v[i];} return r;}
static void print(Object... v) {System.out.print(pretty(v));}
static void println(Object... v) {System.out.println(pretty(v));}

// conversions and constructors
static int i(Object v) {return v instanceof Number? ((Number)v).intValue() : Integer.parseInt(v+"");}
static double d(Object v) {return v instanceof Number? ((Number)v).doubleValue() : Double.parseDouble(v+"");}
static boolean b(Object v) {return v instanceof Boolean? (Boolean)v : "true".equals(v)? true : "false".equals(v)? false : v != null;}
static File file(Object v) {return v instanceof File? (File)v : new File(v+"");}
static <T> List<T> list(Object... v) {val r = new ArrayList(); for (Object vv : v) r.add(vv); return T(r);}
static <K,V> Map<K,V> hashMap(Object... v) {
	if (v.length % 2 != 0) fail();
	val r = new HashMap<K,V>(); for (int i=0;i<v.length/2;i++) r.put((K)v[i*2],(V)v[i*2+1]); return r;}
static <T> Set<T> set(Object... v) {val r = new HashSet(); for (Object vv : v) r.add(vv); return T(r);}

// standard functions
static <T,_> List<T> map(Object list, Fn1<T,_> f) {
	val r = list();
	for (_ v : (List<_>)list) r.add(f.$(v));
	return T(r);}
static <K,V> Map<K,V> dictBy(Object list, Fn1<K,V> f) {
	val r = hashMap();
	for (V v : (List<V>)list) r.put(f.$(v),v);
	return T(r);}
static <T> T or(T a, T b) {return a != null? a : b;}
static <T> T copy(T v) {
	if (v instanceof Collection) return T(new ArrayList((Collection)v));
	return fail();}
static <T> List<T> cat(List<T> a, List<T> b) {val r = list(); r.addAll(a); r.addAll(b); return T(r);}
static <T> List<T> cat(List many) {val r = list(); for (Object v : many) r.addAll((List)v); return T(r);}
static <T> Set<T> intersection(Set<T> a, Set<T> b) {val r = set(); r.addAll(a); r.retainAll(b); return T(r);}
static <T> List<T> sort(List<T> v, Comparator c) {Collections.sort(v,c); return v;}

// reflection
static class T<T>{Type $ = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];}
static <T> T jaccessible(T v) {((AccessibleObject)v).setAccessible(true); return v;}
static Class jclass(Object v) {return v instanceof Class? (Class)v : v.getClass();}
static boolean jisStatic(Member m) {return Modifier.isStatic(m.getModifiers());}
static Set<String> jslots(Class c) {return set(map(list((Object[])c.getDeclaredFields()),fn1("getName")).toArray());}
static Method jmethod1(Class c, String name) {
	// doesn't give reliable results if the method is overloaded
	Method r = null;
	Class c_ = c;
	do {
		for (val m : c_.getDeclaredMethods())
			if (m.getName().equals(name))
				r = m;
		c_ = c_.getSuperclass();
	} while (c_ != null);
	return r == null? null : jaccessible(r);}
static Field jfield(Class c, String name) {try{return jaccessible(c.getDeclaredField(name));}catch(Exception e){return throw_(e);}}
static <T> T jcall(Object v, String name, Object... args) {try{
	val j = jfield(jclass(v),name);
	if (args.length == 0) {
		return (T)j.get(v);
	} else if (args.length == 1) {
		j.set(v,args[0]); return (T)args[0];
	} else return fail();
	}catch(Exception e){return throw_(e);}}
static Object jnew(Class c, Object... args) {try{
	for (val v : c.getDeclaredConstructors())
		try {return v.newInstance(args);} catch (IllegalArgumentException e) {}
	throw new IllegalArgumentException();
	}catch(Exception e){return throw_(e);}}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// misc //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

static BufferedImage makeImage(Component me, int X, int Y) {return me.getGraphicsConfiguration().createCompatibleImage(X,Y);}
static void playSound(final String file, final Runnable onEnd) {
	import javax.sound.sampled.*;
	runIn(0,new Runnable(){public void run(){try{
		Clip t = AudioSystem.getClip();
		t.open(AudioSystem.getAudioInputStream(file(file)));
		t.addLineListener(new LineListener(){public void update(LineEvent ev) {
			if (ev.getType() == LineEvent.Type.CLOSE || ev.getType() == LineEvent.Type.STOP) onEnd.run();
			}});
		t.start();
		}catch(Exception e){throw_(e);}}});}

// misc
static double time() {return System.nanoTime()/1000000000.0;}
static void write(Object file, String v) {try{val out = new PrintWriter(file(file)); out.print(v); out.close();}catch (Exception e){throw_(e);}}
static BufferedImage readImage(Object path) {try{return javax.imageio.ImageIO.read(file(path));}catch(Exception e){return throw_(e);}}
static <T> T fail(Object... v) {return throw_(new Exception(v.length == 0? "assert false;" : pretty(v)));}

// random
static {Math.random();}
static Random rand = jcall(Math.class, "randomNumberGenerator");
static <T> T rand(List<T> v) {return v.get(rand.nextInt(v.size()));}
static int rand(int n) {return rand.nextInt(n);}
static int rand(int a, int b) {return rand(b-a)+a;}
static double randGaussian(double maxDevs) {
	double r; do {r = rand.nextGaussian();} while (Math.abs(r) > maxDevs);
	return r;}

// strictly better than java.util.Timer (also, uses daemon threads)
// minor issue: only uses ten threads, so eleven tasks won't happen simultaneously
static AtomicInteger _timerName = new AtomicInteger();
static java.util.concurrent.ScheduledExecutorService _timer =
	java.util.concurrent.Executors.newScheduledThreadPool(10,
		new java.util.concurrent.ThreadFactory(){public Thread newThread(final Runnable v){
			Thread r = new Thread(v); r.setDaemon(true); r.setName("pool_timer-"+_timerName.incrementAndGet()); return r;}});
static Runnable _exWrap(final Runnable v) {class c_exWrap implements Runnable {public void run(){try {v.run();} catch (Throwable e) {
	List t = list();
	StackTraceElement here = null;
	for (StackTraceElement v : e.getStackTrace()) {
		if (v.getClassName().endsWith("c_exWrap"))
			{here = v; break;}
		t.add(v);}
	e.setStackTrace((StackTraceElement[])t.toArray(new StackTraceElement[0]));
	print("Exception in thread \""+Thread.currentThread().getName()+"\" "); e.printStackTrace();
	println("\tat <pool> ("+here+")");
	}}}
	return new c_exWrap();}
static void runIn(double seconds, Runnable v) {_timer.schedule(_exWrap(v),(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}
static void runWithFixedRate(double seconds, Runnable v) {_timer.scheduleAtFixedRate(_exWrap(v),0,(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}
static void runWithFixedDelay(double seconds, Runnable v) {_timer.scheduleWithFixedDelay(_exWrap(v),0,(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}

// swing / awt
static Color color(double r, double g, double b) {return new Color((float)min(r,1d),(float)min(g,1d),(float)min(b,1d));}
static Toolkit TK() {return Toolkit.getDefaultToolkit();}
static JFrame makeFullscreenJFrame() {
	val r = new JFrame();
	r.setUndecorated(true);
	r.setSize(TK().getScreenSize());
	return r;}
static void JFrame_setContentPane(JFrame a, Container b) {
	a.setContentPane(b);
	a.validate();
	a.repaint();
	}
static List swingGetEscapeHookList(Object j) {
	val jc = j instanceof JFrame? ((JFrame)j).getRootPane() : (JComponent)j;
	class HookList extends AbstractAction {List<Runnable> hooks = list(); public void actionPerformed(ActionEvent e){for (Runnable v : hooks) v.run();}}
	HookList r = (HookList)jc.getActionMap().get("escapeHookList");
	if (r == null) {
		r = new HookList();
		jc.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"escapeHookList");
		jc.getActionMap().put("escapeHookList",r);
	}
	return r.hooks;}
static void useSystemLookAndFeel() {try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception e){throw_(e);}}

// initial rough-draft sketch of destructuring + dictionary to pojo converter
static <T> T destructure(T root, Object o, Object... slots) {
	for (int i=0;i<slots.length;i++) {
		val slot = (String)slots[i];
		val v = o instanceof Map? ((Map)o).get(slot) : ((List)o).get(i);
		val r = unpickle(jfield(jclass(root),slot).getGenericType(),v);
		jcall(root,slot,r);
	}
	return root;}
static <T> T unpickle(Class<T> c, Object o) {return (T)unpickle((Type)c,o);}
static Object unpickle(Type type, Object o) {try{
	if (o == null) return o;
	Class c = type instanceof ParameterizedType? (Class)((ParameterizedType)type).getRawType() : (Class)type;
	final Type[] args = type instanceof ParameterizedType? ((ParameterizedType)type).getActualTypeArguments() : new Type[0];
	val easy =
		c == double.class? d(o):
		c == int.class? i(o):
		c == boolean.class? b(o):
			null;
	if (easy != null) return easy;
	if (c.isInstance(o)) {
		if (c == List.class && args.length == 1) return map(o,new Fn1(){public Object $(Object v){return unpickle(args[0],v);}});
		return o;}
	try {return jnew(c,o);} catch (IllegalArgumentException e) {}
	if (o instanceof Map) return destructure(c.newInstance(),o,intersection(jslots(c),((Map)o).keySet()).toArray());
	return o;}catch(Exception e){return throw_(e);}}