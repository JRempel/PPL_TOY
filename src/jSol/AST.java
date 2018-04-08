package jSol;

import java.util.*;

public class AST {

    private List<Map.Entry<String, int[]>> symbols;
    private List<AST> statements;
    private String value;
    private int[] secondPassVal;
    private ASTType astType;
    private AST parent;
    private UUID id = UUID.randomUUID();

    AST(ASTType type) {
        astType = type;
        symbols = new ArrayList<>();
        statements = new ArrayList<>();
        value = "";
        secondPassVal = null;

    }

    public AST getParent() {
        return parent;
    }

    private void setParent(AST parent) {
        this.parent = parent;
    }

    public String getValue() {
        return value;
    }

//    public Integer [] tryValueAsIntegerArray(){
//        String [] values = getValue().split(",");
//        return Arrays.asList(values).stream()
//                .map(s -> Integer.parseInt(s))
//                .toArray(Integer []::new);
//
//    }

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
        statement.setParent(this);
        statements.add(statement);
    }

    public void print() {
        print(0);
    }

    public UUID getId() {
        return id;
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

    public void generatorPrint(){
        generatorPrint(0);
    }

    //Prints AST node value instead of secondPassVal to allow inspection for manually created AbstractSyntaxTrees.
    private void generatorPrint(int tabs) {
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
                System.out.println(String.join("", Collections.nCopies(tabs, "  ")) + this.getAstType().name() + " = " + value);
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
                    s.generatorPrint(tabs + 2);
                }
                break;
        }
    }


}
