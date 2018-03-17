package jSol;

import java.util.HashMap;

import static jSol.Term.*;
import static jSol.Token.*;

public class Predict {
    private static HashMap<Term, HashMap<Token, Term[]>> productions = new HashMap<Term, HashMap<Token, Term[]>>() {{
        put(Program, new HashMap<Token, Term[]>() {{
            put(COFUN, new Term[]{TopDef, Program});
            put(FUN, new Term[]{TopDef, Program});
            put(TYPE, new Term[]{TopDef, Program});
            put(EOP, new Term[]{Epsilon});
        }});
        put(TopDef, new HashMap<Token, Term[]>() {{
            put(COFUN, new Term[]{CoFunDef});
            put(FUN, new Term[]{FunDef});
            put(TYPE, new Term[]{TypeDef});
        }});
        put(FunDef, new HashMap<Token, Term[]>() {{
            put(FUN, new Term[]{KwFun, KwId, Body, KwEnd});
        }});
        put(CoFunDef, new HashMap<Token, Term[]>() {{
            put(COFUN, new Term[]{KwCoFun, KwId, Body, KwEnd});
        }});
        put(TypeDef, new HashMap<Token, Term[]>() {{
            put(TYPE, new Term[]{KwType, KwId, Fields, KwEnd});
        }});
        put(Fields, new HashMap<Token, Term[]>() {{
            put(ID, new Term[]{KwId, Fields});
            put(END, new Term[]{Epsilon});
        }});
        put(Body, new HashMap<Token, Term[]>() {{
            put(FLOAT, new Term[]{Statement, Body});
            put(COFUN, new Term[]{Statement, Body});
            put(REFERENCE, new Term[]{Statement, Body});
            put(FUN, new Term[]{Statement, Body});
            put(ID, new Term[]{Statement, Body});
            put(LAMBDAEND, new Term[]{Epsilon});
            put(INT, new Term[]{Statement, Body});
            put(LAMBDABEGIN, new Term[]{Statement, Body});
            put(END, new Term[]{Epsilon});
            put(CHAR, new Term[]{Statement, Body});
            put(STRING, new Term[]{Statement, Body});
            put(TYPE, new Term[]{Statement, Body});
            put(VAR, new Term[]{Statement, Body});
            put(CFLAMBDABEGIN, new Term[]{Statement, Body});
            put(SYMBOL, new Term[]{Statement, Body});
            put(CFLAMBDAEND, new Term[]{Epsilon});
        }});
        put(Statement, new HashMap<Token, Term[]>() {{
            put(FLOAT, new Term[]{SimpleStatement});
            put(COFUN, new Term[]{Def});
            put(REFERENCE, new Term[]{SimpleStatement});
            put(FUN, new Term[]{Def});
            put(ID, new Term[]{SimpleStatement});
            put(INT, new Term[]{SimpleStatement});
            put(LAMBDABEGIN, new Term[]{Lambda});
            put(CHAR, new Term[]{SimpleStatement});
            put(STRING, new Term[]{SimpleStatement});
            put(TYPE, new Term[]{Def});
            put(VAR, new Term[]{Def});
            put(CFLAMBDABEGIN, new Term[]{Lambda});
            put(SYMBOL, new Term[]{SimpleStatement});
        }});
        put(Def, new HashMap<Token, Term[]>() {{
            put(COFUN, new Term[]{TopDef});
            put(FUN, new Term[]{TopDef});
            put(TYPE, new Term[]{TopDef});
            put(VAR, new Term[]{VarDef});
        }});
        put(VarDef, new HashMap<Token, Term[]>() {{
            put(VAR, new Term[]{KwVar, KwId});
        }});
        put(SimpleStatement, new HashMap<Token, Term[]>() {{
            put(FLOAT, new Term[]{Float});
            put(REFERENCE, new Term[]{KwReference});
            put(ID, new Term[]{KwId});
            put(INT, new Term[]{Int});
            put(CHAR, new Term[]{Char});
            put(STRING, new Term[]{String});
            put(SYMBOL, new Term[]{KwSymbol});
        }});
        put(Lambda, new HashMap<Token, Term[]>() {{
            put(LAMBDABEGIN, new Term[]{FunLambda});
            put(CFLAMBDABEGIN, new Term[]{CoFunLambda});
        }});
        put(FunLambda, new HashMap<Token, Term[]>() {{
            put(LAMBDABEGIN, new Term[]{KwLambdaBegin, Body, KwLambdaEnd});
        }});
        put(CoFunLambda, new HashMap<Token, Term[]>() {{
            put(CFLAMBDABEGIN, new Term[]{KwCFLambdaBegin, Body, KwCFLambdaEnd});
        }});
    }};

    public static Term[] getExpResult(Term lhs, Token input) {
        System.out.println(lhs + " " + input);
        return productions.get(lhs).get(input);
    }
}
