package jSol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Instruction {

    //Annotation Streams
    private ByteArrayOutputStream annotationBytes;
    private DataOutputStream annotationOut;

    private Instruction childMFR;


    private OpCode opCode;
    private ArrayList<byte[]> paramValues = new ArrayList<>();
    private byte [] address;
    private byte [] code;

    private boolean annotated = false;

    /*
        Node Tracing Structures

        To be able to trace which index was used for which parameter from a given node value array.
        Ex: Debugging LoadOrCall 0,1,5
    */
    private int [] nodeValueIndexes;

    //Symbol Tracing Structures
    private String symbol;
    private int [] symbolIndexes;

    public Instruction (OpCode opCode){
        nodeValueIndexes = new int [opCode.paramCount()];
        symbolIndexes = new int [opCode.paramCount()];
        //All instructions have opCode as first parameter
        try {
            this.opCode = opCode;
            annotateParameter(opCode.hexValue());
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public OpCode getOpCode() {
        return opCode;
    }

    public void setOpCode(OpCode opCode) {
        this.opCode = opCode;
    }

    public ArrayList<byte[]> getParamValues() {
        return paramValues;
    }

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(int address) {
        try(
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                ){
            out.writeInt(address);
            out.flush();
            this.address = bytes.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public byte [] byteCode (){
        if (code != null){
            return code;
        }
        //Error Handling
        if (paramValues.size() != getOpCode().paramCount()){
            throw new RuntimeException(
                    "Cannot generate instruction bytecode, " +
                    (paramValues.size() > getOpCode().paramCount()?
                            "too many parameters!" : "too few paramters!" ) + "\n" +
                    getOpCode().name() + " expects " + getOpCode().paramCount() +
                    " but got " + Integer.toString(paramValues.size()));
        }

        //Produce ByteCode
        try (
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                ){

                    for (byte [] fragment: getParamValues()){
                        out.write(fragment);
                    }

                out.flush();
                this.code = bytes.toByteArray();
                return code;

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }

    //Instruction Parameter Annotations

    //Annotate with Node Value Index
    public void annotateParameter (Object data, int nodeValueIndex) throws IOException
    {

        nodeValueIndexes[paramValues.size()] = nodeValueIndex; //This has to happen before processData

        annotateSetup();

        processData(data);

        annotationOut.flush();
        annotationOut.close();

        addParameter(annotationBytes.toByteArray());
        annotationBytes.close();

    }

    //Annotate with Symbols
    public void annotateParameter (Object data, String symbol, int symbolIndex) throws IOException
    {
        if (symbol == null){
            System.out.println("Symbol annoation is null!");
        }

        if (this.symbol == null){
            this.symbol = symbol;
        }

        symbolIndexes[paramValues.size()] = symbolIndex; //This has to happen before processData

        annotateSetup();

        processData(data);

        annotationOut.flush();
        annotationOut.close();

        addParameter(annotationBytes.toByteArray());
        annotationBytes.close();

    }

    //Annotate Simply
    public void annotateParameter (Object data) throws IOException
    {
        annotateSetup();

        processData(data);

        annotationOut.flush();
        annotationOut.close();

        addParameter(annotationBytes.toByteArray());
        annotationBytes.close();

    }

    private void processData (Object data) throws IOException{
        if (data.getClass().isArray()){
            annotateByteArray((byte[])data);
        }

        if (data.getClass().equals(byte.class) || data.getClass().equals(Byte.class)) {
            annotateByte((byte)data);
        }

        if (data.getClass().equals(int.class) || data.getClass().equals(Integer.class)){
            annotateInt((int) data);
        }

        if (data.getClass().equals(float.class) || data.getClass().equals(Float.class)){
            annotateFloat((float)data);
        }
    }

    //Type Specific ByteCode Rules
    private void annotateByte(byte b) throws IOException{
        annotationOut.write(b);
    }

    private void annotateByteArray (byte [] data) throws IOException{
        annotationOut.write(data);
    }

    private void annotateInt (int data) throws IOException{
        /*
            Generate the appropriate number of bytes given integer data.
            ex: INT instruction should encode a 32 bit integer.
                LOC instruction should encode single byte offsets.

            Correct bytecode is generated by checking to see what size the next parameter ought to be.
         */
        switch(getOpCode().paramSizes()[this.paramValues.size()]){
            //For 32-bit encoded int values
            case 4:
                annotationOut.writeInt(data);
                break;
            //For 16-bit offsets
            case 2:
                annotationOut.writeChar(data);
                break;
            //All else 8-bit representation
            default:
                annotationOut.write(data);

        }
    }

    private void annotateFloat (float data) throws IOException{
        annotationOut.writeFloat(data);
    }

    private void addParameter (byte [] paramData) throws IOException{
        paramValues.add(paramData);

        //If all parameters have been annotated, clean up
        if (paramValues.size() == getOpCode().size()){
            annotated = true;
        }
    }

    //Misc Utils
    private void annotateSetup(){
        if (annotated){
            throw new RuntimeException ("Instruction already annotated!");
        }

        annotationBytes = new ByteArrayOutputStream();
        annotationOut = new DataOutputStream(annotationBytes);
    }

    public Instruction getChildMFR() {
        return childMFR;
    }

    public void setChildMFR(Instruction childMFR) {
        this.childMFR = childMFR;
    }
}
