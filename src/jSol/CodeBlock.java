package jSol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class CodeBlock {

    //Program Data
    StringBlock stringBlock;
    TypeBlock typeBlock;


    //Preamble Data
    int preambleLength;
    byte[] preambleBytes;
    ArrayList<OpCode> preambleOperations = new ArrayList<>();
    ArrayList<Instruction> topDefInstructions = new ArrayList<>();
    int mainOffset = -1;

    ArrayList<NodeInstructions> annotatedInstructions = new ArrayList<>();
    ArrayList<NodeInstructions> annotatedPreambleInstructions = new ArrayList<>();
    ArrayList<NodeInstructions> annotatedTopLevelInstructions = new ArrayList<>();

    byte[] bodyBytes;
    byte[] blockBytes;


    public CodeBlock(AbstractSyntaxTree tree, StringBlock stringBlock, TypeBlock typeBlock) {
        var program = tree.getRoot();
        this.stringBlock = stringBlock;
        this.typeBlock = typeBlock;

        if (program == null) {
            throw new RuntimeException("Cannot generate code block! Root of abstract syntax tree is null!");
        }

        if (program.getAstType() != ASTType.Program) {
            throw new RuntimeException("Cannot generate code block! Root of abstract syntax tree is not ASTType.PROGRAM!");
        }

        //Need to determine preamble length before generating to determine addresses for MCL instructions
        this.preambleLength = preambleLength(program);


        //Generate ByteCode structure
        try {

            for (var topNode : program.getStatements()) {
                if (topNode.getAstType() == ASTType.Function) {
                    byteCode(topNode, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Generate Body byteCode
        resolveAddressesAndBuildBodyByteCode();
        //Generate Preamble byteCode
        generatePreamble(program);

        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {
            out.write(preambleBytes);
            out.write(bodyBytes);
            out.flush();

            this.blockBytes = bytes.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resolveAddressesAndBuildBodyByteCode() {
        int counter = preambleLength;

        for (NodeInstructions ni : annotatedInstructions) {
            for (Instruction i : ni.getInstructions()) {
                i.setAddress(counter);
                counter += i.getOpCode().size();
            }
        }

        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {

            for (NodeInstructions ni : annotatedInstructions) {
                for (Instruction i : ni.getInstructions()) {
                    if (i.getOpCode() == OpCode.MCL) {
                        i.annotateParameter(i.getChildMFR().getAddress());
                        i.byteCode();
                    }
                    out.write(i.byteCode());
                }
            }

            out.flush();
            this.bodyBytes = bytes.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePreamble(AST program) {

        try (
                var bytes = new ByteArrayOutputStream();
                var out = new DataOutputStream(bytes);
        ) {

            //Make Program/Root Frame
            out.write(MFR(program, program, -1).byteCode());
            int variableOffsetCounter = -1;
            int mainOffsetCounter = -1;
            for (int i = 0; i < program.getSymbols().size(); i++) {
                var symbol = program.getSymbols().get(i);
                mainOffsetCounter++;
                if (symbol.getValue()[0] == 0) {
                    variableOffsetCounter++;
                    if (symbol.getKey().equals("main")) {
                        this.mainOffset = mainOffsetCounter;
                    }
                    var closureInstruction = MCL(program);
                    closureInstruction.annotateParameter(annotatedTopLevelInstructions.get(variableOffsetCounter).getInstructions().get(0).getAddress());
                    out.write(closureInstruction.byteCode());
                    out.write(STO(symbol, program));
                }
            }

            out.write(LOJ(program.getSymbols().get(mainOffset), program));
            out.flush();

            this.preambleBytes = bytes.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Instruction byteCode(AST node, int level) throws IOException {
        var myMFR = MFR(node, node, level); //Make frame for the function
        myMFR.byteCode();
        for (var child : node.getStatements()) {
            switch (child.getAstType()) {
                //TODO Handle Builtin Function Loads
                case Var:
                    STO(child, node);
                    break;
                case Function:
                    //Recurse through function AST Nodes
                    var myMCL = MCL(node);
                    var childMFR = byteCode(child, level + 1);
                    myMCL.setChildMFR(childMFR);
                    break;
                case Lambda:
                    var mcl = MCL(node);
                    var lambdaChild = byteCode(child, level + 1);
                    mcl.setChildMFR(lambdaChild);
                    break;
                case Int:
                    INT(Integer.parseInt(child.getValue()), node);
                    break;
                case Float:
                    FLT(Float.parseFloat(child.getValue()), node);
                    break;
                case String:
                    STR(Integer.parseInt(child.getValue()), node);
                    break;
                case Char:
                    CHR(child.getValue().charAt(0), node);
                    break;
                case Symbol:
                    SYM(child.getSecondPassVal()[0], node);
                    break;
                case LoadOrCall:
                    LOC(child, node);
                    break;
                case BuiltInCall:
                    CBI(child, node);
                    break;
                case Load:
                    LOA(child, node);
                    break;
                case ObjectRead:
                    RDF(child, node);
                    break;
                case ObjectWrite:
                    WRF(child, node);
                    break;
                case ObjectCons:
                    MOB(child, node);
                    break;
                case RefUse:
                case VarUse:
                case ObjectConsRef:
                case ObjectReadRef:
                case ObjectWriteRef:
                case UNKNOWN:
                case Program:
                    System.out.println("Error: second-pass AST incomplete or has errors...");
                    System.exit(1);
                case CoRoutine:
                    System.out.println("Co-Routine feature not implemented yet...");
                    System.exit(1);
            }
        }

        RET(node);//Return from the function

        return myMFR;
    }

    private Map.Entry<String, int[]> searchScopeForType(int typeId, AST searchNode) {
        for (var localScopeSymbol : searchNode.getSymbols()) {
            if (localScopeSymbol.getValue()[0] == 1 && //If a local symbol is a type definition
                    localScopeSymbol.getValue()[1] == typeId) { //And it's typeId matches yours
                return localScopeSymbol;
            }
        }
        return null;
    }

    /*
        BYTE ARRAY INSTRUCTION IMPLEMENTATIONS
     */

    /*
        HANDLING LITERAL DATA
     */
    private byte[] INT(int integerValue, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.INT);
        i.annotateParameter(integerValue);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] FLT(float floatValue, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.FLT);
        i.annotateParameter(floatValue);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] CHR(char characterValue, AST nodeID) throws IOException {
        //Error Handling
        var singleCharacter = Character.toString(characterValue);
        ByteCode.fuckUTF8(singleCharacter); //Error Handling
        var i = new Instruction(OpCode.CHR);
        i.annotateParameter(singleCharacter.getBytes("UTF-8"));
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] STR(int stringOffset, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.STR);
        i.annotateParameter(stringOffset);
        instructionList(nodeID, i);
        return i.byteCode();

    }

    private byte[] SYM(int stringOffset, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.SYM);
        i.annotateParameter(stringOffset);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    /*
        MANIPULATING FRAMES AND CALLING FUNCTIONS
     */
    private Instruction MFR(AST node, AST nodeID, int level) throws IOException {
        //Only variables get slots in the frame being made. User defined objects are handled by the data stack!
        //Data Stack != Frame Slots != Call Stack ....well duh, but I'm pretty sleep deprived, so gonna leave this here.
        int variables = 0;

        for (var symbol : node.getSymbols()) {
            if (symbol.getValue()[0] == 0) {
                variables++;
            }
        }

        var i = new Instruction(OpCode.MFR);
        i.annotateParameter(variables);
        instructionList(nodeID, i);
        if (level == 0) {
            topLevelInstructionList(nodeID, i);
        }

        return i;
    }

    private byte[] STO(Map.Entry<String, int[]> symbol, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.STO);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(), 1);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] STO(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.STO);
        i.annotateParameter(data[0], 1);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] LOA(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.LOA);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] LOC(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.LOC);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2], 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] LOJ(Map.Entry<String, int[]> symbol, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.LOJ);
        i.annotateParameter(symbol.getValue()[0], symbol.getKey(), 0);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(), 1);
        i.annotateParameter(symbol.getValue()[2], symbol.getKey(), 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] LOJ(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.LOJ);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2], 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] RET(AST nodeID) throws IOException {
        var i = new Instruction(OpCode.RET);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    /*
        CREATING FUNCTION CLOSURES
     */
    //Puts a blank MCL instruction in the list
    private Instruction MCL(AST nodeID) throws IOException {
        var i = new Instruction(OpCode.MCL);
        instructionList(nodeID, i);
        return i;
    }

    /*
        ACCESSING BUILT-IN FUNCTIONS
     */
    private byte[] CBI(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.CBI);
        i.annotateParameter(data[0], 0);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] LBI(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.LBI);
        i.annotateParameter(data[0], 0);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    /*
        MANIPULATING USER-DEFINED TYPES
     */
    private byte[] MOB(Map.Entry<String, int[]> symbol, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.MOB);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(), 1);
        i.annotateParameter(symbol.getValue()[2], symbol.getKey(), 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] MOB(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.MOB);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] RDF(Map.Entry<String, int[]> symbol, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.RDF);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(), 1);
        i.annotateParameter(symbol.getValue()[2], symbol.getKey(), 2);
        i.annotateParameter(symbol.getValue()[3], symbol.getKey(), 3);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] RDF(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.RDF);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2], 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] WRF(Map.Entry<String, int[]> symbol, AST nodeID) throws IOException {
        var i = new Instruction(OpCode.WRF);
        i.annotateParameter(symbol.getValue()[1], symbol.getKey(), 1);
        i.annotateParameter(symbol.getValue()[2], symbol.getKey(), 2);
        i.annotateParameter(symbol.getValue()[3], symbol.getKey(), 3);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    private byte[] WRF(AST node, AST nodeID) throws IOException {
        var data = node.getSecondPassVal();
        var i = new Instruction(OpCode.WRF);
        i.annotateParameter(data[0], 0);
        i.annotateParameter(data[1], 1);
        i.annotateParameter(data[2], 2);
        instructionList(nodeID, i);
        return i.byteCode();
    }

    /*
        PREAMBLE GENERATION UTILITIES
     */
    private int preambleLength(AST program) {
        int result = OpCode.MFR.size(); //Account for base program frame
        result += OpCode.LOJ.size();    //Account for LOJ at the end of preamble

        for (Map.Entry<String, int[]> symbol : program.getSymbols()) {
            if (symbol.getValue()[0] == 0) {
                result += OpCode.MCL.size();
                result += OpCode.STO.size();
            }
        }

        return result;
    }

    /*
        ERROR HANDLING
     */
    private void invalidSymbolAttribute(Map.Entry<String, int[]> symbol) {
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
    private void instructionList(AST node, Instruction i) {
        if (node.getAstType() == ASTType.Program) {
            if (annotatedPreambleInstructions.size() == 0) {
                var preamble = new NodeInstructions(node);
                annotatedPreambleInstructions.add(preamble);
            }

            annotatedPreambleInstructions.get(0).getInstructions().add(i);

            return;
        }

        boolean added = false;

        for (var instructions : annotatedInstructions) {
            if (instructions.getNode().getId().equals(node.getId())) {
                instructions.getInstructions().add(i);
                added = true;
            }
        }

        if (!added) {
            var newInstructions = new NodeInstructions(node);
            annotatedInstructions.add(newInstructions);
            newInstructions.getInstructions().add(i);
        }
    }

    private NodeInstructions nodeInstructions(AST node) {
        for (var instructions : annotatedInstructions) {
            if (instructions.getNode().getId().equals(node.getId())) {
                return instructions;
            }
        }
        return null;
    }

    private void topLevelInstructionList(AST node, Instruction i) {
        var topLevel = new NodeInstructions(node);
        topLevel.getInstructions().add(i);
        annotatedTopLevelInstructions.add(topLevel);

        return;

    }

    public byte[] getRawBytes() {
        return blockBytes;
    }


}
