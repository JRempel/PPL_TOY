package jSol;

import java.util.Set;

import static jSol.Term.*;

public class AbstractSyntaxTree {
    private static Set<Term> hasChildren = Set.of(Program, TopDef, FunDef, CoFunDef, TypeDef,
            Fields, Body, Statement, Def, VarDef, SimpleStatement, Lambda, FunLambda, CoFunLambda);

    private static Set<Term> isNewScope = Set.of(Program, FunDef, CoFunDef, Lambda);

    private AST root;
    private Set<String> strings;
    private Set<String> types;

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

        ast.setRoot(toAST(tree));
        return ast;
    }

    //TODO - deal with typeDEF
    private static AST toAST(ParseTree tree) {
        var type = tree.getType();
        if (isNewScope.contains(type)) {

            AST unkownParent = AST.UNKNOWN;
            AST typeAST = AST.Function;

            for (var child : tree.getChildren()) {

                AST childAST = toAST(child);

                for (AST node : childAST.getStatements()) {
                    typeAST.addStatement(node);
                }
            }
            unkownParent.addStatement(typeAST);
            return unkownParent;

        } else if (hasChildren.contains(type)) {
            // extract children results, create new AST.UNKNOWN, pass up chain

            AST parentAST = AST.UNKNOWN;

            if (type.equals(VarDef)) {

                AST varDef = AST.VarUse;
                varDef.setValue(tree.getChildren()[1].getContent());
                parentAST.addStatement(varDef);
            } else {
                for (var child : tree.getChildren()) {

                    AST childAST = toAST(child);

                    for (AST node : childAST.getStatements()) {
                        parentAST.addStatement(node);
                    }
                }

            }

            return parentAST;
        } else {
            if (type.equals(Epsilon)) {
                return AST.UNKNOWN;
            } else {
                var parent = AST.UNKNOWN;
                switch (type) {
                    case Int:
                        AST intAst = AST.Int;
                        intAst.setValue(tree.getContent());
                        parent.addStatement(intAst);
                        return parent;
                    case Float:
                        AST floatAST = AST.Float;
                        floatAST.setValue(tree.getContent());
                        parent.addStatement(floatAST);
                        return parent;
                    case String:
                        AST stringAST = AST.String;
                        stringAST.setValue(tree.getContent());
                        parent.addStatement(stringAST);
                        return parent;
                    case Char:
                        AST charAST = AST.Char;
                        charAST.setValue(tree.getContent());
                        parent.addStatement(charAST);
                        return parent;
                    case KwReference:
                        AST refAST = AST.RefUse;
                        refAST.setValue(tree.getContent());
                        parent.addStatement(refAST);
                        return parent;
                    case KwFun:
                        return AST.UNKNOWN;
                    case KwId:
                        AST astId = AST.VarUse;
                        astId.setValue(tree.getContent());
                        parent.addStatement(astId);
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
