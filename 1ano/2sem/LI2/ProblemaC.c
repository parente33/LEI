#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <stdlib.h>

double converterParaDecimal(int sinal, const char* expoente, const char* mantissa) {
    int expoente_decimal = strtol(expoente, NULL, 2);
    int excesso = (1 << (strlen(expoente) - 1)) - 1;

    // Verifica se todos os bits do expoente são '0'
    int expoente_zero = 1;
    for (int i = 0; expoente[i] != '\0'; i++) {
        if (expoente[i] != '0') {
            expoente_zero = 0;
            break;
        }
    }

    // Verifica se todos os bits do expoente são '1'
    int expoente_um = 1;
    for (int i = 0; expoente[i] != '\0'; i++) {
        if (expoente[i] != '1') {
            expoente_um = 0;
            break;
        }
    }

    // Verifica se todos os bits da mantissa são '0'
    int mantissa_zero = 1;
    for (int i = 0; mantissa[i] != '\0'; i++) {
        if (mantissa[i] != '0') {
            mantissa_zero = 0;
            break;
        }
    }
    
    if (expoente_zero) {
        // Número em vírgula flutuante desnormalizado
        double mantissa_decimal = strtol(mantissa, NULL, 2) / pow(2, strlen(mantissa));
        double resultado = pow(-1, sinal) * mantissa_decimal * pow(2, 1 - excesso);
        return resultado;
        }
        
        else if (expoente_um && mantissa_zero) {
        if (sinal == 0) {
            return INFINITY;
        } else {
            return -INFINITY;
        }

    } else if (expoente_um && !(mantissa_zero)) {
        return NAN;
    } else {
        // Número em vírgula flutuante normalizado
        double resultado = pow(-1, sinal) * (1.0 + strtol(mantissa, NULL, 2) / pow(2, strlen(mantissa))) * pow(2, expoente_decimal - excesso);
        return resultado;
    }
}

void processar(char **linhas, int num_linhas) {
    for (int i = 0; i < num_linhas; i++) {
        char *linha = linhas[i];
        int E, M;
        char bits[BUFSIZ] = {0};

        sscanf(linha, "%d %d %s", &E, &M, bits);

        char Sinal;
        char Exp[BUFSIZ] = {0};
        char Mant[BUFSIZ] = {0};

        char formato[BUFSIZ];

        sprintf(formato, "%%c%%%ds%%%ds", E, M);
        sscanf(bits, formato, &Sinal, Exp, Mant);

        double resultado_decimal = converterParaDecimal(Sinal - '0', Exp, Mant);
        
        // Necessário porque imprimia apenas "inf", "-inf" e "nan"
        if (isinf(resultado_decimal)) {
            if (resultado_decimal > 0)
                printf("+Infinity\n");
            else
                printf("-Infinity\n");
        } else if (isnan(resultado_decimal)) {
            printf("NaN\n");
        } else {
            printf("%lg\n", resultado_decimal);
        }
    }
}


int main() {
    int num_testes;
    assert(scanf("%d ", &num_testes) == 1);

    char **linhas = malloc(num_testes * sizeof(char *));
    assert(linhas != NULL);

    for (int i = 0; i < num_testes; i++) {
        linhas[i] = malloc(BUFSIZ * sizeof(char));
        assert(linhas[i] != NULL);
        assert(fgets(linhas[i], BUFSIZ, stdin) != NULL);
        assert(linhas[i][strlen(linhas[i]) - 1] == '\n');
        linhas[i][strlen(linhas[i]) - 1] = 0;
    }

    processar(linhas, num_testes);

    for (int i = 0; i < num_testes; i++) {
        free(linhas[i]);
    }
    free(linhas);

    return 0;
}

