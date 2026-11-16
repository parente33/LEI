import java.util.Arrays;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Ficha2 {
    private LocalDate[] datas;
    private int tamanho;

    public Ficha2() {
        this.datas = new LocalDate[10];
        this.tamanho = 0;
    }

    // Ex1 a)
    public int minArray(int[] numbers) {
        if (numbers.length <= 0) return -1;
        int min = numbers[0];

        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }
        return min;
    }

    // Ex1 b)
    public int[] inBetween(int[] numb, int a, int b) {
        if (numb.length <= 0 || a > b || a < 0 || b > numb.length) return null;

        int size = b - a + 1;
        int[] sub_array = new int[size];

        System.arraycopy(numb, a, sub_array, 0, size);
        return sub_array;
    }

    // Ex1 c)
    public int[] comuns(int[] array1, int[] array2) {
        int[] common = new int[Math.min(array1.length, array2.length)];
        int ind = 0;
        boolean inCommon = false;

        for (int i = 0; i < array1.length; i++) {
            for (int j = 0; j < array2.length; j++) {
                if (array1[i] == array2[j]) {
                    for (int k = 0; k < common.length; k++) {
                        if (common[k] == array1[i]) {
                            inCommon = true;
                            break;
                    }
                    }
                    if (!inCommon) {
                        common[ind++] = array1[i];
                    }
                }
                inCommon = false;
            }
        }
        return common;
    }

    // Ex2 a)
    public void insereData (LocalDate data) {
        if (tamanho == datas.length) {
            datas = Arrays.copyOf(datas,datas.length*2);
        }
        datas[tamanho++] = data;
    }

    // Ex2 b)
    public LocalDate dataMaisProxima(LocalDate data) {
        if (tamanho == 0) return null;

        LocalDate maisProx = null;
        int menorDiff = Integer.MAX_VALUE;

        for (int i = 0; i < tamanho; i++) {
            if (datas[i] != null) {
                int diff = diferencaDias(data, datas[i]);
                if (diff < menorDiff) {
                    menorDiff = diff;
                    maisProx = datas[i];
                }
            }
        }
        return maisProx;
    }

    private int diferencaDias(LocalDate data1, LocalDate data2) {
        int diferenca = 0;
        LocalDate menor = data1.isBefore(data2) ? data1 : data2;
        LocalDate maior = data1.isBefore(data2) ? data2 : data1;

        // Soma os dias, mês a mês, ano a ano
        while (!menor.equals(maior)) {
            menor = menor.plusDays(1);
            diferenca++;
        }
        return diferenca;
    }

    // Ex2 c)
    @Override
    public String toString() {
        return "Datas armazenadas: " + datas.toString();
    }
}