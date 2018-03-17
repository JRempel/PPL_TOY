package jSol;

public class Scanner {
    public static TokenStream scan(String inputFile) {
        return InputStream.from(inputFile)
                .unComment()
                .splitterate()
                .tokenize()
                .reportInvalid();
    }
}
