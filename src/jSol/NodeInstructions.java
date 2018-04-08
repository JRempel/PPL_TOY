package jSol;

import java.util.ArrayList;

public class NodeInstructions {

    private AST node;
    private ArrayList<Instruction> instructions = new ArrayList<>();
    private ArrayList<Instruction> childMFRs = new ArrayList<>();
    private ArrayList<Instruction> parentMCL = new ArrayList<>();

    public NodeInstructions(AST node){
        this.node = node;
    }

    public void addInstruction(Instruction i){
        getInstructions().add(i);
    }

    public AST getNode() {
        return node;
    }

    public void setNode(AST node) {
        this.node = node;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }




}
