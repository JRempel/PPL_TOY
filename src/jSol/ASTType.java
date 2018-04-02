package jSol;

public enum ASTType {
    Program, Function, CoRoutine, Lambda, VarUse, Var, RefUse, Symbol, Int, Float, Char, String,
    UNKNOWN,
    BuiltInCall, LoadOrCall, Load, ObjectRead, ObjectWrite, ObjectCons, ObjectReadRef, ObjectWriteRef, ObjectConsRef
}
