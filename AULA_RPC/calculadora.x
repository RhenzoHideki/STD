struct operandos{
    int a; int b;
};

program CALCULADORA_PROG{
    version CALCULADORA_VERSION{
        int SOMA(operandos) = 1;
        int SUB(operandos) = 2;

    } = 1;
} = 0x20000001;