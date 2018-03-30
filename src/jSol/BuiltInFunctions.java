package jSol;

import java.util.HashMap;

public enum BuiltInFunctions {
    CALL(0, "call"), QMARK(1, "?"), DOUBLE_QMARK(2, "??"), TRUE(3, "true"), FALSE(4, "false"),
    LOGICAL_AND(5, "&&"), LOGICAL_OR(6, "||"), NOT(7, "not"), EQUALS(8, "="), NOT_EQUALS(9, "!="),
    LT(10, "<"), GT(11, ">"), LTE(12, "<="), GRE(13, ">="), PLUS(14, "+"), MINUS(15, "-"),
    TIMES(16, "*"), DIVIDE(17, "/"), MOD(18, "%"), AND(19, "&"), OR(20, "|"), ARRAY(21, "array"),
    PUSH(22, "push"), POP(23, "pop"), ARRAY_AT(24, "@"), ARRAY_ACCESS(25, "@="),
    ARRAY_IS_EMPTY(26, "=0"), SIZE(27, "size"), RESIZE(28, "resize"), INCREMENT(29, "++"),
    NEW(30, "new"), REF(31, "ref"), INT(32, "Int"), FLOAT(33, "Float"), BOOL(34, "Bool"),
    CHAR(35, "Char"), FORMAT(36, "format"), AS_INT(37, "asInt"), AS_BOOL(38, "asBool"),
    AS_FLOAT(39, "asFloat"), AS_SYMBOL(40, "asSymbol"), FROM_STRING(41, "fromString"),
    GET_CHAR(42, "getChar"), PUT_CHAR(43, "putChar"), GET_STR(44, "getStr"), PUT_STR(45, "putStr"),
    NEW_LINE(46, "nl"), OPEN(47, "open"), CLOSE(48, "close"), EOF(49, "eof"),
    GET_CHAR_F(50, "getCharF"), PUT_CHAR_F(51, "putCharF"), GET_STR_F(52, "getStrF"),
    PUT_STR_F(53, "putStrF"), NL_F(54, "nlF"), GET_ARGS(55, "getArgs"), DUP(56, "dup"),
    DUP_N(57, "dupn"), DROP(58, "drop"), DROP_N(59, "dropn"), ROT_L(60, "rotl"), ROT_LN(61, "rotln"),
    ROT_R(62, "rotr"), ROT_RN(63, "rotrn"), SWAP(64, "swap"), SWAP_N(65, "swapn");

    private int val;
    private String identifier;

    BuiltInFunctions(int val, String identifier) {
        this.val = val;
        this.identifier = identifier;
    }

    private static HashMap<String, BuiltInFunctions> lookup = new HashMap<>() {{
       put(CALL.identifier, CALL);
       put(QMARK.identifier, QMARK);
       put(DOUBLE_QMARK.identifier, DOUBLE_QMARK);
       put(TRUE.identifier, TRUE);
       put(FALSE.identifier, FALSE);
       put(LOGICAL_AND.identifier, LOGICAL_AND);
       put(LOGICAL_OR.identifier, LOGICAL_OR);
       put(NOT.identifier, NOT);
       put(EQUALS.identifier, EQUALS);
       put(NOT_EQUALS.identifier, NOT_EQUALS);
       put(LT.identifier, LT);
       put(GT.identifier, GT);
       put(LTE.identifier, LTE);
       put(GRE.identifier, GRE);
       put(PLUS.identifier, PLUS);
       put(MINUS.identifier, MINUS);
       put(TIMES.identifier, TIMES);
       put(DIVIDE.identifier, DIVIDE);
       put(MOD.identifier, MOD);
       put(AND.identifier, AND);
       put(OR.identifier, OR);
       put(ARRAY.identifier, ARRAY);
       put(PUSH.identifier, PUSH);
       put(POP.identifier, POP);
       put(ARRAY_AT.identifier, ARRAY_AT);
       put(ARRAY_ACCESS.identifier, ARRAY_ACCESS);
       put(ARRAY_IS_EMPTY.identifier, ARRAY_IS_EMPTY);
       put(SIZE.identifier, SIZE);
       put(RESIZE.identifier, RESIZE);
       put(INCREMENT.identifier, INCREMENT);
       put(NEW.identifier, NEW);
       put(REF.identifier, REF);
       put(INT.identifier, INT);
       put(FLOAT.identifier, FLOAT);
       put(BOOL.identifier, BOOL);
       put(CHAR.identifier, CHAR);
       put(FORMAT.identifier, FORMAT);
       put(AS_INT.identifier, AS_INT);
       put(AS_BOOL.identifier, AS_BOOL);
       put(AS_FLOAT.identifier, AS_FLOAT);
       put(AS_SYMBOL.identifier, AS_SYMBOL);
       put(FROM_STRING.identifier, FROM_STRING);
       put(GET_CHAR.identifier, GET_CHAR);
       put(PUT_CHAR.identifier, PUT_CHAR);
       put(GET_STR.identifier, GET_STR);
       put(PUT_STR.identifier, PUT_STR);
       put(NEW_LINE.identifier, NEW_LINE);
       put(OPEN.identifier, OPEN);
       put(CLOSE.identifier, CLOSE);
       put(EOF.identifier, EOF);
       put(GET_CHAR_F.identifier, GET_CHAR_F);
       put(PUT_CHAR_F.identifier, PUT_CHAR_F);
       put(GET_STR_F.identifier, GET_STR_F);
       put(PUT_STR_F.identifier, PUT_STR_F);
       put(NL_F.identifier, NL_F);
       put(GET_ARGS.identifier, GET_ARGS);
       put(DUP.identifier, DUP);
       put(DUP_N.identifier, DUP_N);
       put(DROP.identifier, DROP);
       put(DROP_N.identifier, DROP_N);
       put(ROT_L.identifier, ROT_L);
       put(ROT_LN.identifier, ROT_LN);
       put(ROT_R.identifier, ROT_R);
       put(ROT_RN.identifier, ROT_RN);
       put(SWAP.identifier, SWAP);
       put(SWAP_N.identifier, SWAP_N);
    }};

    public static boolean isBuildInFunction(String maybe) {
        return lookup.containsKey(maybe);
    }

    public static int getBuiltInFunctionValue(String identifier) {
        return isBuildInFunction(identifier) ? lookup.get(identifier).val : -1;
    }
}
