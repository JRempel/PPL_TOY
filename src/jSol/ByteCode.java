package jSol;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteCode {

    StringBlock stringBlock;
    TypeBlock typeBlock;
    CodeBlock codeBlock;

    public ByteCode (AbstractSyntaxTree tree){
        this.stringBlock = new StringBlock(tree);
        this.typeBlock = new TypeBlock(tree);
        this.codeBlock = new CodeBlock(tree);
    }

    public void generate(String filename){
        //Report null pointers if they exist
        if (    stringBlock == null ||
                typeBlock == null ||
                codeBlock == null){
            throw new RuntimeException ("Cannot generate bytecode, the following blocks are null:\n" +
                    (stringBlock == null? "String Block\n": "") +
                    (typeBlock == null? "Type Block": "") +
                    (codeBlock == null? "Code Block": "")
            );
        }
        File f = new File (filename);
        try (
                FileOutputStream fout = new FileOutputStream(f);
                DataOutputStream dout = new DataOutputStream(fout);
                )
        {
            //Write String Block
            dout.write(getStringBlock().getRawBytes());


        }catch(IOException e){
            e.printStackTrace();
        }


    }

    public StringBlock getStringBlock() {
        return stringBlock;
    }

    public void setStringBlock(StringBlock stringBlock) {
        this.stringBlock = stringBlock;
    }

    public TypeBlock getTypeBlock() {
        return typeBlock;
    }

    public void setTypeBlock(TypeBlock typeBlock) {
        this.typeBlock = typeBlock;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }
}
