package jSol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static jSol.Term.*;

public class AbstractSyntaxTree {
    private static Set<Term> hasChildren = Set.of(Program, TopDef, FunDef, CoFunDef, TypeDef,
            Fields, Body, Statement, Def, VarDef, SimpleStatement, Lambda, FunLambda, CoFunLambda);

    private static Set<Term> isNewScope = Set.of(Program, FunDef, CoFunDef, Lambda);

    private AST root;
    private Set<String> strings;
    private Set<String> types;

    AbstractSyntaxTree() {
        strings = new HashSet<>();
        types = new HashSet<>();
    }

    public AST getRoot() {
        return root;
    }

    public void setRoot(AST root) {
        this.root = root;
    }

    public Set<java.lang.String> getStrings() {
        return strings;
    }

    public void setStrings(Set<java.lang.String> strings) {
        this.strings = strings;
    }

    public Set<java.lang.String> getTypes() {
        return types;
    }

    public void setTypes(Set<java.lang.String> types) {
        this.types = types;
    }

    public static AbstractSyntaxTree fromParseTree(ParseTree tree) {
        var ast = new AbstractSyntaxTree();
        // CALL toAST HERE
        // CREATE STRINGS & TYPES TABLE HERE

        ast.setRoot(toAST(tree).getStatements().get(0));
        return ast;
    }

    //TODO - deal with typeDEF
    private static AST toAST(ParseTree tree) {
        var type = tree.getType();
        if (type.equals(FunDef)) {
            int a = 0;
        }
        if (isNewScope.contains(type)) {

            var unknownParent = new AST(ASTType.UNKNOWN);
            var typeAST = new AST(ASTType.Function);

            ArrayList<ParseTree> treeChildren = new ArrayList<>(Arrays.asList(tree.getChildren()));

            if (type == FunDef || type == CoFunDef) {
                treeChildren.remove(1);
                treeChildren.remove(0);
            }

            for (var child : treeChildren) {
                var childAST = toAST(child);

                for (var node : childAST.getStatements()) {
                    typeAST.addStatement(node);
                }
            }
            unknownParent.addStatement(typeAST);
            return unknownParent;

        } else if (hasChildren.contains(type)) {
            // extract children results, create new AST.UNKNOWN, pass up chain
            var parentAST = new AST(ASTType.UNKNOWN);

            if (type.equals(VarDef)) {
                var varDef = new AST(ASTType.VarUse);
                varDef.setValue(tree.getChildren()[1].getContent());
                parentAST.addStatement(varDef);
            } else {
                for (var child : tree.getChildren()) {
                    var childAST = toAST(child);

                    for (var node : childAST.getStatements()) {
                        parentAST.addStatement(node);
                    }
                }

            }

            return parentAST;
        } else {
            if (type.equals(Epsilon)) {
                return new AST(ASTType.UNKNOWN);
            } else {
                var parent = new AST(ASTType.UNKNOWN);
                switch (type) {
                    case Int:
                        var intAst = new AST(ASTType.Int);
                        intAst.setValue(tree.getContent());
                        parent.addStatement(intAst);
                        return parent;
                    case Float:
                        var floatAST = new AST(ASTType.Float);
                        floatAST.setValue(tree.getContent());
                        parent.addStatement(floatAST);
                        return parent;
                    case String:
                        var stringAST = new AST(ASTType.String);
                        stringAST.setValue(tree.getContent());
                        parent.addStatement(stringAST);
                        return parent;
                    case Char:
                        var charAST = new AST(ASTType.Char);
                        charAST.setValue(tree.getContent());
                        parent.addStatement(charAST);
                        return parent;
                    case KwReference:
                        var ref = new AST(ASTType.RefUse);
                        ref.setValue(tree.getContent());
                        parent.addStatement(ref);
                        return parent;
                    case KwFun:
                    case KwLambdaBegin:
                    case KwCFLambdaBegin:
                    case KwLambdaEnd:
                    case KwCFLambdaEnd:
                    case KwEnd:
                        return new AST(ASTType.UNKNOWN);
                    case KwSymbol:
                        // Do something with symbol table eventually...
                        var symbol = new AST(ASTType.Symbol);
                        symbol.setValue(tree.getContent());
                        parent.addStatement(symbol);
                        return parent;
                    case KwId:
                        var id = new AST(ASTType.VarUse);
                        id.setValue(tree.getContent());
                        parent.addStatement(id);
                        return parent;
                    default:
                        System.out.printf("tree: \ntype:%s\ncontent:%s\n",tree.getType(), tree.getContent());
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
        System.out.println("Types: ");
        for (var type : types) {
            System.out.print(type);
        }
        System.out.println();
        System.out.println("AST: ");
        root.print();
        return this;
    }
}
