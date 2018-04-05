package jSol;

public enum ASTType {
    Program("Program"),
    Function("Function"),
    CoRoutine("CoRoutine"),
    Lambda("Lambda"),
    VarUse("VarUse"),
    Var("Var"),
    RefUse("RefUse"),
    Symbol("Symbol"),
    Int("Int"),
    Float("Float"),
    Char("Char"),
    String("String"),
    UNKNOWN("UNKNOWN"),
    BuiltInCall("BuiltInCall"),
    LoadOrCall("LoadOrCall"),
    Load("Load"),
    ObjectRead("ObjectRead"),
    ObjectWrite("ObjectWrite"),
    ObjectCons("ObjectCons"),
    ObjectReadRef("ObjectReadRef"),
    ObjectWriteRef("ObjectWriteRef"),
    ObjectConsRef("ObjectConsRef");

    private String id;

    ASTType(String id){
        this.id = id;
    }

    String id(){return id;}

}
