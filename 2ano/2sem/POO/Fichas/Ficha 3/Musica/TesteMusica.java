public class TesteMusica {
    public static void main(String[] args) {
        String[] letra = {
            "Tu estás livre e eu estou livre",
            "E há uma noite para passar",
            "Porque não vamos unidos",
            "Porque não vamos ficar"
        };

        String[] musica = {
            "C D E F G A B",
            "C C G G A A G",
            "F F E E D D C"
        };

        Musica m1 = new Musica(
            "Canção de Engate",
            "António Variações",
            "António Variações",
            "Editora Portuguesa",
            letra,
            musica,
            210
        );

        System.out.println("----- Dados da Música -----");
        System.out.println(m1);

        System.out.println("\nNúmero de linhas do poema: " + m1.qtsLinhasPoema());

        System.out.println("Número total de caracteres: " + m1.numeroCarateres());

        System.out.println("Linha mais longa: " + m1.linhaMaisLonga());

        String[] maisFrequentes = m1.letrasMaisUtilizadas();
        System.out.println("Três letras mais utilizadas:");
        for (String letraFrequente : maisFrequentes) {
            System.out.println("- " + letraFrequente);
        }

        m1.addLetra(2, "Sem perguntas, sem respostas");
        System.out.println("\nLetra após inserção:");
        for (String linha : m1.getLetra()) {
            System.out.println(linha);
        }

        Musica m2 = m1.clone();
        System.out.println("\nClone é igual ao original? " + m1.equals(m2));

        m2.setNome("Versão Remix");
        System.out.println("\nNome do original: " + m1.getNome());
        System.out.println("Nome do clone: " + m2.getNome());
    }
}