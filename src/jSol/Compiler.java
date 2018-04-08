package jSol;

public class Compiler {
    private static String toyType = ".toy";
    private static String byteCodeType = ".byte";

    public static void main(String[] args) {
        // TODO : validate args
        compile("fib.toy");
    }

    private static void compile(String path) {
        String outputPath = path;
        if (path.endsWith(toyType)) {
            var strb = new StringBuilder(path);
            int index = strb.lastIndexOf(toyType);
            strb.replace(index, toyType.length() + index, byteCodeType);
            outputPath = strb.toString();
        } else {
            System.out.println("Please provide a '.toy' file.");
            System.exit(1);
        }
        InputStream.from(path)
                .unComment()
                .splitterate()
                .tokenize()
                .reportInvalid()
                .toParseTree()
                .toAST()
                .secondPass()
                .toByteCode(outputPath);
        System.out.println("Byte-code file written to: " + outputPath);
    }
}
