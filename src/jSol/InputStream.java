package jSol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.regex.MatchResult;

public class InputStream {
    private static Pattern StringSplitterator = Pattern.compile("\"(([^\"]|[^\\\\]|[\\\\.])*)\"|'([^']|[^\\\\]')'|'(\\\\.)'|[^\\s]+");

    private Stream<SToken> delegate;

    private InputStream(Stream<SToken> delegate) {
        this.delegate = delegate;
    }

    Stream<SToken> getDelegate() {
        return delegate;
    }

    /**
     * Create an InputStream from a file, where each SToken is a line.
     * @param filename - the file to read from.
     */
    public static InputStream from(String filename) {
        try {
            var input = Files
                    .lines(Paths.get(filename), StandardCharsets.US_ASCII)
                    .collect(Collectors.toList());
            var stream = IntStream
                    .range(0, input.size())
                    .mapToObj(x -> new SToken(input.get(x), x, 0));
            return new InputStream(stream);
        } catch (IOException e) {
            System.out.println("Unable to read file.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove any characters which match @Link{Token#COMMENT_PATTERN}
     */
    public InputStream unComment() {
        delegate = delegate.map(x -> Token.COMMENT.matcher(x.content()).find() ?
                x.setContent(Token.COMMENT_PATTERN.matcher(x.content()).replaceAll("")) : x);
        return this;
    }

    /**
     * Split each SToken on whitespace, and filter any empty-string STokens.
     */
    public InputStream splitterate() {
        delegate = delegate
                .map(x -> {
                    var lexemes = StringSplitterator.matcher(x.content())
                            .results()
                            .map(MatchResult::group)
                            .toArray(String[]::new);
                    return IntStream
                            .range(0, lexemes.length)
                            .mapToObj(y -> new SToken(lexemes[y], x.line(), y))
                            .toArray(SToken[]::new);
                })
                .flatMap(Arrays::stream)
                .filter(x -> !"".equals(x.content()));
        return this;
    }

    /**
     * Convert the InputStream into a TokenStream.
     */
    public TokenStream tokenize() {
        return TokenStream.fromInputStream(this);
    }
}
