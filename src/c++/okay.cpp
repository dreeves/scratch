#include <cstdio>
#include <map>
#include <string>
#include <vector>
#include <stdexcept>

/*
#include "llvm/Analysis/Passes.h"
#include "llvm/Analysis/Verifier.h"
#include "llvm/ExecutionEngine/ExecutionEngine.h"
#include "llvm/ExecutionEngine/JIT.h"
#include "llvm/DataLayout.h"
#include "llvm/DerivedTypes.h"
#include "llvm/IRBuilder.h"
#include "llvm/LLVMContext.h"
#include "llvm/Module.h"
#include "llvm/Support/TargetSelect.h"
#include "llvm/Transforms/Scalar.h"
*/
#include "llvm/PassManager.h"

using namespace std;

//===--------------------------------------------===// utils //===--------------------------------------------===//

exception err(const string& v) {return runtime_error(v);}

//===--------------------------------------------===// lexer //===--------------------------------------------===//

// The lexer returns tokens [0-255] if it is an unknown character, otherwise one
// of these for known things.
enum Token {
	tok_eof = -1,

	// commands
	tok_def = -2, tok_extern = -3,

	// primary
	tok_identifier = -4, tok_number = -5,
	
	// control
	tok_if = -6, tok_then = -7, tok_else = -8,
	tok_for = -9, tok_in = -10,
	
	// operators
	tok_binary = -11, tok_unary = -12,
	
	// var definition
	tok_var = -13
};

static string IdentifierStr;  // Filled in if tok_identifier
static double NumVal;         // Filled in if tok_number

/// gettok - Return the next token from standard input.
static int gettok() {
	static int LastChar = ' ';

	// Skip any whitespace.
	while (isspace(LastChar)) LastChar = getchar();

	if (isalpha(LastChar)) { // identifier: [a-zA-Z][a-zA-Z0-9]*
		IdentifierStr = LastChar;
		while (isalnum((LastChar = getchar()))) IdentifierStr += LastChar;
		
		if (IdentifierStr == "def") return tok_def;
		if (IdentifierStr == "extern") return tok_extern;
		if (IdentifierStr == "if") return tok_if;
		if (IdentifierStr == "then") return tok_then;
		if (IdentifierStr == "else") return tok_else;
		if (IdentifierStr == "for") return tok_for;
		if (IdentifierStr == "in") return tok_in;
		if (IdentifierStr == "binary") return tok_binary;
		if (IdentifierStr == "unary") return tok_unary;
		if (IdentifierStr == "var") return tok_var;
		return tok_identifier;
	}

	if (isdigit(LastChar) || LastChar == '.') {   // Number: [0-9.]+
		string NumStr;
		do {NumStr += LastChar; LastChar = getchar();} while (isdigit(LastChar) || LastChar == '.');
		NumVal = strtod(NumStr.c_str(), 0);
		return tok_number;
	}

	if (LastChar == '#') {
		// Comment until end of line.
		do LastChar = getchar();
		while (LastChar != EOF && LastChar != '\n' && LastChar != '\r');
		if (LastChar != EOF) return gettok();
	}
	
	// Check for end of file.  Don't eat the EOF.
	if (LastChar == EOF) return tok_eof;

	// Otherwise, just return the character as its ascii value.
	int r = LastChar; LastChar = getchar(); return r;}

// CURRENT: ? SexpAST ?

//===--------------------------------------------===// main //===--------------------------------------------===//

int main() {
	
	return 0;}

//===--------------------------------------------===// <edge> //===--------------------------------------------===//

/* one way to call c++ fns from our-lang.
vector<llvm::Type*> Doubles(1,llvm::Type::getDoubleTy(llvm::getGlobalContext()));
llvm::FunctionType* FT = llvm::FunctionType::get(llvm::Type::getDoubleTy(llvm::getGlobalContext()), Doubles, false);
llvm::Function *F = llvm::Function::Create(FT, llvm::Function::ExternalLinkage, "print", TheModule);
TheExecutionEngine->updateGlobalMapping(F,(void*)printd);
*/