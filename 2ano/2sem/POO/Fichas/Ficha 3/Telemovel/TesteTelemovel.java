public class TesteTelemovel {
    public static void main(String[] args) {
        Telemovel t = new Telemovel("Samsung", "Galaxy S25", 1080, 2400, 10, 5000, 2000, 1500);

        t.instalaApp("WhatsApp", 300);
        t.instalaApp("Instagram", 400);
        t.instalaApp("Spotify", 500);

        t.recebeMsg("Olá!");
        t.recebeMsg("Esta é a maior mensagem de teste enviada para o telemóvel.");
        t.recebeMsg("Boa tarde!");

        System.out.println("Maior mensagem: " + t.maiorMsg());

        System.out.println("Há espaço para 1000 bytes? " + t.existeEspaco(1000));
        System.out.println("Espaço ocupado: " + t.getEspacoOcupado());

        System.out.println("Tamanho médio das apps: " + t.tamMedioApps());

        t.removeApp("Instagram", 400);
        System.out.println("Apps após remover Instagram: " + t.getNomesApps());

        System.out.println(t);

        Telemovel clone = t.clone();
        System.out.println("Clone: " + clone);

        System.out.println("É igual ao original? " + t.equals(clone));
    }
}