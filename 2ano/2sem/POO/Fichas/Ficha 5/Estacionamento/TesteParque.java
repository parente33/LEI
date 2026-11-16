package Estacionamento;

import java.util.Map;

public class TesteParque {
    public static void main(String[] args) {
        Parque parque = new Parque("Parque Central");

        Lugar lugar1 = new Lugar("ABC123", "João", 120, true);
        Lugar lugar2 = new Lugar("DEF456", "Maria", 60, false);
        Lugar lugar3 = new Lugar("GHI789", "José", 200, true);

        parque.registaLugar(lugar1);
        parque.registaLugar(lugar2);
        parque.registaLugar(lugar3);

        System.out.println("Matrículas ocupadas: " + parque.matriculasOcupadas());
        System.out.println("Total de minutos: " + parque.totalMinutos());
        System.out.println("Total de minutos com iterador interno: " + parque.totalMinutosComIteradorInterno());

        System.out.println("Existe lugar para ABC123? " + parque.existeLugar("ABC123"));
        System.out.println("Existe lugar para XYZ000? " + parque.existeLugar("XYZ000"));

        parque.alteraMinutos("ABC123", 180);
        System.out.println("Minutos de ABC123 após alteração: " + parque.getLugar("ABC123").getMinutos());

        parque.removeLugar("DEF456");
        System.out.println("Matrículas ocupadas após remoção: " + parque.matriculasOcupadas());

        System.out.println("Lugares permanentes com mais de 100 minutos: " + parque.lugaresComMaisTempoPermanente(100));

        Map<String, Lugar> copia = parque.copiaLugares();
        System.out.println("Cópia dos lugares: " + copia);
    }
}