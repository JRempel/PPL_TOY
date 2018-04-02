package jSol;

import java.util.List;
import java.util.Map;

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
        root.addType("vector");
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
        n002b.setValue("x");
        n001.addStatement(n002b);
        //Function
        AST n002 = new AST(ASTType.Function);
        n001.addStatement(n002);
        //VarUse = dup
        AST n003 = new AST(ASTType.VarUse);
        n003.setValue("dup");
        n002.addStatement(n003);
        //VarUse = dup
        AST n004 = new AST(ASTType.VarUse);
        n004.setValue("dup");
        n002.addStatement(n004);
        //VarUse = x
        AST n005 = new AST(ASTType.VarUse);
        n005.setValue("x");
        n002.addStatement(n005);
        //VarUse = swap
        AST n006 = new AST(ASTType.VarUse);
        n006.setValue("swap");
        n002.addStatement(n006);
        //VarUse = /
        AST n007 = new AST(ASTType.VarUse);
        n007.setValue("/");
        n002.addStatement(n007);
        //VarUse = +
        AST n008 = new AST(ASTType.VarUse);
        n008.setValue("+");
        n002.addStatement(n008);
        //Int = 2
        AST n009 = new AST(ASTType.Int);
        n009.setValue("2");
        n002.addStatement(n009);
        //VarUse = /
        AST n010 = new AST(ASTType.VarUse);
        n010.setValue("/");
        n002.addStatement(n010);
        //VarUse = dup
        AST n011 = new AST(ASTType.VarUse);
        n011.setValue("dup");
        n002.addStatement(n011);
        //VarUse = rotl
        AST n012 = new AST(ASTType.VarUse);
        n012.setValue("rotl");
        n002.addStatement(n012);
        //VarUse = !=
        AST n013 = new AST(ASTType.VarUse);
        n013.setValue("!=");
        n002.addStatement(n013);
        //RefUse = go
        AST n014 = new AST(ASTType.RefUse);
        n014.setValue("go");
        n002.addStatement(n014);
        //VarUse = ?
        AST n015 = new AST(ASTType.VarUse);
        n015.setValue("?");
        n002.addStatement(n015);
        //Var = go
        AST n016 = new AST(ASTType.Var);
        n016.setValue("go");
        n001.addStatement(n016);
        //Int = 1
        AST n017 = new AST(ASTType.Int);
        n017.setValue("1");
        n001.addStatement(n017);
        //VarUse = go
        AST n018 = new AST(ASTType.Var);
        n018.setValue("go");
        n001.addStatement(n018);
        // Var = sqrt
        AST n019 = new AST(ASTType.Var);
        n019.setValue("sqrt");
        n000.addStatement(n019);
        //Function
        AST n020 = new AST(ASTType.Function);
        n000.addStatement(n020);
        //VarUse = vector
        AST n021 = new AST(ASTType.VarUse);
        n021.setValue("vector");
        n020.addStatement(n021);
        //Int = 3
        AST n022 = new AST(ASTType.Int);
        n022.setValue("3");
        n020.addStatement(n022);
        //VarUse = x=
        AST n023 = new AST(ASTType.VarUse);
        n023.setValue("x=");
        n020.addStatement(n023);
        //Int = 4
        AST n024 = new AST(ASTType.Int);
        n024.setValue("4");
        n020.addStatement(n024);
        //VarUse = y=
        AST n025 = new AST(ASTType.VarUse);
        n025.setValue("y=");
        n020.addStatement(n025);
        //VarUse = vector_len
        AST n026 = new AST(ASTType.VarUse);
        n026.setValue("vector_len");
        n020.addStatement(n026);
        //VarUse = format
        AST n027 = new AST(ASTType.VarUse);
        n027.setValue("format");
        n020.addStatement(n027);
        //String = 8
        AST n028 = new AST(ASTType.String);
        n028.setValue("8");
        n020.addStatement(n028);
        //VarUse = swap
        AST n029 = new AST(ASTType.VarUse);
        n029.setValue("swap");
        n020.addStatement(n029);
        //VarUse = ++
        AST n030 = new AST(ASTType.VarUse);
        n030.setValue("++");
        n020.addStatement(n030);
        //VarUse = putStr
        AST n031 = new AST(ASTType.VarUse);
        n031.setValue("putStr");
        n020.addStatement(n031);
        //VarUse = nl
        AST n032 = new AST(ASTType.VarUse);
        n032.setValue("nl");
        n020.addStatement(n032);
        //Var = main
        AST n033 = new AST(ASTType.Var);
        n033.setValue("main");
        n000.addStatement(n033);

        //root.print();
        toIntermediateAST(root.getRoot());

        root.print();
    }

    //Destructive conversion to intermediate AST
    private static void toIntermediateAST(AST root) {

        //If node has child statements
        if (root.getStatements().size() > 0) {
            //For each child
            for (AST node : root.getStatements()) {
                toIntermediateAST(node);
            }
        } else {

            if (root.getAstType().equals(ASTType.Int) || root.getAstType().equals(ASTType.String)){
                return;
            }

            String targetValue = root.getValue();
            boolean found = false;
            AST cursor = root;

            while (!found && cursor != null) {

                //Look for the variable in the current symbol table
                for (Map.Entry<String, int[]> entry : cursor.getSymbols()) {
                    System.out.println(root.getAstType() + " looking for " + root.getValue());
                    System.out.println("is it " + entry.getKey() + "?");
                    //If the variable is found in the symbol table
                    if (entry.getKey().equals(targetValue)) {
                        //Set root type to LoadOrCall and replace value with new value from symbol table
                        if (root.getAstType() == ASTType.VarUse) {
                            root.setAstType(ASTType.LoadOrCall);
                        }
                        if (root.getAstType() == ASTType.RefUse) {
                            root.setAstType(ASTType.ObjectRead);
                        }

                            String newValue = "";
                            for (int i : entry.getValue()) {
                                newValue += Integer.toString(i) + ",";
                            }
                            newValue = newValue.substring(0, newValue.length() - 1);
                            root.setValue(newValue);
                            found = true;
                            break;
                        }
                    }

                    cursor = cursor.getParent();

                }

                if (!found && cursor == null) {
                    int builtInFunctionValue = BuiltInFunctions.getBuiltInFunctionValue(root.getValue());
                    if (builtInFunctionValue != -1) {
                        root.setAstType(ASTType.BuiltInCall);
                        root.setValue(Integer.toString(builtInFunctionValue));
                    } else {
                        throw new RuntimeException("Could not resolve symbol (" + root.getValue() + ")");
                    }
                }

            }
        }

    }

