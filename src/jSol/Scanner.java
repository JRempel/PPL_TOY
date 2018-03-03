package jSol;

import java.util.stream.Collectors;

public class Scanner {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.exit(1);
//        }
//
//        InputStream.from(args[0])
//                .unComment()
//                .splitterate()
//                .tokenize()
//                .reportInvalid();

        InputStream.from("./program.txt")
                .unComment()
                .splitterate()
                .tokenize()
                .reportInvalid()
                .toIntStream()
                // vv only necessary until rest of interpreter is built.
                .boxed()
                .collect(Collectors.toList());
    }
}
