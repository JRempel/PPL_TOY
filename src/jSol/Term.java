package jSol;

public enum Term {
    Program, TopDef, FunDef, CoFunDef, TypeDef, Fields, Body, Statement, Def, VarDef,
    SimpleStatement, Lambda, FunLambda, CoFunLambda, KwFun, KwId, KwEnd, KwCoFun, KwType, KwVar,
    Int, Float, Char, String, KwSymbol, KwReference, KwLambdaBegin, KwLambdaEnd, KwCFLambdaBegin,
    KwCFLambdaEnd, Epsilon;

    public boolean isEqualTo(Token token) {
        switch (token) {
            case INT: return this.equals(Int);
            case FLOAT: return this.equals(Float);
            case CHAR: return this.equals(Char);
            case STRING: return this.equals(String);
            case LAMBDABEGIN: return this.equals(KwLambdaBegin);
            case LAMBDAEND: return this.equals(KwLambdaEnd);
            case CFLAMBDABEGIN: return this.equals(KwCFLambdaBegin);
        }
    }
}
