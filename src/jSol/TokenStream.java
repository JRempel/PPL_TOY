package jSol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TokenStream {
    private static boolean debugMode = false;
    private static String TOKEN_FORMAT = "%s(%s) ";
    private static String INVALID_TOKEN_MESSAGE = "\n\nInvalid token '%s' on line %d in position %d.";
    private static List<String> illegalKeywords = new ArrayList<>() {{
        add("\\.");
        add("fun");
        add("cofun");
        add("type");
        add("var");
    }};

    private Stream<Token> delegate;

    private TokenStream(Stream<Token> delegate) {
        this.delegate = delegate;
    }

    /**
     * Create a TokenStream from a given InputSteam.
     *
     * @param input - the InputStream to convert.
     */
    public static TokenStream fromInputStream(InputStream input) {
        return new TokenStream(input.getDelegate()
                .map(x -> Arrays.stream(Token.values())
                        .filter(a -> a.matches(x.content()))
                        .map(a -> a.copyOf(x))
                        .map(a -> {
                            if (a == Token.ID) {
                                var matches = illegalKeywords.stream()
                                        .filter(y -> x.content().matches(y))
                                        .count();
                                if (matches > 0) {
                                    return Token.INVALID;
                                }
                            } else if (a == Token.REFERENCE) {
                                var matches = illegalKeywords.stream()
                                        .filter(y -> x.content().matches("%" + y))
                                        .count();
                                if (matches > 0 || !Token.ID.matches(x.content().substring(1))) {
                                    return Token.INVALID;
                                }
                            }
                            return a;
                        })
                        .findFirst()
                        .orElse(Token.INVALID).copyOf(x)));
    }

    /**
     * Print each token name and content, and report the first invalid token, along with its position.
     */
    public TokenStream reportInvalid() {
        delegate = delegate.peek(x -> {
            if (x == Token.INVALID) {
                System.out.println(String.format(INVALID_TOKEN_MESSAGE, x.content(), x.line(), x.position()));
                System.exit(1);
            } else if (debugMode) {
                System.out.print(String.format(TOKEN_FORMAT, x.name().toLowerCase(), x.content()));
            }
        });
        return this;
    }

    /**
     * Convert each Token into a representing Int, according to the ordinal in its Enum.
     */
    public IntStream toIntStream() {
        return delegate
                .map(Token::id)
                .mapToInt(Integer::intValue);
    }

    public ParseTree toParseTree() {
        delegate = Stream.concat(delegate, Stream.of(Token.EOP));
        return ParseTree.fromTokenSTream(this.delegate);
    }
}
