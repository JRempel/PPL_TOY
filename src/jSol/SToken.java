package jSol;

public class SToken {
    private String content;
    private int line;
    private int position;

    public SToken(String content, int line, int position){
        this.content = content;
        this.line = line;
        this.position = position;
    }

    public String content() {
        return content;
    }

    public SToken setContent(String content) {
        this.content = content;
        return this;
    }

    public int line() {
        return line;
    }

    public int position() {
        return position;
    }
}
