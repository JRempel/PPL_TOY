package jSol;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TokenStream {
    private static String TOKEN_FORMAT = "%s(%s) ";
    private static String INVALID_TOKEN_MESSAGE = "\n\nInvalid token '%s' on line %d in position %d.";

    private Stream<Token> delegate;

    private TokenStream(Stream<Token> delegate) {
        this.delegate = delegate;
    }

    /**
     * Create a TokenStream from a given InputSteam.
     * @param input - the InputStream to convert.
     */
    public static TokenStream fromInputStream(InputStream input) {
        return new TokenStream(input.getDelegate()
                .map(x -> Arrays.stream(Token.values())
                        .filter(a -> a.matches(x.content()))
                        .map(a -> a.copyOf(x))
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
            } else {
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
}
