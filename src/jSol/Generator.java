package jSol;

import javax.xml.crypto.Data;
import java.io.*;

public class Generator {

    public static void main(String[] args) {

        AbstractSyntaxTree root = new AbstractSyntaxTree();
        //Strings
        root.addString("x");
        root.addString("x=");
        root.addString("y");
        root.addString("y=");
        root.addString("sqrt");
        root.addString("go");
        root.addString("vector_len");
        root.addString("main");
        root.addString("len(3,4)=");
        //Types
        root.generatorAddType("vector");
        //Program
        AST n000 = new AST(ASTType.Program);
        root.setRoot(n000);
        //vector((1,0,2))
        n000.addSymbol("vector", new int[]{1, 0, 2});
        //x((2,0,0,0))
        n000.addSymbol("x", new int[]{2, 0, 0, 0});
        //x=((3,0,0,1))
        n000.addSymbol("x=", new int[]{3, 0, 0, 1});
        //y((2,0,1,2))
        n000.addSymbol("y", new int[]{2, 0, 1, 2});
        //y=((3,0,1,3))
        n000.addSymbol("y=", new int[]{3, 0, 1, 3});
        //sqrt((0,0,4))
        n000.addSymbol("sqrt", new int[]{0, 0, 4});
        //vector_len((0,1,6))
        n000.addSymbol("vector_len", new int[]{0, 1, 6});
        //main((0,2,7))
        n000.addSymbol("main", new int[]{0, 2, 7});
        //Function
        AST n001 = new AST(ASTType.Function);
        //x((0,0))
        n001.addSymbol("x", new int[]{0, 0, 0});
        //go((0,1,5))
        n001.addSymbol("go", new int[]{0, 1, 5});
        n000.addStatement(n001);
        //Var = x
        AST n002b = new AST(ASTType.Var);
        n002b.setValue("0");
        n001.addStatement(n002b);
        //Function
        AST n002 = new AST(ASTType.Function);
        n001.addStatement(n002);
        //VarUse = dup
        AST n003 = new AST(ASTType.BuiltInCall);
        n003.setValue("56");
        n002.addStatement(n003);
        //VarUse = dup
        AST n004 = new AST(ASTType.BuiltInCall);
        n004.setValue("56");
        n002.addStatement(n004);
        //VarUse = x
        AST n005 = new AST(ASTType.LoadOrCall);
        n005.setValue("1,0,0");
        n002.addStatement(n005);
        //VarUse = swap
        AST n006 = new AST(ASTType.BuiltInCall);
        n006.setValue("64");
        n002.addStatement(n006);
        //VarUse = /
        AST n007 = new AST(ASTType.BuiltInCall);
        n007.setValue("17");
        n002.addStatement(n007);
        //VarUse = +
        AST n008 = new AST(ASTType.BuiltInCall);
        n008.setValue("14");
        n002.addStatement(n008);
        //Int = 2
        AST n009 = new AST(ASTType.Int);
        n009.setValue("2");
        n002.addStatement(n009);
        //VarUse = /
        AST n010 = new AST(ASTType.BuiltInCall);
        n010.setValue("17");
        n002.addStatement(n010);
        //VarUse = dup
        AST n011 = new AST(ASTType.BuiltInCall);
        n011.setValue("56");
        n002.addStatement(n011);
        //VarUse = rotl
        AST n012 = new AST(ASTType.BuiltInCall);
        n012.setValue("60");
        n002.addStatement(n012);
        //VarUse = !=
        AST n013 = new AST(ASTType.BuiltInCall);
        n013.setValue("9");
        n002.addStatement(n013);
        //RefUse = go
        AST n014 = new AST(ASTType.Load);
        n014.setValue("1,1,5");
        n002.addStatement(n014);
        //VarUse = ?
        AST n015 = new AST(ASTType.BuiltInCall);
        n015.setValue("1");
        n002.addStatement(n015);
        //Var = 1
        AST n016 = new AST(ASTType.Var);
        n016.setValue("1");
        n001.addStatement(n016);
        //Int = 1
        AST n017 = new AST(ASTType.Int);
        n017.setValue("1");
        n001.addStatement(n017);
        //LoadOrCall= 0,1,5 (possibly VarUse = go
        AST n017a = new AST(ASTType.LoadOrCall);
        n017a.setValue("0,1,5");
        n001.addStatement(n017a);
        //Var = 0
        AST n018 = new AST(ASTType.Var);
        n018.setValue("0");
        n000.addStatement(n018);
        //Function
        AST n020 = new AST(ASTType.Function);
        n000.addStatement(n020);

        AST n021 = new AST(ASTType.BuiltInCall);
        n021.setValue("56");
        n020.addStatement(n021);

        AST n022 = new AST(ASTType.ObjectRead);
        n022.setValue("0,0,0");
        n020.addStatement(n022);

        AST n023 = new AST(ASTType.BuiltInCall);
        n023.setValue("56");
        n020.addStatement(n023);

        AST n024 = new AST(ASTType.BuiltInCall);
        n024.setValue("16");
        n020.addStatement(n024);

        AST n025 = new AST(ASTType.BuiltInCall);
        n025.setValue("64");
        n020.addStatement(n025);

        AST n026 = new AST(ASTType.ObjectRead);
        n026.setValue("0,1,2");
        n020.addStatement(n026);

        AST n027 = new AST(ASTType.BuiltInCall);
        n027.setValue("56");
        n020.addStatement(n027);

        AST n028 = new AST(ASTType.BuiltInCall);
        n028.setValue("16");
        n020.addStatement(n028);

        AST n029 = new AST(ASTType.BuiltInCall);
        n029.setValue("14");
        n020.addStatement(n029);

        AST n030 = new AST(ASTType.LoadOrCall);
        n030.setValue("1,0,4");
        n020.addStatement(n030);

        AST n031 = new AST(ASTType.Var);
        n031.setValue("1");
        n000.addStatement(n031);

        //Function
        AST n032 = new AST(ASTType.Function);
        n000.addStatement(n032);

        AST n033 = new AST(ASTType.ObjectCons);
        n033.setValue("0,2");
        n032.addStatement(n033);

        AST n034 = new AST(ASTType.Int);
        n034.setValue("3");
        n032.addStatement(n034);

        AST n035 = new AST(ASTType.ObjectWrite);
        n035.setValue("0,0,1");
        n032.addStatement(n035);

        AST n036 = new AST(ASTType.Int);
        n036.setValue("4");
        n032.addStatement(n036);

        AST n037 = new AST(ASTType.ObjectWrite);
        n037.setValue("0,1,3");
        n032.addStatement(n037);

        AST n038 = new AST(ASTType.LoadOrCall);
        n038.setValue("1,1,6");
        n032.addStatement(n038);

        AST n039 = new AST(ASTType.BuiltInCall);
        n039.setValue("36");
        n032.addStatement(n039);

        AST n040 = new AST(ASTType.String);
        n040.setValue("8");
        n032.addStatement(n040);

        AST n041 = new AST(ASTType.BuiltInCall);
        n041.setValue("64");
        n032.addStatement(n041);

        AST n042 = new AST(ASTType.BuiltInCall);
        n042.setValue("29");
        n032.addStatement(n042);

        AST n043 = new AST(ASTType.BuiltInCall);
        n043.setValue("45");
        n032.addStatement(n043);

        AST n044 = new AST(ASTType.BuiltInCall);
        n044.setValue("46");
        n032.addStatement(n044);

        AST n045 = new AST(ASTType.Var);
        n045.setValue("2");
        n000.addStatement(n045);

        root.generatorPrint();

        ByteCode byteCode = new ByteCode(root);
        byteCode.generate("test.byte");



    }



}

