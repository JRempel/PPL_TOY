package jSol;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jSol.Term.Epsilon;
import static jSol.Term.Program;

public class Parser {

    private static String INVALID_TOKEN_MESSAGE = "\n\nCannot find expression to match '%s' on line %d in position %d.";

    public static void main(String[] args) {
        ParseNode tree = Scanner
                .scan("./program.txt")
                .toParseTree();
    }

    public static ParseNode fromTokenSTream(Stream<Token> tokens) {
        ParseNode start = new ParseNode(Program, null, -1, -1);
        Stack<ParseNode> stack = new Stack<>();
        stack.push(start);
        tokens.forEach(curr -> {
            while (!stack.empty() && !stack.peek().getType().isEqualTo(curr) && !(stack.peek().getType() == Epsilon)) {
                ParseNode top = stack.pop();
                Term[] productionForTop = Predict.getExpResult(top.getType(), curr);

                if (productionForTop == null) {
                    System.out.println(String.format(INVALID_TOKEN_MESSAGE, curr.content(), curr.line(), curr.position()));
                    System.exit(1);
                }

                top.setChildren(Arrays.stream(productionForTop)
                        .map(t -> new ParseNode(t, null, -1, -1))
                        .toArray(ParseNode[]::new));

                for (int i = top.getChildren().length - 1; i >= 0; i--) {
                    stack.push(top.getChildren()[i]);
                }

                while (!stack.empty() && stack.peek().getType() == Epsilon) {
                    stack.pop();
                }
            }

            if (!stack.empty()) {
                if (stack.peek().getType().isEqualTo(curr)) {
                    ParseNode p = stack.pop();
                    p.setContent(curr.content());
                    p.setLine(curr.line());
                    p.setPosition(curr.position());
                } else {
                    System.out.println(String.format(INVALID_TOKEN_MESSAGE, curr.content(), curr.line(), curr.position()));
                    System.exit(1);
                }
            }
        });
        return start;
    }
}