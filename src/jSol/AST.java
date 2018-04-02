package jSol;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AST {

    private List<Map.Entry<String, int[]>> symbols;
    private List<AST> statements;
    private String value;
    private int[] secondPassVal;
    private ASTType astType;

    AST(ASTType type) {
        symbols = new ArrayList<>();
        statements = new ArrayList<>();
        value = "";
        secondPassVal = null;
        astType = type;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Map.Entry<String, int[]>> getSymbols() {
        return symbols;
    }

    public void addSymbol(String symbol, int[] value) {
        symbols.add(new AbstractMap.SimpleEntry<>(symbol, value));
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

    public int[] getSecondPassVal() {
        return secondPassVal;
    }

    public void setSecondPassVal(int[] secondPassVal) {
        this.secondPassVal = secondPassVal;
    }

    private void print(int tabs) {
        switch (this.getAstType()) {
            case Int:
            case Char:
            case Float:
            case String:
            case RefUse:
            case VarUse:
//            case Var:
            case Symbol:
                System.out.println(String.join("", Collections.nCopies(tabs, "  ")) + this.getAstType().name() + " = " + value);
                break;
            case LoadOrCall:
            case ObjectCons:
            case ObjectRead:
            case ObjectWrite:
            case BuiltInCall:
            case ObjectConsRef:
            case ObjectReadRef:
            case ObjectWriteRef:
            case Load:
            case Var:
                System.out.println(String.join("", Collections.nCopies(tabs, "  ")) + this.getAstType().name() + " = " + Arrays.toString(secondPassVal));
                break;
            case Program:
            case Function:
            case CoRoutine:
            case Lambda:
                System.out.println(String.join("", Collections.nCopies(tabs, "  ")) + this.getAstType().name());
                System.out.print(String.join("", Collections.nCopies(tabs + 1, "  ")) + "Symbols: ");
                for (var s : symbols) {
                    System.out.print(s.getKey() + "(" + Arrays.toString(s.getValue()) + ") ");
                }
                System.out.println();
                System.out.println(String.join("", Collections.nCopies(tabs + 1, "  ")) + "Statements: ");
                for (var s : statements) {
                    s.print(tabs + 2);
                }
                break;
        }
    }
}
