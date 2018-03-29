package jSol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AST {

    private HashMap<String, int[]> symbols;
    private List<AST> statements;
    private String value;
    private ASTType astType;

    AST(ASTType type) {
        symbols = new HashMap<>();
        statements = new ArrayList<>();
        value = "";
        astType = type;

    }

    public java.lang.String getValue() {
        return value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public HashMap<java.lang.String, int[]> getSymbols() {
        return symbols;
    }

    public void addSymbol(String symbol, int[] value) {
        symbols.put(symbol, value);
    }

    public List<AST> getStatements() {
        return statements;
    }

    public void addStatement(AST statement) {
        statements.add(statement);
    }

    public void print() {
        print(0);
    }

    public ASTType getAstType() {
        return astType;
    }

    public void setAstType(ASTType astType) {
        this.astType = astType;
    }

    private void print(int tabs) {
        switch (this.getAstType()) {
            case Int:
            case Char:
            case Float:
            case String:
            case RefUse:
            case VarUse:
                System.out.println(Collections.nCopies(tabs, "  ") + this.getAstType().name() + " = " + value);
                break;
            case Program:
            case Function:
            case CoRoutine:
                System.out.println(Collections.nCopies(tabs, "  ") + this.getAstType().name());
                System.out.print(Collections.nCopies(tabs + 1, "  ") +"Symbols: ");
                for (var s : symbols.entrySet()) {
                    System.out.print(s.getKey() + "(" + Arrays.toString(s.getValue()) + ") ");
                }
                System.out.println();
                System.out.print(Collections.nCopies(tabs + 1, "  ") +"Statements: ");
                for (var s : statements) {
                    print(tabs + 2);
                }
                break;
        }
    }
}
