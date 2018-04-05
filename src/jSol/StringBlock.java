package jSol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class StringBlock {

    int length;
    byte[] byteLength = new byte[2];
    LinkedHashMap<String, byte[]> strings = new LinkedHashMap<String, byte[]>();

    public StringBlock (AbstractSyntaxTree tree){
        setLength(tree.getStrings().size());
        for (String s: tree.getStrings()){
            addString(s);
        }
    }

    public int getLength() {
        return length;
    }

    private void setLength(int length) {
        this.length = length;

        try(
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                )
        {

            out.writeChar(length);
            this.byteLength = bytes.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void addString (String s){

        try(
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                ){

                ByteCode.fuckUTF8(s);
                byte[] ascii = s.getBytes("UTF-8"); //first 128 UTF-8 code points match 1-to-1 with ASCII
                out.write(ascii);
                out.write(0x0); //Pad with '\0'


            this.strings.put(s,bytes.toByteArray());

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public byte[] getRawBytes() {

        try (
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                ){

                //Write string block length metadata
                out.write(byteLength);

                //Write '\0' appended strings
                for (Map.Entry<String,byte[]> e: this.strings.entrySet()){
                    out.write(e.getValue());
                }

                return bytes.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}
