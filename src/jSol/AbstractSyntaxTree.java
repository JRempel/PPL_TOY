package jSol;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static jSol.ASTType.Function;
import static jSol.ASTType.LoadOrCall;
import static jSol.ASTType.Var;
import static jSol.ASTType.VarUse;
import static jSol.Term.*;

public class AbstractSyntaxTree {
    private static Set<Term> hasChildren = Set.of(Program, TopDef, FunDef, CoFunDef, TypeDef,
            Fields, Body, Statement, Def, VarDef, SimpleStatement, Lambda, FunLambda, CoFunLambda);

    private static Set<Term> isNewScope = Set.of(Program, FunDef, CoFunDef, Lambda);

    private AST root;
    private ArrayList<String> strings;
    private ArrayList<Map.Entry<String, List<String>>> types;


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

    public ArrayList<Map.Entry<String, List<String>>> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Map.Entry<java.lang.String, List<java.lang.String>>> types) {
        this.types = types;
    }

    public static AbstractSyntaxTree fromParseTree(ParseTree tree) {
        var ast = new AbstractSyntaxTree();
        AST root = toAST(tree, ast.getStrings(), ast.getTypes());
        root.setAstType(ASTType.Program);
        ast.setRoot(root);
        return ast;
    }

    private static AST toAST(ParseTree tree, List<String> stringTable, List<Map.Entry<String, List<String>>> typeTable) {
        var type = tree.getType();
        if (isNewScope.contains(type)) {

            var unknownParent = new AST(ASTType.UNKNOWN);
            // Label lambda's different for function hoisting
            var typeAST = new AST(type == Lambda ? ASTType.Lambda : Function);

            ArrayList<ParseTree> treeChildren = new ArrayList<>(Arrays.asList(tree.getChildren()));

            if (type == FunDef || type == CoFunDef) {
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

                // Handle symbols
                int numTypeParam = 0;
                Map.Entry<String, int[]> tempType = null;
                boolean typeAdded = false;
                var tempSymbolListAfterTempType = new ArrayList<Map.Entry<String, int[]>>();
                for (var symbol : childAST.getSymbols()) {

                    // Update index in local symbol table before adding
                    int[] meta = symbol.getValue();

                    // Function symbols
                    if (meta[0] == 0) {
                        meta[1] = fnSymbolIndex;
                        fnSymbolIndex++;
                        if (!typeAdded && tempType != null) {
                            tempSymbolListAfterTempType.add(new AbstractMap.SimpleEntry<>(symbol.getKey(), meta));
                        } else {
                            typeAST.addSymbol(symbol.getKey(), meta);
                        }
                    }

                    // Types
                    if (meta[0] == 1) {
                        meta[1] = typeSymbolIndex;
                        if (tempType != null && !typeAdded) {
                            int[] paramUpdate = tempType.getValue();
                            paramUpdate[2] = numTypeParam;
                            tempType.setValue(paramUpdate);
                            typeAST.addSymbol(tempType.getKey(), tempType.getValue());
                            int typeFields = 0;
                            var paramList = new ArrayList<String>();
                            for (var tempSymbol : tempSymbolListAfterTempType) {
                                var tempValue = tempSymbol.getValue();
                                if (tempValue[0] == 2 || tempValue[0] == 3) {
                                    for (var entry : typeTable) {
                                        if (entry.getKey().equals(tempType.getKey())) {
                                            tempValue[1] = typeTable.indexOf(entry);
                                            break;
                                        }
                                    }
                                    if (tempValue[0] == 2) {
                                        paramList.add(tempSymbol.getKey());
                                    }
                                    tempValue[2] = typeFields;
                                    tempValue[3] = stringTable.indexOf(tempSymbol.getKey());
                                }
                                typeFields += (tempValue[0] == 2) ? 1 : 0;
                                typeAST.addSymbol(tempSymbol.getKey(), tempValue);
                            }
                            for (int i = 0; i < typeTable.size(); i++) {
                                if (typeTable.get(i).getKey().equals(tempType.getKey())) {
                                    var typeEntry = typeTable.get(i);
                                    typeEntry.setValue(paramList);
                                }
                            }
                        }
                        tempType = new AbstractMap.SimpleEntry<>(symbol.getKey(), symbol.getValue());
                        typeAdded = false;
                        numTypeParam = 0;
                        typeSymbolIndex++;
                    }

                    // Type param read
                    if (meta[0] == 2) {
                        numTypeParam++;
                        if (!typeAdded && tempType != null) {
                            tempSymbolListAfterTempType.add(new AbstractMap.SimpleEntry<>(symbol.getKey(), meta));
                        } else {
                            // ERROR?
                            typeAST.addSymbol(symbol.getKey(), meta);
                        }
                    }

                    // Type param write
                    if (meta[0] == 3) {
                        if (!typeAdded && tempType != null) {
                            tempSymbolListAfterTempType.add(new AbstractMap.SimpleEntry<>(symbol.getKey(), meta));
                        } else {
                            // ERROR?
                            typeAST.addSymbol(symbol.getKey(), meta);
                        }
                    }
                }
                if (tempType != null && !typeAdded) {
                    int[] paramUpdate = tempType.getValue();
                    paramUpdate[2] = numTypeParam;
                    tempType.setValue(paramUpdate);
                    typeAST.addSymbol(tempType.getKey(), tempType.getValue());
                    int typeFields = 0;
                    var paramList = new ArrayList<String>();
                    for (var tempSymbol : tempSymbolListAfterTempType) {
                        var tempValue = tempSymbol.getValue();
                        if (tempValue[0] == 2 || tempValue[0] == 3) {
                            for (var entry : typeTable) {
                                if (entry.getKey().equals(tempType.getKey())) {
                                    tempValue[1] = typeTable.indexOf(entry);
                                    break;
                                }
                            }
                            if (tempValue[0] == 2) {
                                paramList.add(tempSymbol.getKey());
                            }
                            tempValue[2] = typeFields;
                            tempValue[3] = stringTable.indexOf(tempSymbol.getKey());
                        }
                        typeFields += (tempValue[0] == 2) ? 1 : 0;
                        typeAST.addSymbol(tempSymbol.getKey(), tempValue);
                    }
                    for (int i = 0; i < typeTable.size(); i++) {
                        if (typeTable.get(i).getKey().equals(tempType.getKey())) {
                            var typeEntry = typeTable.get(i);
                            typeEntry.setValue(paramList);
                        }
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
                // {variable, index of fn in symbol table (define later), index in string-table}
                parentAST.addSymbol(value, new int[]{0, 0, stringTable.indexOf(value)});
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
                        // TODO: Come back to this depending on secondPass result(s)
//                        if (BuiltInFunctions.isBuildInFunction(value)) {
//                            id = new AST(ASTType.BuiltInCall);
//                            value = "" + BuiltInFunctions.getBuiltInFunctionValue(value);
//                        } else {
                        id = new AST(ASTType.VarUse);
//                        }
                        id.setValue(value);
                        parent.addStatement(id);
                        return parent;
                    }
                    case KwTypeId: {
                        AST unknownParent = new AST(ASTType.UNKNOWN);
                        value = tree.getContent();
                        if (!typeTable.contains(value)) {
                            var params = new AbstractMap.SimpleEntry<String, List<String>>(value, new ArrayList<>());
                            typeTable.add(params);
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
                            stringTable.add(value + "=");
                        }
                        // {field-read, type id (update later), position in type (update later), index in string-table}
                        unknownParent.addSymbol(value, new int[]{2, 0, 0, 0});

                        // {field-write, type id (update later), position in type (update later), index in string-table}
                        unknownParent.addSymbol(value + "=", new int[]{3, 0, 0, 0});

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

    public AbstractSyntaxTree secondPass() {
        secondPass(this.root, new ArrayList<>(), this.getTypes(), new ArrayList<>());
        return this;
    }

    private static void secondPass(AST node, List<List<Map.Entry<String, int[]>>> symbolScopes, List<Map.Entry<String, List<String>>> types, List<String> varsInScope) {
        // Add current-node symbol scope
        var currScope = new ArrayList<>(node.getSymbols());
        if (node.getAstType() == ASTType.Function || node.getAstType() == ASTType.Lambda || node.getAstType() == ASTType.Program) {
            // ensure scopes aren't passed between children.
            symbolScopes = new ArrayList<>(symbolScopes);
            varsInScope = new ArrayList<>(varsInScope);
            // so we don't have to reverse-iterate through scopes
            symbolScopes.add(0, currScope);
        }

        type:
        {
            switch (node.getAstType()) {
                // Node type never changes, or is non-terminal
                case Program:
                case Function:
                case Lambda:
                case Symbol:
                case Int:
                case Char:
                case Float:
                case String: {
                    break type;
                }
                case VarUse: {
                    for (int i = 0; i < symbolScopes.size(); i++) {
                        for (var symbol : symbolScopes.get(i)) {
                            if (symbol.getKey().equals(node.getValue())) {
                                if (symbol.getValue()[0] == 0) {
                                    // Function
                                    int[] secondPassVal = Arrays.copyOf(symbol.getValue(), symbol.getValue().length);
                                    secondPassVal[0] = i;
                                    node.setSecondPassVal(secondPassVal);
                                    node.setAstType(ASTType.LoadOrCall);
                                    break type;
                                } else if (symbol.getValue()[0] == 1) {
                                    // Type constructor
                                    int index = -1;
                                    int params = -1;
                                    for (var type : types) {
                                        if (type.getKey().equals(node.getValue())) {
                                            index = types.indexOf(type);
                                            params = type.getValue().size();
                                        }
                                    }
                                    node.setSecondPassVal(new int[]{index, params});
                                    node.setAstType(ASTType.ObjectCons);
                                    break type;
                                } else if (symbol.getValue()[0] == 2) {
                                    // Type param read
                                    node.setSecondPassVal(Arrays.copyOfRange(symbol.getValue(), 1, 4));
                                    node.setAstType(ASTType.ObjectRead);
                                    break type;
                                } else if (symbol.getValue()[0] == 3) {
                                    // Type param write
                                    node.setSecondPassVal(Arrays.copyOfRange(symbol.getValue(), 1, 4));
                                    node.setAstType(ASTType.ObjectWrite);
                                    break type;
                                }
                            }
                        }
                    }
                    if (BuiltInFunctions.isBuildInFunction(node.getValue())) {
                        node.setSecondPassVal(new int[]{BuiltInFunctions.getBuiltInFunctionValue(node.getValue())});
                        node.setAstType(ASTType.BuiltInCall);
                        break type;
                    }
                }
                case RefUse: {
                    for (int i = 0; i < symbolScopes.size(); i++) {
                        for (var symbol : symbolScopes.get(i)) {
                            if (symbol.getKey().equals(node.getValue())) {
                                if (symbol.getValue()[0] == 0) {
                                    // Function
                                    int[] secondPassVal = Arrays.copyOf(symbol.getValue(), symbol.getValue().length);
                                    secondPassVal[0] = i;
                                    node.setSecondPassVal(secondPassVal);
                                    node.setAstType(ASTType.Load);
                                    break type;
                                } else if (symbol.getValue()[0] == 1) {
                                    // Type constructor
                                    int index = -1;
                                    int params = -1;
                                    for (var type : types) {
                                        if (type.getKey().equals(node.getValue())) {
                                            index = types.indexOf(type);
                                            params = type.getValue().size();
                                        }
                                    }
                                    node.setSecondPassVal(new int[]{index, params});
                                    node.setAstType(ASTType.ObjectConsRef);
                                    break type;
                                } else if (symbol.getValue()[0] == 2) {
                                    // Type param read
                                    node.setSecondPassVal(Arrays.copyOfRange(symbol.getValue(), 1, 4));
                                    node.setAstType(ASTType.ObjectReadRef);
                                    break type;
                                } else if (symbol.getValue()[0] == 3) {
                                    // Type param write
                                    node.setSecondPassVal(Arrays.copyOfRange(symbol.getValue(), 1, 4));
                                    node.setAstType(ASTType.ObjectWriteRef);
                                    break type;
                                }
                            }
                        }
                    }
                    if (BuiltInFunctions.isBuildInFunction(node.getValue())) {
                        node.setSecondPassVal(new int[]{BuiltInFunctions.getBuiltInFunctionValue(node.getValue())});
                        node.setAstType(ASTType.BuiltInCall);
                        break type;
                    }
                }
                case Var: {
                    if (varsInScope.contains(node.getValue())) {
                        throw new RuntimeException("Variable defined twice in scope: " + node.getAstType() + " " + node.getValue());
                    } else {
                        for (int i = 0; i < symbolScopes.size(); i++) {
                            for (var symbol : symbolScopes.get(i)) {
                                if (symbol.getKey().equals(node.getValue())) {
                                    node.setSecondPassVal(new int[]{symbol.getValue()[1]});
                                }
                            }
                        }
                    }
                    break type;
                }
                default:
                    throw new RuntimeException("Compiler error - invalid use in AST: " + node.getAstType() + " " + node.getValue());
            }
        }

        for (var child : node.getStatements()) {
            secondPass(child, symbolScopes, types, varsInScope);
            if (child.getAstType() == Var) {
                varsInScope.add(child.getValue());
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

    //Print types from types data structure
    public AbstractSyntaxTree generatorPrint() {
        System.out.println("Strings: ");
        for (var s : strings) {
            System.out.println("  " + s);
        }
        System.out.println("Types: ");
        for (var type : types) {
            System.out.print("\t" + type.getKey());
        }
        System.out.println();
        System.out.println("Abstract Syntax Tree: ");
        root.generatorPrint();
        return this;
    }

    public void addString(String s) {
        getStrings().add(s);
    }

    public void addType(String t){
        getStrings().add(t);
    }

    public void generatorAddType(String t, ArrayList<String> typeMembers) {
        Map.Entry<String,List<String>> typeMap = new AbstractMap.SimpleEntry<String,List<String>>(t,typeMembers);
        getTypes().add(typeMap);
    }
}
