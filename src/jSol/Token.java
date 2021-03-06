package jSol;

import java.util.regex.Pattern;

public enum Token {
    /**
     *IMPORTANT: Keep ID at the end because other tokens are a prefix for it.
     */
    INT("[+-]?[0-9]+|0x[0-9a-fA-F]+|0b[01]+"),
    FLOAT("[+-]?([0-9]*\\.)?[0-9]+([Ee][+-]?[0-9]+)?"),
    CHAR("'[^\\\\']'|'\\\\.'"),
    STRING("\"(([^\"]|[^\\\\]|[\\\\.])*)\""),
    FUN("fun"),
    COFUN("cofun"),
    TYPE("type"),
    VAR("var"),
    SYMBOL(":.+"),
    LAMBDABEGIN("\\["),
    LAMBDAEND("\\]"),
    CFLAMBDABEGIN("\\[\\|"),
    CFLAMBDAEND("\\|\\]"),
    REFERENCE("%[-+%]|%\\.[^0-9].*|%[-+][^.0-9].*|%[^-+.:%0-9\'\"].*"),
    END("\\."),
    ID("[-+%]|\\.[^0-9].*|[-+][^.0-9].*|[^-+.:%0-9\'\"].*"),
    INVALID(""),
    // End of Program
    EOP("");

    public static Pattern COMMENT = Pattern.compile("//");
    public static Pattern COMMENT_PATTERN = Pattern.compile("//.*");

    private Pattern regex;
    private String content;
    private int line;
    private int position;

    Token(String regex) {
        this.regex = Pattern.compile(regex);
        this.content = "";
        this.line = 0;
        this.position = 0;
    }

    public Token copyOf(SToken string) {
        this.content = string.content();
        this.line = string.line();
        this.position = string.position();
        return this;
    }

    public boolean matches(String input) {
        return regex.matcher(input).matches();
    }

    public String content() {
        return content;
    }

    public int id () {
        return this.ordinal();
    }

    public int line() {
        return line;
    }

    public int position() {
        return position;
    }
}
