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
        Stack<Term> stack = new Stack<>();
        tokens.forEach(x -> {
            while()
        });
        return start;
    }
}
