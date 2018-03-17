package jSol;

public class ParseNode {
    private Term type;
    private String content;
    private int line;
    private int position;
    private ParseNode[] children;

    public ParseNode(Term type, String content, int line, int position) {
        this.type = type;
        this.content = content;
        this.line = line;
        this.position = position;
        this.children = new ParseNode[0];
    }

    public Term getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }

    public ParseNode[] getChildren() {
        return children;
    }

    public void setChildren(ParseNode[] children) {
        this.children = children;
    }

    // TODO: DFS Print
}
