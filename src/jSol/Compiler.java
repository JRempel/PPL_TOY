package jSol;

public class Compiler {
    public static void main(String[] args) {
        compile("./vector.txt");
    }

    private static void compile(String path) {
        // TODO: Only print errors, remove tree/token printer
        // Lexer
        InputStream.from(path)
                .unComment()
                .splitterate()
                .tokenize()
                .reportInvalid()
                //Parser
                .toParseTree()
                .print();
                //To AST
//                .toAST()
//                .print();
                // To AST
    }
}
