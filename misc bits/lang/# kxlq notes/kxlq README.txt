the purpose of kxlq is to make coding not suck, not be frustrating
this may not be possible
but whatever

core unsolved features:
	interoperability:
		kxlq must enable easy use of other libraries, which are in many different programming languages
			incl standard libraries from multiple different languages
	extensible parser/syntax
		impl: macros, recursive descent, both, or what?
		impl: ? lazy, on-demand symbol resolution/definition, notably to provide meaning for symbols like `92`
	the dot operator: object.method syntax
		? meaning: method is a 1-dispatch fn on object . namespace of method is some type that object isa
	non-frustrating ways to express cross-cutting concerns
		example: the "expression problem" (clojure solves it with multimethods)
		mixins/traits
	solid collections, good collection operators, incl advanced subscripting, strings and streams are collections
		nested collections are a thing, and "solid collections" applies to them too
			java-style multi-dim arrays are a part of that thing

crucial-but-solved features:
	proper closures
	namespace/package mechanism
	list and map literals
	generators
	varargs

features implied by the core features:
	(solved) param defaults
	(solved) steve yegge says: list comprehensions (? incl haskelloid .. ranges)
	(solved) destructuring
	(solved) keyword params
	(unsolved) standard OOP: classes, instances, interfaces, polymorphism, etc

additional valuable features:
	(solved) integrated, perl5-compatible regexes (or is it perl6 or what?)
		see http://en.wikipedia.org/wiki/Perl_6_rules
	(solved) cross-platform GUI (html & friends)
	(unsolved) normalcy: look extremely normal and/or have idiomatic-translators
		see javascript, which did normal-looks with extreme success
	(unsolved) static typing, and not just that simplistic java crap . but not that complicated scala crap, either
	(unsolved) duck typing
	(unsolved) strong multi-threading support . clojure does this with traditional tools of immutability and transactions
	(unsolved) continuations & call/cc: oh god, these are beautiful . but they can make interoperability a lot harder, i think
		see http://zope.stackless.com/spcpaper.htm

actual implemented reality of kxlq:
	no public/protected/private, just pythonic name/_name/__name
	"operators" are mostly just names
		if there exist those that are functionally more than just names, they can be specified normally anyway