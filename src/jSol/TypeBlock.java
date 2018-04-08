package jSol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeBlock {

    int length;
    byte[] byteLength = new byte[2];
    LinkedHashMap<String, byte[]> types = new LinkedHashMap<>();


    public TypeBlock(AbstractSyntaxTree tree) {
        this.length = tree.getTypes().size();
        setLength(this.length);
        for (var e : tree.getTypes()) {
            addType(e.getKey());
        }
    }

    public int getLength() {
        return length;
    }

    private void setLength(int length) {
        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {
            out.writeChar(length);
            this.byteLength = bytes.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addType(String type) {
        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {

            ByteCode.fuckUTF8(type); //Error Handling
            byte[] ascii = type.getBytes("UTF-8"); //first 128 UTF-8 code points match 1-to-1 with ASCII
            out.write(ascii);
            out.write(0x0); //Pad with '\0'

            this.types.put(type, bytes.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getRawBytes() {
        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {

            //Write type block length metadata
            out.write(byteLength);

            //Write '\0' appended strings
            for (Map.Entry<String, byte[]> e : this.types.entrySet()) {
                out.write(e.getValue());
            }

            return bytes.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
