package jSol;

import java.util.Stack;
import java.util.stream.Stream;

import static jSol.Term.Program;

public class Parser {
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
            while(!stack.peek().getType().isEqualTo(curr)) {
                ParseNode top = stack.pop();
                Term[] productionForTop = 
            }
            // do thing when stack(0) == input(0)
        });
        return start;
    }
}
