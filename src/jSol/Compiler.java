package jSol;

public class Compiler {
    public static void main(String[] args) {
        compile("./fib.txt");
    }

    private static void compile(String path) {
        // TODO: Only print errors, remove tree/token printer
        // Lexer
        InputStream.from(path)
                .unComment()
                .splitterate()
                .tokenize()
                //.reportInvalid()
                //Parser
                .toParseTree()
//                .print()
                //To AST
                .toAST()
                .secondPass()
                .print();
        // To AST
    }
}
