/* -*- Mode: C; tab-width: 4 -*- */

typedef unsigned long ulong;
typedef unsigned long SCHEME;
typedef unsigned char BYTECODE;

typedef struct _heap {
  SCHEME *	one;
  SCHEME *	two;
  ulong		size;
  SCHEME *	free;
} heap_type;

enum opcodes {

  /* opcodes with arguments: */
  OP_PROC				='\n',
  OP_SAVE				='S',
  OP_LIT				='L',
  OP_TUPLE				='T',
  OP_STORE				='>',
  OP_TEST				='?',
  OP_PRIMOP				='%',
  OP_VARREF				='V',
  OP_CONSTANT			='C',
  OP_BUILTIN			='B',
  OP_ASSIGN				='!',

  /* no-argument opcodes */
  OP_RESTORE			='R',
  OP_INVOKE				='I',
  OP_JUMP				='J',
  OP_PUSH_ENV			='Z',

  /* opcodes with implied arguments */
  OP_STORE0				='0',
  OP_STORE1				='1',
  OP_STORE2				='2',
  OP_STORE3				='3',
  OP_STORE4				='4',
  OP_STORE5				='5',
  OP_STORE6				='6',
  OP_STORE7				='7',
  OP_STORE8				='8',
  OP_STORE9				='9',

  OP_LEXREF_00			='a',
  OP_LEXREF_01			='b',
  OP_LEXREF_02			='c',
  OP_LEXREF_03			='d',
  OP_LEXREF_10			='e',
  OP_LEXREF_11			='f',
  OP_LEXREF_12			='g',
  OP_LEXREF_13			='h',
  OP_LEXREF_20			='i',
  OP_LEXREF_21			='j',
  OP_LEXREF_22			='k',
  OP_LEXREF_23			='l',
  OP_LEXREF_30			='m',
  OP_LEXREF_31			='n',
  OP_LEXREF_32			='o',
  OP_LEXREF_33			='p',

  OP_LEXSET_00			='q',
  OP_LEXSET_01			='r',
  OP_LEXSET_02			='s',
  OP_LEXSET_03			='t',
  OP_LEXSET_10			='u',
  OP_LEXSET_11			='v',
  OP_LEXSET_12			='w',
  OP_LEXSET_13			='x',
  OP_LEXSET_20			='y',
  OP_LEXSET_21			='z',
  OP_LEXSET_22			='<',
  OP_LEXSET_23			='>',
  OP_LEXSET_30			='[',
  OP_LEXSET_31			=']',
  OP_LEXSET_32			='{',
  OP_LEXSET_33			='}',

  OP_TUPLE_0			='.',
  OP_TUPLE_1			=';',
  OP_TUPLE_2			='"',
  OP_TUPLE_3			=',',

};

enum primops {
  PRIM_ADD				='+',
  PRIM_SUB				='-',
  PRIM_MUL				='*',
  PRIM_DIV				='/',
  PRIM_MOD				='%',
  PRIM_EQ				='=',
  PRIM_GT				='>',
  PRIM_LT				='<',
  PRIM_AND				='&',
  PRIM_OR				='|',
  PRIM_GE				='}',
  PRIM_LE				='{',
  PRIM_CONS				='a',
  PRIM_CAR				='b',
  PRIM_CDR				='c',
  PRIM_INTEGER_CHAR		='d',
  PRIM_CHAR_INTEGER		='e',
  PRIM_APPLY1			='f',
  PRIM_NEW_TUPLE		='h',
  PRIM_TUPLE_LENGTH		='i',
  PRIM_TUPLE_REF		='j',
  PRIM_TUPLE_SET		='k',
  PRIM_BUILD_TUPLE		='m',
  PRIM_UNDEFINED		='n',
  PRIM_GC				='o',
  PRIM_DUMP_IMAGE		='p',
  PRIM_GET_TYPECODE		='q',
  PRIM_GET_ENVIRONMENT	='r',
  PRIM_ROOM				='s',
  PRIM_INSNS			='t',
  PRIM_GETCC			='u',
  PRIM_PUTCC			='v',
  PRIM_LEFT_SHIFT		='w',
  PRIM_RIGHT_SHIFT		='y'
};

/* Type Tags */

/* immediate types (multiples of 2) */

#define TC_CHAR			0x02
#define TC_BOOL			0x06
#define TC_NIL			0x0a
#define TC_UNDEFINED	0x0e

/* pointer types (multiples of 4) */
#define TC_SAVE_VAL		1<<2	/* 00000100  04 */
#define TC_ENV			2<<2	/* 00001000  08 */ /* DEPRECATED */
#define TC_CLOSURE		3<<2	/* 00001100  0c */
#define TC_SAVE			4<<2	/* 00010000  10 */
#define TC_STRING		5<<2	/* 00010100  14 */
#define TC_VECTOR		6<<2	/* 00011000  18 */
#define TC_TUPLE		7<<2	/* 00011100  1c */
#define TC_PAIR			8<<2	/* 00100000  20 */
#define TC_SYMBOL		9<<2	/* 00100100  24 */
#define TC_CODE			10<<2	/* 00101000  28 */

#define TC_LAST			TC_CODE

#define GET_TYPECODE(p)			(((ulong)(p))&0xff)
#define GET_PAYLOAD(p)			(((ulong)(p))>>8)
#define GET_TUPLE_LENGTH(p)		(((ulong)(p))>>8)
#define GET_TUPLE_SIZE(p)		((GET_TUPLE_LENGTH(p)+1)<<2)
#define TAG_VALUE(tag,value)	((SCHEME)((tag&0xff)|(value<<8)))
#define GET_STRING_POINTER(s)	((char*)(&((s)[2])))

#define IS_INTEGER(p)			(((ulong)(p)) & 1)
#define BOX_INTEGER(p)			((SCHEME)(((p)<<1)|1))
#define UNBOX_INTEGER(p)		((p)>>1)

#define IMMEDIATE(p)			(((ulong)(p)) & 3)
#define IS_TYPE(t, p)			(((ulong)(p)&0xff)==t)
#define IS_CHAR(p)				IS_TYPE (TC_CHAR, p)
#define IS_BOOL(p)				IS_TYPE (TC_BOOL, p)
#define IS_NIL(p)				IS_TYPE (TC_NIL, p)
#define IS_PAIR(p)				IS_TYPE (TC_PAIR, p)
#define IS_UNDEFINED(p)			IS_TYPE (TC_UNDEFINED, p)

#define GET_CHAR(p)				(((ulong)(p)>>8))

#define HOWMANY(x,n)			((x+n-1)/n)

// immediate constants
#define LJ_FALSE		0x00000006
#define LJ_TRUE			0x00000106
#define LJ_MAYBE		0x00000206 // just kidding
#define LJ_NIL			0x0000000a
#define LJ_UNDEFINED	0x0000000e


// builtins
typedef SCHEME * (*builtin_function) (SCHEME *);
extern builtin_function builtin_table[];
SCHEME gc_relocate (SCHEME nroots, SCHEME * start, SCHEME * finish, int delta);

// there is a temptation to use struct/typedefs for the various tuple
// types, say, L_tuple, L_closure, etc...
// however, using them consistently would complicate other parts of
// the code that treats 'tuples' identically.  I'm thinking instead
// of using macros for the slot offsets.
// 'til then, a handy reference:
// tuple  = { tag, next, arg0, arg1 ...}
// vector = { tag, v0, v1, ... }
// code   = { tag, constant-vector, code-string }
// symbol = { tag, string }
// closure= { tag, code, pc, lenv }
// save   = { tag, k, val, lenv, code, pc }
// pair   = { tag, car, cdr }

