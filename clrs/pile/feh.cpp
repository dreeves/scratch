//===--------------------------------------------===// Lexer //===--------------------------------------------===//

// The lexer returns tokens [0-255] if it is an unknown character, otherwise one
// of these for known things.
enum Token {
	tok_eof = -1,
	tok_def = -2, tok_extern = -3, // commands
	tok_identifier = -4, tok_number = -5, // primary
	tok_if = -6, tok_then = -7, tok_else = -8, tok_for = -9, tok_in = -10, // control
	tok_binary = -11, tok_unary = -12, // operators
	tok_var = -13 // var definition
	};
string IdentifierStr;  // Filled in if tok_identifier
double NumVal;         // Filled in if tok_number

/// gettok - Return the next token from standard input.
int gettok() {
	static int last_ch = ' ';

	// Skip any whitespace.
	while (isspace(last_ch)) last_ch = getchar();

	if (isalpha(last_ch)) { // identifier: [a-zA-Z][a-zA-Z0-9]*
		IdentifierStr = last_ch;
		while (isalnum((last_ch = getchar())))
			IdentifierStr += last_ch;

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

	if (isdigit(last_ch) || last_ch == '.') { // Number: [0-9.]+
		string t;
		do {t += last_ch; last_ch = getchar();} while (isdigit(last_ch) || last_ch == '.');
		NumVal = strtod(t.c_str(), 0);
		return tok_number;
	}

	if (last_ch == '#') {
		// Comment until end of line.
		do last_ch = getchar(); while (last_ch != EOF && last_ch != '\n' && last_ch != '\r');
		if (last_ch != EOF) return gettok();
	}
	
	// Check for end of file.  Don't eat the EOF.
	if (last_ch == EOF) return tok_eof;

	// Otherwise, just return the character as its ascii value.
	int r = last_ch; last_ch = getchar(); return r;}

//===--------------------------------------------===// Abstract Syntax Tree (aka Parse Tree) //===--------------------------------------------===//

// CURRENT: ? SexpAST ?

class ExprAST {public:
	virtual ~ExprAST() {}
	virtual llvm::Value* emit() = 0;
	};
class NumberExprAST : public ExprAST {double val; public:
	NumberExprAST(double val) : val(val) {}
	virtual llvm::Value* emit();
	};
class VariableExprAST : public ExprAST {string name; public:
	VariableExprAST(const string &name) : name(name) {}
	const string &getName() const {return name;}
	virtual llvm::Value* emit();
	};
class UnaryExprAST : public ExprAST {char opcode; ExprAST* operand; public:
	UnaryExprAST(char opcode, ExprAST* operand) : opcode(opcode), operand(operand) {}
	virtual llvm::Value* emit();
	};
class BinaryExprAST : public ExprAST {char op; ExprAST* lhs, *rhs; public:
	BinaryExprAST(char op, ExprAST* lhs, ExprAST* rhs) : op(op), lhs(lhs), rhs(rhs) {}
	virtual llvm::Value* emit();
	};
class CallExprAST : public ExprAST {string callee; vector<ExprAST*> args; public:
	CallExprAST(const string &callee, vector<ExprAST*> &args) : callee(callee), args(args) {}
	virtual llvm::Value* emit();
	};
class IfExprAST : public ExprAST {ExprAST* cond, *then, *else_; public:
	IfExprAST(ExprAST* cond, ExprAST* then, ExprAST* else_) : cond(cond), then(then), else_(else_) {}
	virtual llvm::Value* emit();
	};
class ForExprAST : public ExprAST {string var_name; ExprAST* start, *end, *step, *body; public:
	ForExprAST(const string &var_name, ExprAST* start, ExprAST* end, ExprAST* step, ExprAST* body) : var_name(var_name), start(start), end(end), step(step), body(body) {}
	virtual llvm::Value* emit();
	};
class VarExprAST : public ExprAST {vector<pair<string, ExprAST*> > var_names; ExprAST* body; public:
	VarExprAST(const vector<pair<string, ExprAST*> > &var_names, ExprAST* body) : var_names(var_names), body(body) {}
	virtual llvm::Value* emit();
	};
// this class represents the "prototype" for a function, which captures its argument names as well as if it is an operator.
class PrototypeAST {string name; vector<string> args; bool is_op; unsigned precedence; public:
	// ^ Precedence if a binary op.
	PrototypeAST(const string &name, const vector<string> &args, bool is_op = false, unsigned precedence = 0) : name(name), args(args), is_op(is_op), precedence(precedence) {}
	
	bool isUnaryOp() const {return is_op && args.size() == 1;}
	bool isBinaryOp() const {return is_op && args.size() == 2;}
	
	char getOperatorName() const {assert(isUnaryOp() || isBinaryOp()); return name[name.size()-1];}
	unsigned getBinaryPrecedence() const {return precedence;}
	llvm::Function* emit();
	void CreateArgumentAllocas(llvm::Function* F);
	};
class FunctionAST {PrototypeAST* proto; ExprAST* body; public:
	FunctionAST(PrototypeAST* proto, ExprAST* body) : proto(proto), body(body) {}
	llvm::Function* emit();
	};

//===--------------------------------------------===// Parser //===--------------------------------------------===//

/// cur_tok/getNextToken - Provide a simple token buffer.  cur_tok is the current token the parser is looking at.  getNextToken reads another token from the lexer and updates cur_tok with its results.
int cur_tok;
int getNextToken() {return (cur_tok = gettok());}

/// binop_precedence - This holds the precedence for each binary operator that is defined.
map<char, int> binop_precedence;

/// get_tok_precedence - Get the precedence of the pending binary operator token.
int get_tok_precedence() {if (!isascii(cur_tok)) return -1; int r = binop_precedence[cur_tok]; return r <= 0? -1 : r;}

/// Error* - These are little helper functions for error handling.
ExprAST* Error(const char* v) {printf("Error: %s\n", v); return NULL;}
PrototypeAST* ErrorP(const char* v) {Error(v); return NULL;}
FunctionAST* ErrorF(const char* v) {Error(v); return NULL;}

ExprAST* ParseExpression();

ExprAST* ParseIdentifierExpr() { /// identifierexpr ::= identifier ( '(' expression* ')' )?
	string tok = IdentifierStr;
	
	getNextToken();  // eat identifier.
	
	if (cur_tok != '(') // Simple variable ref.
		return new VariableExprAST(tok);
	
	// Call.
	getNextToken();  // eat (
	vector<ExprAST*> args;
	if (cur_tok != ')') {
		while (1) {
			ExprAST* v = ParseExpression();
			if (!v) return NULL;
			args.push_back(v);
			if (cur_tok == ')') break;
			if (cur_tok != ',') return Error("Expected ')' or ',' in argument list");
			getNextToken();
		}
	}

	// Eat the ')'.
	getNextToken();
	
	return new CallExprAST(tok, args);}
ExprAST* ParseNumberExpr() {ExprAST* r = new NumberExprAST(NumVal); getNextToken(); return r;} /// numberexpr ::= number
ExprAST* ParseParenExpr() { /// parenexpr ::= '(' expression ')'
	getNextToken();  // eat (.
	ExprAST* r = ParseExpression();
	if (!r) return NULL;
	if (cur_tok != ')') return Error("expected ')'");
	getNextToken();  // eat ).
	return r;}
ExprAST* ParseIfExpr() { /// ifexpr ::= 'if' expression 'then' expression 'else' expression
	getNextToken();  // eat the if.
	ExprAST* cond = ParseExpression(); if (!cond) return NULL;
	if (cur_tok != tok_then) return Error("expected then");
	getNextToken();  // eat the then
	ExprAST* then = ParseExpression(); if (!then) return NULL;
	if (cur_tok != tok_else) return Error("expected else");
	getNextToken();
	ExprAST* else_ = ParseExpression(); if (!else_) return NULL;
	return new IfExprAST(cond, then, else_);}
ExprAST* ParseForExpr() { /// forexpr ::= 'for' identifier '=' expr ',' expr (',' expr)? 'in' expression
	getNextToken();  // eat the for.

	if (cur_tok != tok_identifier) return Error("expected identifier after for");
	
	string tok = IdentifierStr;
	getNextToken();  // eat identifier.
	
	if (cur_tok != '=') return Error("expected '=' after for");
	getNextToken();  // eat '='.
	
	ExprAST* start = ParseExpression(); if (!start) return NULL;
	if (cur_tok != ',') return Error("expected ',' after for start value");
	getNextToken();
	
	ExprAST* end = ParseExpression(); if (!end) return NULL;
	
	// The step value is optional.
	ExprAST* step = NULL;
	if (cur_tok == ',') {getNextToken(); step = ParseExpression(); if (!step) return NULL;}
	
	if (cur_tok != tok_in) return Error("expected 'in' after for");
	getNextToken();  // eat 'in'.
	
	ExprAST* body = ParseExpression(); if (!body) return NULL;

	return new ForExprAST(tok, start, end, step, body);}
ExprAST* ParseVarExpr() { /// varexpr ::= 'var' identifier ('=' expression)? (',' identifier ('=' expression)?)* 'in' expression
	getNextToken();  // eat the var.

	vector<pair<string, ExprAST*> > var_names;

	// At least one variable name is required.
	if (cur_tok != tok_identifier) return Error("expected identifier after var");
	
	while (1) {
		string Name = IdentifierStr;
		getNextToken();  // eat identifier.

		// Read the optional initializer.
		ExprAST* Init = NULL;
		if (cur_tok == '=') {getNextToken(); Init = ParseExpression(); if (!Init) return NULL;}
		
		var_names.push_back(make_pair(Name, Init));
		
		// End of var list, exit loop.
		if (cur_tok != ',') break;
		getNextToken(); // eat the ','.
		
		if (cur_tok != tok_identifier) return Error("expected identifier list after var");
	}
	
	// At this point, we have to have 'in'.
	if (cur_tok != tok_in) return Error("expected 'in' keyword after 'var'");
	getNextToken();  // eat 'in'.
	
	ExprAST* Body = ParseExpression();
	if (!Body) return NULL;
	
	return new VarExprAST(var_names, Body);}
ExprAST* ParsePrimary() { /// primary ::= identifierexpr | numberexpr | parenexpr | ifexpr | forexpr | varexpr
	switch (cur_tok) {
		default:             return Error("unknown token when expecting an expression");
		case tok_identifier: return ParseIdentifierExpr();
		case tok_number:     return ParseNumberExpr();
		case '(':            return ParseParenExpr();
		case tok_if:         return ParseIfExpr();
		case tok_for:        return ParseForExpr();
		case tok_var:        return ParseVarExpr();
		}}
ExprAST* ParseUnary() { /// unary ::= primary | ( '!' unary )
	// If the current token is not an operator, it must be a primary expr.
	if (!isascii(cur_tok) || cur_tok == '(' || cur_tok == ',')
		return ParsePrimary();
	
	// If this is a unary operator, read it.
	int Opc = cur_tok;
	getNextToken();
	if (ExprAST* Operand = ParseUnary())
		return new UnaryExprAST(Opc, Operand);
	return NULL;}
ExprAST* ParseBinOpRHS(int ExprPrec, ExprAST* LHS) { /// binoprhs ::= ('+' unary)*
	// If this is a binop, find its precedence.
	while (1) {
		int TokPrec = get_tok_precedence();
		
		// If this is a binop that binds at least as tightly as the current binop,
		// consume it, otherwise we are done.
		if (TokPrec < ExprPrec) return LHS;
		
		// Okay, we know this is a binop.
		int BinOp = cur_tok;
		getNextToken();  // eat binop
		
		// Parse the unary expression after the binary operator.
		ExprAST* RHS = ParseUnary();
		if (!RHS) return NULL;
		
		// If BinOp binds less tightly with RHS than the operator after RHS, let
		// the pending operator take RHS as its LHS.
		int NextPrec = get_tok_precedence();
		if (TokPrec < NextPrec) {RHS = ParseBinOpRHS(TokPrec+1, RHS); if (!RHS) return NULL;}
		
		// Merge LHS/RHS.
		LHS = new BinaryExprAST(BinOp, LHS, RHS);
	}}
ExprAST* ParseExpression() {ExprAST* LHS = ParseUnary(); return LHS? ParseBinOpRHS(NULL, LHS) : NULL;} /// expression ::= unary binoprhs
PrototypeAST* ParsePrototype() { /// prototype ::= ( id '(' id* ')' ) | ( binary LETTER number? (id, id) ) | ( unary LETTER (id) )
	string FnName;
	
	unsigned Kind = 0; // 0 = identifier, 1 = unary, 2 = binary.
	unsigned BinaryPrecedence = 30;
	
	switch (cur_tok) {
		default:
			return ErrorP("Expected function name in prototype");
		case tok_identifier:
			FnName = IdentifierStr;
			Kind = 0;
			getNextToken();
			break;
		case tok_unary:
			getNextToken();
			if (!isascii(cur_tok))
				return ErrorP("Expected unary operator");
			FnName = "unary";
			FnName += (char)cur_tok;
			Kind = 1;
			getNextToken();
			break;
		case tok_binary:
			getNextToken();
			if (!isascii(cur_tok))
				return ErrorP("Expected binary operator");
			FnName = "binary";
			FnName += (char)cur_tok;
			Kind = 2;
			getNextToken();
			
			// Read the precedence if present.
			if (cur_tok == tok_number) {
				if (NumVal < 1 || NumVal > 100)
					return ErrorP("Invalid precedecnce: must be 1..100");
				BinaryPrecedence = (unsigned)NumVal;
				getNextToken();
			}
			break;
	}
	
	if (cur_tok != '(') return ErrorP("Expected '(' in prototype");
	
	vector<string> ArgNames;
	while (getNextToken() == tok_identifier) ArgNames.push_back(IdentifierStr);
	if (cur_tok != ')') return ErrorP("Expected ')' in prototype");
	
	// success.
	getNextToken();  // eat ')'.
	
	// Verify right number of names for operator.
	if (Kind && ArgNames.size() != Kind) return ErrorP("Invalid number of operands for operator");
	
	return new PrototypeAST(FnName, ArgNames, Kind != 0, BinaryPrecedence);}
FunctionAST* ParseDefinition() { /// definition ::= 'def' prototype expression
	getNextToken();  // eat def.
	PrototypeAST* Proto = ParsePrototype();
	if (!Proto) return NULL;
	if (ExprAST* E = ParseExpression()) return new FunctionAST(Proto, E);
	return NULL;}
FunctionAST* ParseTopLevelExpr() { /// toplevelexpr ::= expression
	if (ExprAST* E = ParseExpression()) {PrototypeAST* Proto = new PrototypeAST("", vector<string>()); return new FunctionAST(Proto, E);}
	else return NULL;}
PrototypeAST* ParseExtern() {getNextToken(); return ParsePrototype();} /// external ::= 'extern' prototype

//===--------------------------------------------===// Code Generation //===--------------------------------------------===//

llvm::Module* module;
llvm::IRBuilder<> Builder(llvm::getGlobalContext());
map<string, llvm::AllocaInst*> NamedValues;
llvm::FunctionPassManager* fpm;

llvm::Value* ErrorV(const char* Str) {Error(Str); return NULL;}

llvm::Value* NumberExprAST::emit() {return llvm::ConstantFP::get(llvm::getGlobalContext(), llvm::APFloat(val));}
llvm::Value* VariableExprAST::emit() {
	// Look this variable up in the function.
	llvm::Value* V = NamedValues[name];
	if (!V) return ErrorV("Unknown variable name");
	// Load the value.
	return Builder.CreateLoad(V, name.c_str());}
llvm::Value* UnaryExprAST::emit() {
	llvm::Value* OperandV = operand->emit();
	if (!OperandV) return NULL;
	
	llvm::Function* F = module->getFunction(string("unary")+opcode);
	if (!F) return ErrorV("Unknown unary operator");
	
	return Builder.CreateCall(F, OperandV, "unop");}
llvm::Value* BinaryExprAST::emit() {
	// Special case '=' because we don't want to emit the LHS as an expression.
	if (op == '=') {
		// Assignment requires the LHS to be an identifier.
		VariableExprAST* LHSE = dynamic_cast<VariableExprAST*>(lhs);
		if (!LHSE) return ErrorV("destination of '=' must be a variable");

		// emit the RHS.
		llvm::Value* v = rhs->emit();
		if (!v) return NULL;

		// Look up the name.
		llvm::Value* Variable = NamedValues[LHSE->getName()];
		if (!Variable) return ErrorV("Unknown variable name");

		Builder.CreateStore(v, Variable);
		return v;
	}
	
	llvm::Value* L = lhs->emit();
	llvm::Value* R = rhs->emit();
	if (!L || !R) return NULL;
	
	switch (op) {
		case '+': return Builder.CreateFAdd(L, R, "addtmp");
		case '-': return Builder.CreateFSub(L, R, "subtmp");
		case '*': return Builder.CreateFMul(L, R, "multmp");    
		case '<':
			L = Builder.CreateFCmpULT(L, R, "cmptmp");
			// Convert bool 0/1 to double 0.0 or 1.0
			return Builder.CreateUIToFP(L, llvm::Type::getDoubleTy(llvm::getGlobalContext()), "booltmp");
		default: break;
	}
	
	// If it wasn't a builtin binary operator, it must be a user defined one. Emit a call to it.
	llvm::Function* F = module->getFunction(string("binary")+op);
	assert(F && "binary operator not found!");
	
	llvm::Value* Ops[] = {L, R};
	return Builder.CreateCall(F, Ops, "binop");}
llvm::Value* CallExprAST::emit() {
	// Look up the name in the global module table.
	llvm::Function* CalleeF = module->getFunction(callee);
	if (!CalleeF) return ErrorV("Unknown function referenced");
	
	// If argument mismatch error.
	if (CalleeF->arg_size() != args.size()) return ErrorV("Incorrect # arguments passed");

	vector<llvm::Value*> ArgsV;
	for (unsigned i = 0, e = args.size(); i != e; ++i) {
		ArgsV.push_back(args[i]->emit());
		if (!ArgsV.back()) return NULL;
	}
	
	return Builder.CreateCall(CalleeF, ArgsV, "calltmp");}
llvm::Value* IfExprAST::emit() {
	llvm::Value* CondV = cond->emit(); if (!CondV) return NULL;
	
	// Convert condition to a bool by comparing equal to 0.0.
	CondV = Builder.CreateFCmpONE(CondV, llvm::ConstantFP::get(llvm::getGlobalContext(), llvm::APFloat(0.0)), "ifcond");
	
	llvm::Function* TheFunction = Builder.GetInsertBlock()->getParent();
	
	// Create blocks for the then and else cases.  Insert the 'then' block at the end of the function.
	llvm::BasicBlock* ThenBB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "then", TheFunction);
	llvm::BasicBlock* ElseBB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "else");
	llvm::BasicBlock* MergeBB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "ifcont");
	
	Builder.CreateCondBr(CondV, ThenBB, ElseBB);
	
	// Emit then value.
	Builder.SetInsertPoint(ThenBB);
	
	llvm::Value* ThenV = then->emit();
	if (!ThenV) return NULL;
	
	Builder.CreateBr(MergeBB);
	// emit of 'Then' can change the current block, update ThenBB for the PHI.
	ThenBB = Builder.GetInsertBlock();
	
	// Emit else block.
	TheFunction->getBasicBlockList().push_back(ElseBB);
	Builder.SetInsertPoint(ElseBB);
	
	llvm::Value* ElseV = else_->emit();
	if (!ElseV) return NULL;
	
	Builder.CreateBr(MergeBB);
	// emit of 'Else' can change the current block, update ElseBB for the PHI.
	ElseBB = Builder.GetInsertBlock();
	
	// Emit merge block.
	TheFunction->getBasicBlockList().push_back(MergeBB);
	Builder.SetInsertPoint(MergeBB);
	llvm::PHINode* r = Builder.CreatePHI(llvm::Type::getDoubleTy(llvm::getGlobalContext()), 2, "iftmp");
	r->addIncoming(ThenV, ThenBB);
	r->addIncoming(ElseV, ElseBB);
	return r;}
llvm::Value* ForExprAST::emit() {
	// Output this as:
	//   var = alloca double
	//   ...
	//   start = startexpr
	//   store start -> var
	//   goto loop
	// loop: 
	//   ...
	//   bodyexpr
	//   ...
	// loopend:
	//   step = stepexpr
	//   endcond = endexpr
	//
	//   curvar = load var
	//   nextvar = curvar + step
	//   store nextvar -> var
	//   br endcond, loop, endloop
	// outloop:
	
	llvm::Function* TheFunction = Builder.GetInsertBlock()->getParent();

	// Create an alloca for the variable in the entry block.
	llvm::AllocaInst* Alloca = CreateEntryBlockAlloca(TheFunction, var_name);
	
	// Emit the start code first, without 'variable' in scope.
	llvm::Value* StartVal = start->emit();
	if (!StartVal) return NULL;
	
	// Store the value into the alloca.
	Builder.CreateStore(StartVal, Alloca);
	
	// Make the new basic block for the loop header, inserting after current block.
	llvm::BasicBlock* LoopBB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "loop", TheFunction);
	
	// Insert an explicit fall through from the current block to the LoopBB.
	Builder.CreateBr(LoopBB);

	// Start insertion in LoopBB.
	Builder.SetInsertPoint(LoopBB);
	
	// Within the loop, the variable is defined equal to the PHI node. If it shadows an existing variable, we have to restore it, so save it now.
	llvm::AllocaInst* OldVal = NamedValues[var_name];
	NamedValues[var_name] = Alloca;
	
	// Emit the body of the loop.  This, like any other expr, can change the current BB.  Note that we ignore the value computed by the body, but don't allow an error.
	if (!body->emit()) return NULL;
	
	// Emit the step value.
	llvm::Value* StepVal;
	if (step) {StepVal = step->emit(); if (!StepVal) return NULL;}
	else {StepVal = llvm::ConstantFP::get(llvm::getGlobalContext(), llvm::APFloat(1.0));} // If not specified, use 1.0.
	
	// Compute the end condition.
	llvm::Value* EndCond = end->emit(); if (!EndCond) return NULL;
	
	// Reload, increment, and restore the alloca.  This handles the case where the body of the loop mutates the variable.
	llvm::Value* CurVar = Builder.CreateLoad(Alloca, var_name.c_str());
	llvm::Value* NextVar = Builder.CreateFAdd(CurVar, StepVal, "nextvar");
	Builder.CreateStore(NextVar, Alloca);
	
	// Convert condition to a bool by comparing equal to 0.0.
	EndCond = Builder.CreateFCmpONE(EndCond, llvm::ConstantFP::get(llvm::getGlobalContext(), llvm::APFloat(0.0)), "loopcond");
	
	// Create the "after loop" block and insert it.
	llvm::BasicBlock* AfterBB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "afterloop", TheFunction);
	
	// Insert the conditional branch into the end of LoopEndBB.
	Builder.CreateCondBr(EndCond, LoopBB, AfterBB);
	
	// Any new code will be inserted in AfterBB.
	Builder.SetInsertPoint(AfterBB);
	
	// Restore the unshadowed variable.
	if (OldVal) NamedValues[var_name] = OldVal;
	else NamedValues.erase(var_name);

	// for expr always returns 0.0.
	return llvm::Constant::getNullValue(llvm::Type::getDoubleTy(llvm::getGlobalContext()));}
llvm::Value* VarExprAST::emit() {
	vector<llvm::AllocaInst *> OldBindings;
	
	llvm::Function* TheFunction = Builder.GetInsertBlock()->getParent();

	// Register all variables and emit their initializer.
	for (unsigned i = 0, e = var_names.size(); i != e; ++i) {
		const string &var_name = var_names[i].first;
		ExprAST* Init = var_names[i].second;
		
		// Emit the initializer before adding the variable to scope, this prevents the initializer from referencing the variable itself, and permits stuff like this:
		//  var a = 1 in
		//    var a = a in ...   # refers to outer 'a'.
		llvm::Value* InitVal;
		if (Init) {InitVal = Init->emit(); if (!InitVal) return NULL;}
		else InitVal = llvm::ConstantFP::get(llvm::getGlobalContext(), llvm::APFloat(0.0)); // If not specified, use 0.0.
		
		llvm::AllocaInst* Alloca = CreateEntryBlockAlloca(TheFunction, var_name);
		Builder.CreateStore(InitVal, Alloca);

		// Remember the old variable binding so that we can restore the binding when we unrecurse.
		OldBindings.push_back(NamedValues[var_name]);
		
		// Remember this binding.
		NamedValues[var_name] = Alloca;
	}
	
	// emit the body, now that all vars are in scope.
	llvm::Value* r = body->emit();
	if (!r) return NULL;
	
	// Pop all our variables from scope.
	for (unsigned i = 0, e = var_names.size(); i != e; ++i)
		NamedValues[var_names[i].first] = OldBindings[i];

	// Return the body computation.
	return r;}
llvm::Function* PrototypeAST::emit() {
	// Make the function type:  double(double,double) etc.
	vector<llvm::Type*> Doubles(args.size(), llvm::Type::getDoubleTy(llvm::getGlobalContext()));
	llvm::FunctionType* FT = llvm::FunctionType::get(llvm::Type::getDoubleTy(llvm::getGlobalContext()), Doubles, false);
	llvm::Function* r = llvm::Function::Create(FT, llvm::Function::ExternalLinkage, name, module);
	
	// If F conflicted, there was already something named 'Name'.  If it has a body, don't allow redefinition or reextern.
	if (r->getName() != name) {
		// Delete the one we just made and get the existing one.
		r->eraseFromParent();
		r = module->getFunction(name);
		
		// If F already has a body, reject this.
		if (!r->empty()) {ErrorF("redefinition of function"); return NULL;}
		
		// If F took a different number of args, reject.
		if (r->arg_size() != args.size()) {ErrorF("redefinition of function with different # args"); return NULL;}
	}
	
	// Set names for all arguments.
	unsigned Idx = 0;
	for (llvm::Function::arg_iterator v = r->arg_begin(); Idx != args.size(); ++v, ++Idx)
		v->setName(args[Idx]);
		
	return r;}

/// CreateArgumentAllocas - Create an alloca for each argument and register the argument in the symbol table so that references to it will succeed.
void PrototypeAST::CreateArgumentAllocas(llvm::Function* F) {
	llvm::Function::arg_iterator AI = F->arg_begin();
	for (unsigned Idx = 0, e = args.size(); Idx != e; ++Idx, ++AI) {
		// Create an alloca for this variable.
		llvm::AllocaInst* Alloca = CreateEntryBlockAlloca(F, args[Idx]);

		// Store the initial value into the alloca.
		Builder.CreateStore(AI, Alloca);

		// Add arguments to variable symbol table.
		NamedValues[args[Idx]] = Alloca;
	}
	}

llvm::Function* FunctionAST::emit() {
	NamedValues.clear();
	
	llvm::Function* r = proto->emit();
	if (!r) return NULL;
	
	// If this is an operator, install it.
	if (proto->isBinaryOp())
		binop_precedence[proto->getOperatorName()] = proto->getBinaryPrecedence();
	
	// Create a new basic block to start insertion into.
	llvm::BasicBlock* BB = llvm::BasicBlock::Create(llvm::getGlobalContext(), "entry", r);
	Builder.SetInsertPoint(BB);
	
	// Add all arguments to the symbol table and create their allocas.
	proto->CreateArgumentAllocas(r);

	if (llvm::Value* RetVal = body->emit()) {
		// Finish off the function.
		Builder.CreateRet(RetVal);
		// Validate the generated code, checking for consistency.
		verifyFunction(*r);
		// Optimize the function.
		fpm->run(*r);
		return r;}
	
	// Error reading body, remove function.
	r->eraseFromParent();

	if (proto->isBinaryOp()) binop_precedence.erase(proto->getOperatorName());
	return NULL;}

//===--------------------------------------------===// main //===--------------------------------------------===//

/// top ::= definition | expression
void MainLoop(llvm::ExecutionEngine* engine) {
	printf("ready> ");
	getNextToken();
	for(;;){
		printf("ready> ");
		switch (cur_tok) {
			case tok_eof: return;
			case tok_def: {
				if (FunctionAST* v = ParseDefinition()) {
					if (llvm::Function* vc = v->emit()) {printf("Read function definition:"); vc->dump();}
				} else getNextToken(); // Skip token for error recovery.
				break;}
			default: {
				// Evaluate a top-level expression into an anonymous function.
				if (FunctionAST* F = ParseTopLevelExpr()) {
					if (llvm::Function* LF = F->emit()) {
						// JIT the function, returning a function pointer.
						void* f = engine->getPointerToFunction(LF);
						// Cast it to the right type (takes no arguments, returns a double) so we can call it as a native function.
						double (*fc)() = (double (*)())(intptr_t)f;
						printf("Evaluated to %f\n", fc());
					}
				} else getNextToken(); // Skip token for error recovery.
				break;}
		}
	}
	}

int main() {
	// Install standard binary operators. (1 is lowest precedence.)
	binop_precedence['='] = 2;
	binop_precedence['<'] = 10;
	binop_precedence['+'] = 20;
	binop_precedence['-'] = 20;
	binop_precedence['*'] = 40;  // highest.

	// ...

	MainLoop(engine); // the main "interpreter loop"

	// ...
	}