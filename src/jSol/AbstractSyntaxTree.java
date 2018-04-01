package jSol;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jSol.ASTType.Function;
import static jSol.ASTType.Var;
import static jSol.Term.*;

public class AbstractSyntaxTree {
    private static Set<Term> hasChildren = Set.of(Program, TopDef, FunDef, CoFunDef, TypeDef,
            Fields, Body, Statement, Def, VarDef, SimpleStatement, Lambda, FunLambda, CoFunLambda);

    private static Set<Term> isNewScope = Set.of(Program, FunDef, CoFunDef, Lambda);

    private AST root;
    private ArrayList<String> strings;
    private ArrayList<String> types;

    AbstractSyntaxTree() {
        strings = new ArrayList<>();
        types = new ArrayList<>();
    }

    public AST getRoot() {
        return root;
    }

    public void setRoot(AST root) {
        this.root = root;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public static AbstractSyntaxTree fromParseTree(ParseTree tree) {
        var ast = new AbstractSyntaxTree();
        // CREATE STRINGS & TYPES TABLE HERE
        AST root = toAST(tree, ast.getStrings(), ast.getTypes());
        root.setAstType(ASTType.Program);
        ast.setRoot(root);
        return ast;
    }

    //TODO - deal with typeDEF
    private static AST toAST(ParseTree tree, ArrayList<String> stringTable, ArrayList<String> typeTable) {
        var type = tree.getType();
        if (isNewScope.contains(type)) {

            var unknownParent = new AST(ASTType.UNKNOWN);
            // Label lambda's different for function hoisting
            var typeAST = new AST(type == Lambda ? ASTType.Lambda : Function);

            ArrayList<ParseTree> treeChildren = new ArrayList<>(Arrays.asList(tree.getChildren()));

            if (type == FunDef || type == CoFunDef) {
                // TODO: Probably need to do something to generate symbol-table here?
                treeChildren.remove(1);
                treeChildren.remove(0);
            }

            // Collect subtree statements
            int fnSymbolIndex = 0;
            int typeSymbolIndex = 0;

            for (var child : treeChildren) {
                var childAST = toAST(child, stringTable, typeTable);

                for (var node : childAST.getStatements()) {
                    typeAST.addStatement(node);
                }

                int numTypeParam = 0;
                Map.Entry<String, int[]> tempType = null;
                boolean typeAdded = false;
                var tempSymbolListAfterTempType = new ArrayList<Map.Entry<String, int[]>>();
                for (var symbol : childAST.getSymbols()) {
                    // Update index in local symbol table before adding
                    int[] meta = symbol.getValue();
                    if (meta[0] == 0) {
                        meta[1] = fnSymbolIndex;
                        fnSymbolIndex++;
                        if (!typeAdded && tempType != null) {
                            tempSymbolListAfterTempType.add(new AbstractMap.SimpleEntry<>(symbol.getKey(), meta));
                        } else {
                            typeAST.addSymbol(symbol.getKey(), meta);
                        }
                    }
                    if (meta[0] == 1) {
                        meta[1] = typeSymbolIndex;
                        if (tempType != null) {
                            int[] paramUpdate = tempType.getValue();
                            paramUpdate[2] = numTypeParam;
                            tempType.setValue(paramUpdate);
                            typeAST.addSymbol(tempType.getKey(), tempType.getValue());
                            for (var tempSymbol : tempSymbolListAfterTempType) {
                                typeAST.addSymbol(tempSymbol.getKey(), tempSymbol.getValue());
                            }
                            typeAdded = true;
                        }
                        tempType = new AbstractMap.SimpleEntry<>(symbol.getKey(), symbol.getValue());
                        typeAdded = false;
                        numTypeParam = 0;
                        typeSymbolIndex++;
                    }
                    if (meta[0] == -1) {
                        numTypeParam++;
                    }
                }
                if (tempType != null && !typeAdded) {
                    int[] paramUpdate = tempType.getValue();
                    paramUpdate[2] = numTypeParam;
                    tempType.setValue(paramUpdate);
                    typeAST.addSymbol(tempType.getKey(), tempType.getValue());
                    for (var tempSymbol : tempSymbolListAfterTempType) {
                        typeAST.addSymbol(tempSymbol.getKey(), tempSymbol.getValue());
                    }
                }
            }

            // Collapse empty trees
            if (typeAST.getStatements().isEmpty()) {
                return new AST(ASTType.UNKNOWN);
            }

            unknownParent.addStatement(typeAST);

            // Add Var with function name after declaration
            if (type == FunDef || type == CoFunDef) {
                var nameAst = new AST(Var);
                String value = tree.getChildren()[1].getContent();
                if (!stringTable.contains(value)) {
                    stringTable.add(value);
                }
                nameAst.setValue(value);
                unknownParent.addStatement(nameAst);

                // { Type function, index of fn in symbol table (define later), index in str table }
                unknownParent.addSymbol(value, new int[]{0, 0, stringTable.indexOf(value)});
            }

            // Hoist un-named functions... within a function; intentionally ignore lambdas
            if (typeAST.getAstType() == Function && unknownParent.getStatements().get(unknownParent.getStatements().size() - 1).getAstType() != Var) {
                for (var subStatement : typeAST.getStatements()) {
                    unknownParent.addStatement(subStatement);
                }
                for (var entry : typeAST.getSymbols()) {
                    unknownParent.addSymbol(entry.getKey(), entry.getValue());
                }
                unknownParent.getStatements().remove(typeAST);
            }

            return unknownParent;

        } else if (hasChildren.contains(type)) {
            // extract children results, create new AST.UNKNOWN, pass up chain
            var parentAST = new AST(ASTType.UNKNOWN);

            if (type == VarDef) {
                var varDefAST = new AST(Var);
                var value = tree.getChildren()[1].getContent();
                if (!stringTable.contains(value)) {
                    stringTable.add(value);
                }
                varDefAST.setValue(value);
                parentAST.addStatement(varDefAST);
                return parentAST;
            }

            for (var child : tree.getChildren()) {
                var childAST = toAST(child, stringTable, typeTable);

                for (var node : childAST.getStatements()) {
                    parentAST.addStatement(node);
                }

                for (var symbol : childAST.getSymbols()) {
                    parentAST.addSymbol(symbol.getKey(), symbol.getValue());
                }
            }

            return parentAST;
        } else {
            if (type.equals(Epsilon)) {
                return new AST(ASTType.UNKNOWN);
            } else {
                var parent = new AST(ASTType.UNKNOWN);
                String value;
                switch (type) {
                    case Int: {
                        var intAst = new AST(ASTType.Int);
                        intAst.setValue(tree.getContent());
                        parent.addStatement(intAst);
                        return parent;
                    }
                    case Float: {
                        var floatAST = new AST(ASTType.Float);
                        floatAST.setValue(tree.getContent());
                        parent.addStatement(floatAST);
                        return parent;
                    }
                    case String: {
                        var stringAST = new AST(ASTType.String);
                        // Remove the '"' at start and end of content
                        value = tree.getContent().substring(1, tree.getContent().length() - 1);
                        if (!stringTable.contains(value)) {
                            stringTable.add(value);
                        }
                        stringAST.setValue("" + stringTable.indexOf(value));
                        parent.addStatement(stringAST);
                        return parent;
                    }
                    case Char: {
                        var charAST = new AST(ASTType.Char);
                        // remove the ''' at start and end of content
                        charAST.setValue(tree.getContent().substring(1, tree.getContent().length() - 1));
                        parent.addStatement(charAST);
                        return parent;
                    }
                    case KwReference: {
                        var ref = new AST(ASTType.RefUse);
                        // Remove the '%' char in front of reference
                        ref.setValue(tree.getContent().substring(1));
                        parent.addStatement(ref);
                        return parent;
                    }
                    case KwFun:
                    case KwLambdaBegin:
                    case KwCFLambdaBegin:
                    case KwLambdaEnd:
                    case KwCFLambdaEnd:
                    case KwType:
                    case KwEnd:
                        return new AST(ASTType.UNKNOWN);
                    case KwSymbol: {
                        // Do something with symbol table eventually...
                        var symbol = new AST(ASTType.Symbol);
                        // Remove the ':' at the beginning of symbol content
                        value = tree.getContent().substring(1);
                        if (!stringTable.contains(value)) {
                            stringTable.add(value);
                        }
                        symbol.setValue("" + stringTable.indexOf(value));
                        parent.addStatement(symbol);
                        return parent;
                    }
                    case KwId: {
                        value = tree.getContent();
                        AST id;
                        if (!typeTable.contains(value) && !stringTable.contains(value) && !BuiltInFunctions.isBuildInFunction(value)) {
                            stringTable.add(value);
                        }
                        if (BuiltInFunctions.isBuildInFunction(value)) {
                            id = new AST(ASTType.BuiltInCall);
                            value = "" + BuiltInFunctions.getBuiltInFunctionValue(value);
                        } else {
                            id = new AST(ASTType.VarUse);
                        }
                        id.setValue(value);
                        parent.addStatement(id);
                        return parent;
                    }
                    case KwTypeId: {
                        AST unknownParent = new AST(ASTType.UNKNOWN);
                        value = tree.getContent();
                        if (!typeTable.contains(value)) {
                            typeTable.add(value);
                        }
                        // {type of symbol, index of order of type in symbol table (update later), # params (update later)}
                        unknownParent.addSymbol(value, new int[]{1, 0, 0});
                        return unknownParent;
                    }
                    case KwTypeField: {
                        AST unknownParent = new AST(ASTType.UNKNOWN);
                        value = tree.getContent();
                        if (!stringTable.contains(value)) {
                            stringTable.add(value);
                        }
                        unknownParent.addSymbol(value, new int[]{-1});
                        return unknownParent;
                    }
                    case KwVar: {
                        var var = new AST(Var);
                        var.setValue(tree.getContent());
                        parent.addStatement(var);
                        return parent;
                    }
                    default:
                        System.out.printf("tree: \ntype:%s\ncontent:%s\n", tree.getType(), tree.getContent());
                        throw new RuntimeException("Could not match literal in toAST");

                }
            }
        }

    }

    public AbstractSyntaxTree print() {
        System.out.println("Strings: ");
        for (var s : strings) {
            System.out.println("  " + s);
        }
        System.out.print("Types: ");
        for (var type : types) {
            System.out.print(type);
        }
        System.out.println();
        System.out.println("Abstract Syntax Tree: ");
        root.print();
        return this;
    }

    public void addString(String s) {
        getStrings().add(s);
    }

    public void addType(String t) {
        getStrings().add(t);
    }
}
