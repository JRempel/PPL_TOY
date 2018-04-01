package jSol;

public class Generator {

    public static void main (String [] args){

        AbstractSyntaxTree root =  new AbstractSyntaxTree();

        //Program
        AST n000 = new AST(ASTType.Program);
        root.setRoot(n000);
        //vector((1,0,2))
        n000.addSymbol("vector", new int [] {1,0,2});
        //x((2,0,0,0))
        n000.addSymbol("x", new int [] {2,0,0,0});
        //x=((3,0,0,1))
        n000.addSymbol("x=", new int[] {3,0,0,1});
        //y((2,0,1,2))
        n000.addSymbol("y", new int [] {2,0,1,2});
        //y=((3,0,1,3))
        n000.addSymbol("y=",new int [] {3,0,1,3});
        //sqrt((0,0,4))
        n000.addSymbol("sqrt", new int [] {0,0,4});
        //vector_len((0,1,6))
        n000.addSymbol("vector_len", new int [] {0,1,6});
        //main((0,2,7))
        n000.addSymbol("main", new int [] {0,2,7});
            //Function
            AST n001 = new AST(ASTType.Function);
            //x((0,0))
            n001.addSymbol("x", new int[] {0,0});
            //go((0,1,5))
            n001.addSymbol("go", new int[] {0,1,5});
            n000.addStatement(n001);
                //Var = x
                AST n002b = new AST(ASTType.Var);
                n001.addStatement(n002b);
                //Function
                AST n002 = new AST(ASTType.Function);
                n001.addStatement(n002);
                    //VarUse = dup
                    AST n003 = new AST(ASTType.VarUse);
                    n002.addStatement(n003);
                    //VarUse = dup
                    AST n004 = new AST(ASTType.VarUse);
                    n002.addStatement(n004);
                    //VarUse = x
                    AST n005 = new AST(ASTType.VarUse);
                    n002.addStatement(n005);
                    //VarUse = swap
                    AST n006 = new AST(ASTType.VarUse);
                    n002.addStatement(n006);
                    //VarUse = /
                    AST n007 = new AST(ASTType.VarUse);
                    n002.addStatement(n007);
                    //VarUse = +
                    AST n008 = new AST(ASTType.VarUse);
                    n002.addStatement(n008);
                    //Int = 2
                    AST n009 = new AST(ASTType.Int);
                    n002.addStatement(n009);
                    //VarUse = /
                    AST n010 = new AST(ASTType.VarUse);
                    n002.addStatement(n010);
                    //VarUse = dup
                    AST n011 = new AST(ASTType.VarUse);
                    n002.addStatement(n011);
                    //VarUse = rotl
                    AST n012 = new AST(ASTType.VarUse);
                    n002.addStatement(n012);
                    //VarUse = !=
                    AST n013 = new AST(ASTType.VarUse);
                    n002.addStatement(n013);
                    //RefUse = go
                    AST n014 = new AST(ASTType.RefUse);
                    n002.addStatement(n014);
                    //VarUse = ?
                    AST n015 = new AST(ASTType.VarUse);
                    n002.addStatement(n015);
                //Var = go
                AST n016 = new AST(ASTType.Var);
                n001.addStatement(n016);
                //Int = 1
                AST n017 = new AST(ASTType.Int);
                n001.addStatement(n017);
                //VarUse = go
                AST n018 = new AST(ASTType.Var);
                n001.addStatement(n018);
        // Var = sqrt
        AST n019 =  new AST(ASTType.Var);
        n000.addStatement(n019);
        //Function
        AST n020 = new AST(ASTType.Function);
        n000.addStatement(n020);
            //VarUse = vector
            AST n021 = new AST(ASTType.VarUse);
            n020.addStatement(n021);
            //Int = 3
            AST n022 = new AST(ASTType.Int);
            n020.addStatement(n022);
            //VarUse = x=
            AST n023 = new AST(ASTType.VarUse);
            n020.addStatement(n023);
            //Int = 4
            AST n024 = new AST(ASTType.Int);
            n020.addStatement(n024);
            //VarUse = y=
            AST n025 = new AST(ASTType.VarUse);
            n020.addStatement(n025);
            //VarUse = vector_len
            AST n026 = new AST(ASTType.VarUse);
            n020.addStatement(n026);
            //VarUse = format
            AST n027 = new AST(ASTType.VarUse);
            n020.addStatement(n027);
            //String = 8
            AST n028 = new AST(ASTType.String);
            n020.addStatement(n028);
            //VarUse = swap
            AST n029 = new AST(ASTType.VarUse);
            n020.addStatement(n029);
            //VarUse = ++
            AST n030 = new AST(ASTType.VarUse);
            n020.addStatement(n030);
            //VarUse = putStr
            AST n031 = new AST(ASTType.VarUse);
            n020.addStatement(n031);
            //VarUse = nl
            AST n032 = new AST(ASTType.VarUse);
            n020.addStatement(n032);
        //Var = main
        AST n033 = new AST(ASTType.Var);
        n000.addStatement(n033);
    }

}
