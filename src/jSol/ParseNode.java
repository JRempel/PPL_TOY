package jSol;

public class ParseNode {
    private Term type;
    private String content;
    private int line;
    private int position;
    private ParseNode[] children;

    public ParseNode(Term type, String content, int line, int position) {
        this.content = content;
        this.line = line;
        this.position = position;
        this.children = new ParseNode[0];
    }

    public Term getType() {
        return type;
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
