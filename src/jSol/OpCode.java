package jSol;

import java.util.LinkedHashMap;

public enum OpCode {
    //Handling Literal Data
    INT(
            (byte)0x00,
            "Push integer value (32-bit) to data stack",
            "Integer",
            new String []{
                    "OpCode",
                    "Integer to be encoded (32 bit)"
            },
            new String [] {
                    "Op",
                    "Integer"
            },
            new int [] {1,4}
            ),
    FLT(
            (byte)0x01,
            "Push float (32-bit) to data stack",
            "Float",
            new String [] {
                    "OpCode",
                    "Float to be pushed to data stack"
            },
            new String [] {
                    "Op",
                    "Float"
            },
            new int [] {1,4}
            ),
    CHR(
            (byte)0x02,
            "Push ascii character (8-bit) to data stack",
            "Character",
            new String [] {
                    "OpCode",
                    "ASCII character to be pushed to data stack, MUST be encoded with UTF-8"
            },
            new String [] {
                    "Op",
                    "Character"
            },
            new int [] {1,1}
            ),
    STR(
            (byte)0x03,
            "Push string from string block to data stack",
            "String",
            new String [] {
                    "OpCode",
                    "Offset from string block, string block indexes are 2 bytes"
            },
            new String [] {
                    "Op",
                    "Offset"
            },
            new int [] {1,2}
            ),
    SYM(
            (byte)0x04,
            "Push symbol to the data stack",
            "Symbol",
            new String [] {
                    "OpCode",
                    "Offset from string block, string block indexes are 2 bytes"
            },
            new String [] {
                    "Op",
                    "Offset"
            },
            new int [] {1,2}
            ),

    //Manipulating Frames and Calling Functions
    MFR(
            (byte)0x10,
            "Make a new frame whose parent is the currently active frame. The new frame becomes the active frame",
            "Make Frame",
            new String [] {
                    "OpCode",
                    "Number of variable slots in the new frame"
            },
            new String [] {
                    "Op",
                    "Slots"
            },
            new int [] {1,1}
            ),
    STO(
            (byte)0x11,
            "Store top most element from data stack in frame slot",
            "Store",
            new String [] {
                    "OpCode",
                    "Offset to slot in active frame"
            },
            new String [] {
                    "Op",
                    "Offset"
            },
            new int [] {1,1}
            ),
    LOA(
            (byte)0x12,
            "Get content from frame slot and push it to data stack",
            "Load",
            new String [] {
                    "OpCode",
                    "Number of static links to follow to reach appropriate frame",
                    "Frame Slot Offset"
            },
            new String [] {
                    "Op",
                    "Links",
                    "Slot"
            },
            new int [] {1,1,1}
            ),
    LOC(
            (byte)0x13,
            "Get content from frame slot, if content is function closure " +
            "call associated function with frame specified in the closure as the active frame. Otherwise, " +
            "if content isn't a function closure, push content to data stack",
            "LoadOrCall",
            new String [] {
                    "OpCode",
                    "Number of static links to follow to reach appropriate frame",
                    "Frame Slot Offset",
                    "Offset in String block (if content to load is function)"
            },
            new String [] {
                    "Op",
                    "Links",
                    "Offset"
            },
            new int [] {1,1,1,2}
            ),
    LOJ(
            (byte)0x14,
            "Get the content from a slot in some frame. If this content is a function closure," +
            " jump to the function with the frame referenced in the closure as the active frame." +
            " Otherwise, if content isn't a function closure, push the content onto the data stack.",
            "LoadOrJump",
            new String [] {
                    "OpCode",
                    "Number of static links to follow to reach appropriate frame",
                    "Frame Slot Offset",
                    "Offset in String block (if content to load is function)"
            },
            new String [] {
                    "Op",
                    "Links",
                    "Slot",
                    "Offset"
            },
            new int [] {1,1,1,2}
            ),
    RET(
            (byte)0x1F,
            "Return from the current function to its caller",
            "Return",
            new String [] {
                    "OpCode"
            },
            new String [] {
                    "Op"
            },
            new int [] {1}
    ),

    //Creating Function Closures
    MCL(
            (byte)0x20,
            "Creates a function closure consisting of a function and the currently active frame",
            "Make Closure",
            new String []{
                    "Opcode",
                    "Function address in code block"
            },
            new String []{
                    "Op",
                    "Address"
            },
            new int [] {1,4}
    ),

    //Accessing Built-in Functions
    CBI(
            (byte)0x30,
            "Call a built in function",
            "Built-in Call",
            new String [] {
                    "OpCode",
                    "Built-in function ID (ex: BuiltInCall = 56)"
            },
            new String [] {
                    "Op",
                    "ID"
            },
            new int [] {1,1}
    ),
    LBI(
            (byte)0x31,
            "Push a closure identifying a built-in function onto the data stack",
            "Built-in Load",
            new String [] {
                    "OpCode",
                    "Built-in function ID (ex: BuiltInCall = 56)"
            },
            new String [] {
                    "Op",
                    "ID"
            },
            new int [] {1,1}
    ),

    //Manipulating User-Defined Types
    MOB(
            (byte)0x40,
            "Push user defined type onto the data stack",
            "Create Object",
            new String [] {
                    "OpCode",
                    "Type Id of object being created (offset into type block)",
                    "Type size (number of variable slots)"
            },
            new String [] {
                    "Op",
                    "ID",
                    "Size"
            },
            new int [] {1,1,1}
            ),
    RDF(
            (byte)0x41,
            "Pop the topmost element from the data stack. This must be an object of a user-defined type. " +
                    "Push a field of this object onto the stack.",
            "Read Field",
            new String [] {
                    "OpCode",
                    "Type Id of the object the field being read from lives in",
                    "Index of the field to be read from type definition",
                    "Offset in string block to accessor function for target field"
            },
            new String [] {
                    "Op",
                    "ID",
                    "Index",
                    "Offset"
            },
            new int [] {1,1,1,2}
            ),
    WRF(
            (byte)0x42,
            "Expects two elements on the top of the data stack. The topmost element can be of any type. " +
                    "The one below must be of a user-defined type. Pop the top most element from the data stack" +
                    " and store it in a field of the object below it",
            "Write Field",
            new String [] {
                    "OpCode",
                    "Type Id of the object whose field is being written to",
                    "Index of the field in the object to write to",
                    "Offset in string block to accessor function for target field"
            },
            new String [] {
                    "Op",
                    "ID",
                    "Index",
                    "Offset"
            },
            new int []{1,1,1,2}
            );

    private byte hex;
    private String action;
    private String actionShort;
    private int size;
    private String [] paramDetails;
    private int [] paramSize;
    private String[] paramShort;

    OpCode(byte hex, String action, String actionShort, String [] paramDetails, String [] paramShort, int [] paramSize ){
        this.hex = hex;
        this.action = action;
        this.actionShort = actionShort;
        this.paramShort = paramShort;
        this.paramDetails = paramDetails;
        this.paramSize = paramSize;

        //Computer total instruction size
        int sum = 0;
        for (int i: paramSize){
            sum+= i;
        }
        this.size = sum;
    }

    byte hexValue (){
        return hex;
    }

    String action (){
        return action;
    }

    int paramCount(){
        return paramSize.length;
    }

    int size() {return size;}

    int [] paramSizes (){
        return paramSize;
    }
}
