package jSol;

import java.util.Set;

import static jSol.Term.*;

public class AbstractSyntaxTree {
    private static Set<Term> hasChildren = Set.of(Program, TopDef, FunDef, CoFunDef, TypeDef,
            Fields, Body, Statement, Def, VarDef, SimpleStatement, Lambda, FunLambda, CoFunLambda);

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
        return new AbstractSyntaxTree();
    }

    private static AbstractSyntaxTree toAST(ParseTree tree) {
        for (var node : tree.getChildren()) {
            if (hasChildren.contains(node.getType())) {
                var result = toAST(node);
                // collect results to AST Obj
                // If node is Function / Program??
                // Switch(type)??
                return new AbstractSyntaxTree();
            } else {
                // add self
            }
        }
    }

    public void print() {
        System.out.println("Strings: ");
        for (var s : strings) {
            System.out.println("  " + s);
        }
        System.out.println("Types: ");
        for (var type: types) {
            System.out.print(type);
        }
        System.out.println();
        System.out.println("AST: ");
        root.print();
    }
}
