import java.util.Set;

public class TesteCasaInteligente {
    public static void main(String[] args) {
        CasaInteligente casa = new CasaInteligente();

        Lampada l1 = new Lampada(0.01);
        Lampada l2 = new Lampada(0.02);
        Lampada l3 = new Lampada(0.015);
        Lampada l4 = new Lampada(0.03);

        casa.addLampada(l1);
        casa.addLampada(l2);
        casa.addLampada(l3);
        casa.addLampada(l4);

        casa.ligaLampadaNormal(0);
        casa.ligaLampadaEco(1);
        casa.ligaLampadaEco(2);

        l1.passarTempo(1000);
        l2.passarTempo(1000);
        l3.passarTempo(1000);
        l4.passarTempo(500);

        System.out.println("Quantidade em modo ECO: " + casa.qtEmEco());

        System.out.println("Consumo total da casa: " + casa.consumoTotal());

        Lampada maisGasta = casa.maisGastadora();
        if (maisGasta != null) {
            System.out.println("Lâmpada mais gastadora: " + maisGasta.totalConsumo() + " unidades");
        }

        Set<Lampada> ecoSet = casa.lampadasEmModoEco();
        System.out.println("Lâmpadas em modo ECO: " + ecoSet.size());

        casa.reset();
        System.out.println("Após reset, consumo da lâmpada 1: " + l1.periodoConsumo());

        Set<Lampada> podium = casa.podiumEconomia();
        System.out.println("Podium das lâmpadas mais económicas:");
        for (Lampada l : podium) {
            System.out.println("  Consumo: " + l.totalConsumo());
        }
    }
}