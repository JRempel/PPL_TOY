package jSol;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class CodeBlock {


    //Preamble Data
    int preambleLength;
    byte [] preambleBytes;
    ArrayList<OpCode> preambleOperations = new ArrayList<OpCode>();

    LinkedHashMap<UUID, ArrayList<Instruction>> annotatedInstructions = new LinkedHashMap<UUID, ArrayList<Instruction>>();

    public CodeBlock (AbstractSyntaxTree tree){

        AST program = tree.getRoot();

        if (program == null){
            throw new RuntimeException("Cannot generate code block! Root of abstract syntax tree is null!");
        }

        if (program.getAstType() != ASTType.Program){
            throw new RuntimeException("Cannot generate code block! Root of abstract syntax tree is not ASTType.PROGRAM!");
        }

        //Need to determine preamble length before generating to determine addresses for MCL instructions
        this.preambleLength = preambleLength(program);

        //Generate preamble
        generatePreamble(program);
    }

    private void generatePreamble(AST program){

        try(
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bytes);
                ){

                //Make Program/Root Frame
                out.write(MFR(program));

                for (Map.Entry<String, int[]> symbol: program.getSymbols()){

                    switch (symbol.getValue()[0]){
                        //Symbol is a variable
                        /*Assumption: Any variable discovered in the preamble must be a function.

                            Supporting arguments from most logically compelling to 'I choose to believe'

                            1)  Language specification states that functions are the only ones allowed to define
                                variables and other, nested functions.

                            2)  Given that TOY checks function closures for variable defitions, where would a
                                global primitive variable even go so it could be retrieved? Strings block...?
                                That feels wrong.

                            3)  I don't see how I could tell a function apart from a primitive variable given just
                                symbol information. I know at least a top level 'main' function has to exist
                                therefore making this assumption really is the only way I'd know how to proceed next.

                            4)  None of the vm_test toy programs define a global variable at this level that isn't
                                a function.

                            5)  Norbert likes functions, he would define a function that just returns a primitive.

                            6)  I choose to believe

                            Also given the fact that TOY checks local closures for variable definitions
                          */
                        /*TODO - Verify assuption
                            PROOF:
                                TOY Language Grammar definition lists program as starting non-terminal.
                                The only Program production is Program -> TopDef
                                The only TopDef productions are:
                                    TopDef -> FunDef
                                    TopDef -> CofunDef
                                    TopDef -> TypeDef
                                No VarDef in sight. We gucci.
                         */
                        case 0:


                            break;
                        //Symbol is a type
                        case 1:
                            out.write(MOB(symbol,program.getId()));
                            preambleOperations.add(OpCode.MOB);
                            break;
                        //Symbol is a field read from user defined type
                        case 2:
                            out.write(RDF(symbol,program.getId()));
                            preambleOperations.add(OpCode.RDF);
                            break;
                        //Symbol is a field write to a user defined type
                        case 3:
                            out.write(WRF(symbol,program.getId()));
                            preambleOperations.add(OpCode.WRF);
                            break;
                        default:
                            invalidSymbolAttribute(symbol);
                    }
                }

                out.flush();

                this.preambleBytes = bytes.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Instruction> functionByteCode(AST node){
        if (node.getAstType() != ASTType.Function){
            throw new RuntimeException("Cannot generate function bytecode! AST node is not of type function!");
        }

        ArrayList<Instruction> result = new ArrayList<Instruction>();

        


        return result;
    }

    /*
        BYTE ARRAY INSTRUCTION IMPLEMENTATIONS
     */

    /*
        HANDLING LITERAL DATA
     */
    private byte [] INT (int integerValue, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.INT);
        i.annotateParameter(integerValue);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    private byte [] FLT (float floatValue, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.FLT);
        i.annotateParameter(floatValue);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    private byte [] CHR (char characterValue, UUID nodeID) throws IOException{
        //Error Handling
        String singleCharacter = Character.toString(characterValue);
        ByteCode.fuckUTF8(singleCharacter); //Error Handling

        Instruction i = new Instruction(OpCode.CHR);
        i.annotateParameter(singleCharacter.getBytes("UTF-8"));
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    private byte [] STR (Map.Entry<String,int []> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.STR);
        i.annotateParameter(symbol.getValue()[3],symbol.getKey(),3);
        instructionList(nodeID).add(i);
        return i.byteCode();

    }

    private byte [] SYM (Map.Entry<String,int []> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.SYM);
        i.annotateParameter(symbol.getValue()[3], symbol.getKey(), 3);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    /*
        MANIPULATING FRAMES AND CALLING FUNCTIONS
     */
    private byte [] MFR (AST node) throws IOException{
        //Only variables get slots in the frame being made. User defined objects are handled by the data stack!
        //Data Stack != Frame Slots != Call Stack ....well duh, but I'm pretty sleep deprived, so gonna leave this here.
        int variables = 0;

        for (Map.Entry<String,int[]> symbol: node.getSymbols()){
            if (symbol.getValue()[0] == 0){
                variables++;
            }
        }

        Instruction i = new Instruction(OpCode.MFR);
        i.annotateParameter(variables);
        instructionList(node.getId()).add(i);
        return i.byteCode();
    }

    private byte [] STO (Map.Entry<String,int []> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.STO);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(),1);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    private byte [] LOA (AST node) throws IOException{
        Integer [] data = node.tryValueAsIntegerArray();

        Instruction i = new Instruction(OpCode.LOA);
        i.annotateParameter(data[0],0);
        i.annotateParameter(data[1], 1);
        instructionList(node.getId());
        return i.byteCode();
    }

    private byte [] LOC (AST node) throws IOException{
        Integer [] data = node.tryValueAsIntegerArray();

        Instruction i = new Instruction(OpCode.LOC);
        i.annotateParameter(data[0],0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2],2);
        instructionList(node.getId());
        return i.byteCode();
    }

    private byte [] LOJ (AST node) throws IOException{
        Integer [] data = node.tryValueAsIntegerArray();

        Instruction i = new Instruction(OpCode.LOJ);
        i.annotateParameter(data[0],0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2], 2);
        instructionList(node.getId());
        return i.byteCode();
    }

    private byte [] RET (UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.RET);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    /*
        CREATING FUNCTION CLOSURES
     */
    private byte [] MCL (int functionAddress, UUID nodeID) throws IOException {
        Instruction i = new Instruction(OpCode.MCL);
        i.annotateParameter(functionAddress);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    /*
        ACCESSING BUILT-IN FUNCTIONS
     */
    private byte [] CBI (AST node) throws IOException{
        Integer [] data = node.tryValueAsIntegerArray();

        Instruction i = new Instruction(OpCode.CBI);
        i.annotateParameter(data[0],0);
        instructionList(node.getId());
        return i.byteCode();
    }

    private byte [] LBI (AST node) throws IOException{
        Integer [] data = node.tryValueAsIntegerArray();

        Instruction i = new Instruction(OpCode.LBI);
        i.annotateParameter(data[0],0);
        instructionList(node.getId());
        return i.byteCode();
    }
    /*
        MANIPULATING USER-DEFINED TYPES
     */
    private byte [] MOB (Map.Entry<String, int[]> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.MOB);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(),1);
        i.annotateParameter(symbol.getValue()[2],symbol.getKey(), 2);
        instructionList(nodeID);
        return i.byteCode();
    }

    private byte [] RDF (Map.Entry<String,int[]> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.RDF);
        i.annotateParameter(symbol.getValue()[1],symbol.getKey(),1);
        i.annotateParameter(symbol.getValue()[2],symbol.getKey(),2);
        i.annotateParameter(symbol.getValue()[3],symbol.getKey(),3);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    private byte [] WRF (Map.Entry<String, int[]> symbol, UUID nodeID) throws IOException{
        Instruction i = new Instruction(OpCode.WRF);
        i.annotateParameter(symbol.getValue()[1],symbol.getKey(),1);
        i.annotateParameter(symbol.getValue()[2],symbol.getKey(),2);
        i.annotateParameter(symbol.getValue()[3],symbol.getKey(),3);
        instructionList(nodeID).add(i);
        return i.byteCode();
    }

    /*
        PREAMBLE GENERATION UTILITIES
     */
    private int preambleLength (AST program){
        int result = OpCode.MFR.size(); //Account for base program frame
        result += OpCode.LOJ.size();    //Account for LOJ at the end of preamble

        for (Map.Entry<String, int[]> symbol: program.getSymbols()){
            switch (symbol.getValue()[0]){
                //Variable
                case 0:
                    result += OpCode.MCL.size();
                    result += OpCode.STO.size();
                    break;
                //Type
                case 1:
                    result += OpCode.MOB.size();
                    break;
                //Field Read
                case 2:
                    result += OpCode.RDF.size();
                    break;
                //Field Write
                case 3:
                    result += OpCode.WRF.size();
                    break;
                default:
                    invalidSymbolAttribute(symbol);
            }
        }

        return result;
    }

    /*
        ERROR HANDLING
     */
    private void invalidSymbolAttribute(Map.Entry<String, int []> symbol){

        throw new RuntimeException("Could not generate preamble for code block! Program level symbol attribute is invalid!\n" +
                "Got " + symbol.getValue()[0] + " but expected one of the following:\n" +
                "\t0 = variable\n" +
                "\t1 = user type\n" +
                "\t2 = field read from user defined type\n" +
                "\t3 = field write to user defined type\n");
    }

    /*
        ANNOTATION UTILS
     */
    private ArrayList instructionList(UUID nodeId){

        if (annotatedInstructions.containsKey(nodeId)){
            return annotatedInstructions.get(nodeId);
        }else{
            annotatedInstructions.put(nodeId, new ArrayList<Instruction>());
            return annotatedInstructions.get(nodeId);
        }

    }


}
