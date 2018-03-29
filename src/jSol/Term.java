package jSol;

public enum Term {
    Program, TopDef, FunDef, CoFunDef, TypeDef, Fields, Body, Statement, Def, VarDef,
    SimpleStatement, Lambda, FunLambda, CoFunLambda, KwFun, KwId, KwEnd, KwCoFun, KwType, KwVar,
    Int, Float, Char, String, KwSymbol, KwReference, KwLambdaBegin, KwLambdaEnd, KwCFLambdaBegin,
    KwCFLambdaEnd, Epsilon;

    public boolean isEqualTo(Token token) {
        switch (token) {
            case FUN: return this.equals(KwFun);
            case COFUN: return this.equals(KwCoFun);
            case ID: return this.equals(KwId);
            case END: return this.equals(KwEnd);
            case TYPE: return this.equals(KwType);
            case VAR: return this.equals(KwVar);
            case INT: return this.equals(Int);
            case FLOAT: return this.equals(Float);
            case CHAR: return this.equals(Char);
            case STRING: return this.equals(String);
            case SYMBOL: return this.equals(KwSymbol);
            case REFERENCE: return this.equals(KwReference);
            case LAMBDABEGIN: return this.equals(KwLambdaBegin);
            case LAMBDAEND: return this.equals(KwLambdaEnd);
            case CFLAMBDABEGIN: return this.equals(KwCFLambdaBegin);
            case CFLAMBDAEND: return this.equals(KwCFLambdaEnd);
                default: return false;
        }
    }
}
