#include <stdio.h>
#include <wchar.h>
#include <locale.h>

void convertToYijing(unsigned long long int num) {

    setlocale(LC_CTYPE, "C.UTF-8");
    
    wchar_t result[64];
    int i = 0;

    if (num == 0) {
        wprintf(L"䷀");
        return;
    }
    
    while (num > 0) {
        unsigned remainder = num % 64;
        
        int char_utf = remainder + 19904;
        wchar_t star = char_utf;
        result[i++] = star;
        
        num = num / 64;
    }
    
    for (int j = i - 1; j > 0; j--) {
        wprintf(L"%lc ", result[j]);
    }
        wprintf(L"%lc", result[0]);
}

int main() {
    unsigned long long int num;
    

    if (scanf("%llu", &num) != 1) {
        printf("Entrada inválida. Por favor, insira um número inteiro válido.\n");
        return 1;
    }

    convertToYijing(num);
    wprintf(L"\n");

    return 0;
}