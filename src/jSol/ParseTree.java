package jSol;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Stream;

import static jSol.Term.Epsilon;
import static jSol.Term.Program;

public class ParseTree {
    private static String INVALID_TOKEN_MESSAGE = "\n\nCannot find expression to match '%s' on line %d in position %d.";

    private Term type;
    private String content;
    private ParseTree[] children;

    public ParseTree(Term type, String content) {
        this.type = type;
        this.content = content;
        this.children = new ParseTree[0];
    }

    public Term getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ParseTree[] getChildren() {
        return children;
    }

    public void setChildren(ParseTree[] children) {
        this.children = children;
    }

    public static ParseTree fromTokenSTream(Stream<Token> tokens) {
        ParseTree start = new ParseTree(Program, null);
        Stack<ParseTree> stack = new Stack<>();
        stack.push(start);
        tokens.forEach(curr -> {
            while (!stack.empty() && !stack.peek().getType().isEqualTo(curr) && !(stack.peek().getType() == Epsilon)) {
                ParseTree top = stack.pop();
                Term[] productionForTop = Predict.getExpResult(top.getType(), curr);

                if (productionForTop == null) {
                    System.out.println(String.format(INVALID_TOKEN_MESSAGE, curr.content(), curr.line(), curr.position()));
                    System.exit(1);
                }

                top.setChildren(Arrays.stream(productionForTop)
                        .map(t -> new ParseTree(t, null))
                        .toArray(ParseTree[]::new));

                for (int i = top.getChildren().length - 1; i >= 0; i--) {
                    stack.push(top.getChildren()[i]);
                }

                while (!stack.empty() && stack.peek().getType() == Epsilon) {
                    stack.pop();
                }
            }

            if (!stack.empty()) {
                if (stack.peek().getType().isEqualTo(curr)) {
                    ParseTree p = stack.pop();
                    p.setContent(curr.content());
                } else {
                    System.out.println(String.format(INVALID_TOKEN_MESSAGE, curr.content(), curr.line(), curr.position()));
                    System.exit(1);
                }
            }
        });
        return start;
    }

    public ParseTree print() {
        System.out.println("\n");
        print(this, 0);
        return this;
    }

    private static void print(ParseTree tree, int tabs) {
        System.out.println(String.join("", Collections.nCopies(tabs, "  ")) +
                tree.getType() +
                (tree.getContent() != null ? "(" + tree.getContent() + ")" : ""));

        for (ParseTree p : tree.getChildren()) {
            if (p.getType() != Epsilon) {
                print(p, tabs + 1);
            }
        }
    }

    public AbstractSyntaxTree toAST() {
        return AbstractSyntaxTree.fromParseTree(this);
    }
}
