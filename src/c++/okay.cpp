#include <cstdio>
#include <map>
#include <string>
#include <vector>
#include <stdexcept>

#include "llvm/Analysis/Passes.h"
#include "llvm/Analysis/Verifier.h"
//#include "llvm/ExecutionEngine/ExecutionEngine.h"
#include "llvm/ExecutionEngine/JIT.h"
//#include "llvm/DataLayout.h"
//#include "llvm/DerivedTypes.h"
#include "llvm/IRBuilder.h"
//#include "llvm/LLVMContext.h"
#include "llvm/Module.h"
#include "llvm/Support/TargetSelect.h"
#include "llvm/Transforms/Scalar.h"
#include "llvm/PassManager.h"

using namespace std;

//===--------------------------------------------===// utils //===--------------------------------------------===//

exception err(const string& v) {return runtime_error(v);}

//===--------------------------------------------===// llvm utils //===--------------------------------------------===//

llvm::LLVMContext& CTX = llvm::getGlobalContext();
llvm::IRBuilder<> builder(CTX);

namespace llvm {

// create an alloca instruction in the entry block of the function . this is used for mutable variables etc.
llvm::AllocaInst* mk_entry_block_alloca(llvm::Function* f, const string &var_name) {
	return llvm::IRBuilder<>(&f->getEntryBlock(), f->getEntryBlock().begin()).CreateAlloca(
		llvm::Type::getDoubleTy(CTX), 0, var_name.c_str());}

llvm::ExecutionEngine* mk_engine(llvm::Module* module) {
	string t; llvm::ExecutionEngine* r = llvm::EngineBuilder(module).setErrorStr(&t).create(); if (!r) throw err(t); return r;}
void mk_module(llvm::Module*& m, llvm::ExecutionEngine*& ee, llvm::FunctionPassManager*& fpm) {
	llvm::InitializeNativeTarget();
	m = new llvm::Module("module of primacy",CTX); // Make the module, which holds all the code.
	ee = llvm::mk_engine(m); // Create the JIT.  This takes ownership of the module.d
	fpm = new llvm::FunctionPassManager(m);
	fpm->add(new llvm::DataLayout(*ee->getDataLayout())); // Set up the optimizer pipeline.  Start with registering info about how the target lays out t.ructures.
	fpm->add(llvm::createBasicAliasAnalysisPass()); // Provide basic AliasAnalysis support for GVN.
	fpm->add(llvm::createPromoteMemoryToRegisterPass()); // Promote allocas to registers.
	fpm->add(llvm::createInstructionCombiningPass()); // Do simple "peephole" optimizations and bit-twiddling optzns.
	fpm->add(llvm::createReassociatePass()); // Reassociate expressions.
	fpm->add(llvm::createGVNPass()); // Eliminate Common SubExpressions.
	fpm->add(llvm::createCFGSimplificationPass()); // Simplify the control flow graph (deleting unreachable blocks, etc).
	fpm->doInitialization();
	}

}
//===--------------------------------------------===// <edge> //===--------------------------------------------===//

// http://llvm.org/docs/LangRef.html

double printd(double v) {printf("%f\n", v); return v;}

llvm::Module* module;
llvm::ExecutionEngine* engine;
llvm::FunctionPassManager* optimizer;

int main() {
	llvm::mk_module(module,engine,optimizer);

	{ // q = fn(a,b) a+b
		if (llvm::Function* f = module->getFunction("q")) f->eraseFromParent();

		vector<string> args; args.push_back("a"); args.push_back("b");
		// Make the function type:  double(double,double) etc.
		vector<llvm::Type*> doubles(args.size(), llvm::Type::getDoubleTy(CTX));
		llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getDoubleTy(CTX), doubles, false);
		llvm::Function* f = llvm::Function::Create(ftype, llvm::Function::ExternalLinkage, "q", module);
		// Set names for all arguments.
		{unsigned i = 0;
		for (llvm::Function::arg_iterator v = f->arg_begin(); i != args.size(); ++v, ++i)
			v->setName(args[i]);}
		// Create a new basic block to start insertion into.
		llvm::BasicBlock* block = llvm::BasicBlock::Create(CTX, "entry", f);
		builder.SetInsertPoint(block);
		// Add all arguments to the symbol table and create their allocas.
		//proto->CreateArgumentAllocas(f);
		/// CreateArgumentAllocas - Create an alloca for each argument and register the argument in the symbol table so that references to it will succeed.
		//void PrototypeAST::CreateArgumentAllocas(llvm::Function* F) {
			llvm::Function::arg_iterator iter = f->arg_begin();
			llvm::AllocaInst* alloca_a = llvm::mk_entry_block_alloca(f, args[0]);
			llvm::AllocaInst* alloca_b = llvm::mk_entry_block_alloca(f, args[1]);
			builder.CreateStore(iter, alloca_a); ++iter;
			builder.CreateStore(iter, alloca_b);
			//llvm::Function::arg_iterator iter = f->arg_begin();
			//for (unsigned i = 0, e = args.size(); i != e; ++i, ++iter) {
			//	// Create an alloca for this variable.
			//	llvm::AllocaInst* alloca = llvm::mk_entry_block_alloca(f, args[i]);
			//	// Store the initial value into the alloca.
			//	builder.CreateStore(iter, alloca);
			//	// Add arguments to variable symbol table.
			//	//NamedValues[args[i]] = alloca;
			//}
		//llvm::Value* ret_val = body->emit();
		llvm::Value* ret_val; {
			llvm::Value* l = builder.CreateLoad(alloca_a, "a");
			llvm::Value* r = builder.CreateLoad(alloca_b, "b");
			ret_val = builder.CreateFAdd(l, r, "addtmp");}
		// Finish off the function.
		builder.CreateRet(ret_val);
		// Validate the generated code, checking for consistency.
		llvm::verifyFunction(*f);
		// Optimize the function.
		optimizer->run(*f);
	}
	{ // print = builtin("printd")
		llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getDoubleTy(CTX), vector<llvm::Type*>(1,llvm::Type::getDoubleTy(CTX)), false);
		llvm::Function *f = llvm::Function::Create(ftype, llvm::Function::ExternalLinkage, "print", module);
		engine->updateGlobalMapping(f,(void*)printd);
	}
	{ // print(q(1,2.4))
		vector<string> args;
		// Make the function type:  double(double,double) etc.
		vector<llvm::Type*> doubles;
		llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getDoubleTy(CTX), doubles, false);
		llvm::Function* f = llvm::Function::Create(ftype, llvm::Function::ExternalLinkage, "", module);
		// Set names for all arguments.
		// noop!
		// Create a new basic block to start insertion into.
		llvm::BasicBlock* block = llvm::BasicBlock::Create(CTX, "entry", f);
		builder.SetInsertPoint(block);
		// Add all arguments to the symbol table and create their allocas.
		//proto->CreateArgumentAllocas(f);
		/// CreateArgumentAllocas - Create an alloca for each argument and register the argument in the symbol table so that references to it will succeed.
		//void PrototypeAST::CreateArgumentAllocas(llvm::Function* F) {
		//	llvm::Function::arg_iterator iter = f->arg_begin();
		//	for (unsigned i = 0, e = args.size(); i != e; ++i, ++iter) {
		//		// Create an alloca for this variable.
		//		llvm::AllocaInst* alloca = llvm::mk_entry_block_alloca(f, args[i]);
		//		// Store the initial value into the alloca.
		//		builder.CreateStore(iter, alloca);
		//		// Add arguments to variable symbol table.
		//		//NamedValues[args[i]] = alloca;
		//	}
		// ??
		llvm::Value* qval; {
			llvm::Function* f = module->getFunction("q");
			vector<llvm::Value*> args;
			args.push_back(llvm::ConstantFP::get(CTX, llvm::APFloat(1.0)));
			args.push_back(llvm::ConstantFP::get(CTX, llvm::APFloat(2.4)));
			qval = builder.CreateCall(f,args,"calltmp");}
		llvm::Value* printval; {
			llvm::Function* f = module->getFunction("print");
			vector<llvm::Value*> args; args.push_back(qval);
			printval = builder.CreateCall(f,args,"calltmp");}
		//llvm::Value* ret_val = body->emit();
		//llvm::Value* ret_val = llvm::ConstantFP::get(CTX, llvm::APFloat(1.0));
		llvm::Value* ret_val = printval;
		// Finish off the function.
		builder.CreateRet(ret_val);
		// Validate the generated code, checking for consistency.
		llvm::verifyFunction(*f);
		// Optimize the function.
		optimizer->run(*f);

		// JIT the function, returning a function pointer.
		void* fcv = engine->getPointerToFunction(f);
		// Cast it to the right type (takes no arguments, returns a double) so we can call it as a native function.
		double(*fc)() = (double(*)())(intptr_t)fcv;
		printf("Evaluated to %f\n", fc());
	}
	{ // q = fn(a,b,c) a*b-c
		if (llvm::Function* f = module->getFunction("q")) f->eraseFromParent();

		vector<string> args; args.push_back("a"); args.push_back("b"); args.push_back("c");
		// Make the function type:  double(double,double) etc.
		vector<llvm::Type*> doubles(args.size(), llvm::Type::getDoubleTy(CTX));
		llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getDoubleTy(CTX), doubles, false);
		llvm::Function* f = llvm::Function::Create(ftype, llvm::Function::ExternalLinkage, "q", module);
		// Set names for all arguments.
		{unsigned i = 0;
		for (llvm::Function::arg_iterator v = f->arg_begin(); i != args.size(); ++v, ++i)
			v->setName(args[i]);}
		// Create a new basic block to start insertion into.
		llvm::BasicBlock* block = llvm::BasicBlock::Create(CTX, "entry", f);
		builder.SetInsertPoint(block);
		// Add all arguments to the symbol table and create their allocas.
		//proto->CreateArgumentAllocas(f);
		/// CreateArgumentAllocas - Create an alloca for each argument and register the argument in the symbol table so that references to it will succeed.
		//void PrototypeAST::CreateArgumentAllocas(llvm::Function* F) {
			llvm::Function::arg_iterator iter = f->arg_begin();
			llvm::AllocaInst* alloca_a = llvm::mk_entry_block_alloca(f, args[0]);
			llvm::AllocaInst* alloca_b = llvm::mk_entry_block_alloca(f, args[1]);
			llvm::AllocaInst* alloca_c = llvm::mk_entry_block_alloca(f, args[2]);
			builder.CreateStore(iter, alloca_a); ++iter;
			builder.CreateStore(iter, alloca_b); ++iter;
			builder.CreateStore(iter, alloca_c);
			//llvm::Function::arg_iterator iter = f->arg_begin();
			//for (unsigned i = 0, e = args.size(); i != e; ++i, ++iter) {
			//	// Create an alloca for this variable.
			//	llvm::AllocaInst* alloca = llvm::mk_entry_block_alloca(f, args[i]);
			//	// Store the initial value into the alloca.
			//	builder.CreateStore(iter, alloca);
			//	// Add arguments to variable symbol table.
			//	//NamedValues[args[i]] = alloca;
			//}
		//llvm::Value* ret_val = body->emit();
		llvm::Value* ret_val; {
			llvm::Value* l = builder.CreateLoad(alloca_a, "a");
			llvm::Value* r = builder.CreateLoad(alloca_b, "b");
			ret_val = builder.CreateFMul(l, r, "multmp");}
			{
			llvm::Value* l = ret_val;
			llvm::Value* r = builder.CreateLoad(alloca_c, "c");
			ret_val = builder.CreateFSub(l, r, "subtmp");}
		// Finish off the function.
		builder.CreateRet(ret_val);
		// Validate the generated code, checking for consistency.
		llvm::verifyFunction(*f);
		// Optimize the function.
		optimizer->run(*f);
	}
	{ // print(q(1.1,2.4,9))
		vector<string> args;
		// Make the function type:  double(double,double) etc.
		vector<llvm::Type*> doubles;
		llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getDoubleTy(CTX), doubles, false);
		llvm::Function* f = llvm::Function::Create(ftype, llvm::Function::ExternalLinkage, "", module);
		// Set names for all arguments.
		// noop!
		// Create a new basic block to start insertion into.
		llvm::BasicBlock* block = llvm::BasicBlock::Create(CTX, "entry", f);
		builder.SetInsertPoint(block);
		// Add all arguments to the symbol table and create their allocas.
		//proto->CreateArgumentAllocas(f);
		/// CreateArgumentAllocas - Create an alloca for each argument and register the argument in the symbol table so that references to it will succeed.
		//void PrototypeAST::CreateArgumentAllocas(llvm::Function* F) {
		//	llvm::Function::arg_iterator iter = f->arg_begin();
		//	for (unsigned i = 0, e = args.size(); i != e; ++i, ++iter) {
		//		// Create an alloca for this variable.
		//		llvm::AllocaInst* alloca = llvm::mk_entry_block_alloca(f, args[i]);
		//		// Store the initial value into the alloca.
		//		builder.CreateStore(iter, alloca);
		//		// Add arguments to variable symbol table.
		//		//NamedValues[args[i]] = alloca;
		//	}
		// ??
		llvm::Value* qval; {
			llvm::Function* f = module->getFunction("q");
			vector<llvm::Value*> args;
			args.push_back(llvm::ConstantFP::get(CTX, llvm::APFloat(1.1)));
			args.push_back(llvm::ConstantFP::get(CTX, llvm::APFloat(2.4)));
			args.push_back(llvm::ConstantFP::get(CTX, llvm::APFloat(9.0)));
			qval = builder.CreateCall(f,args,"calltmp");}
		llvm::Value* printval; {
			llvm::Function* f = module->getFunction("print");
			vector<llvm::Value*> args; args.push_back(qval);
			printval = builder.CreateCall(f,args,"calltmp");}
		//llvm::Value* ret_val = body->emit();
		//llvm::Value* ret_val = llvm::ConstantFP::get(CTX, llvm::APFloat(1.0));
		llvm::Value* ret_val = printval;
		// Finish off the function.
		builder.CreateRet(ret_val);
		// Validate the generated code, checking for consistency.
		llvm::verifyFunction(*f);
		// Optimize the function.
		optimizer->run(*f);

		// JIT the function, returning a function pointer.
		void* fcv = engine->getPointerToFunction(f);
		// Cast it to the right type (takes no arguments, returns a double) so we can call it as a native function.
		double(*fc)() = (double(*)())(intptr_t)fcv;
		printf("Evaluated to %f\n", fc());
	}

	printf("--------------\n");
	module->dump();
	return 0;}