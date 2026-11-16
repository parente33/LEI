import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotelComFilaEspera extends Hotel {
    private List<Registo> reservasEmEspera;

    public HotelComFilaEspera(Iterator<Quarto> quartos) {
        super(quartos);
        this.reservasEmEspera = new ArrayList<>();
    }

    public void adicionaReserva(LocalDate entrada, LocalDate saida) {
        try {
            List<Quarto> livres = this.quartosLivres(entrada, saida);
            if (!livres.isEmpty()) {
                Quarto q = livres.get(0);
                this.adicionaRegisto(entrada, saida, q.getNumeroQuarto());
            }
        } catch (SemDisponibilidadeException e) {
            reservasEmEspera.add(new Registo(entrada, saida, null));
        }
    }

    public void ocupaQuartoComReservaEmEspera(LocalDate entrada, LocalDate saida) {
        if (reservasEmEspera.isEmpty()) return;

        Registo reserva = reservasEmEspera.get(0);
        
        try {
            List<Quarto> livres = this.quartosLivres(reserva.getDataInicio(), reserva.getDataFim());
            if (!livres.isEmpty()) {
                Quarto q = livres.get(0);
                this.adicionaRegisto(reserva.getDataInicio(), reserva.getDataFim(), q.getNumeroQuarto());
                reservasEmEspera.remove(0);
            }
        } catch (SemDisponibilidadeException e) {
            // nenhum quarto disponível, ignora
        }
    }
}