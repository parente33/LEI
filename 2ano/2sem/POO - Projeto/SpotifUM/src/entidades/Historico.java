package entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Historico implements Serializable {
    private String nomeMusica;
    private LocalDateTime dataHora;

    /**
     * Construtor por omissão de Historico
     */
    public Historico() {
        this.nomeMusica = "";
        this.dataHora = LocalDateTime.now();
    }

     /**
     * Construtor de cópia de Historico
     * @param h Outro objeto Historico
     */
    public Historico(Historico h) {
        this.nomeMusica = h.getMusica();
        this.dataHora = h.getDataHora();
    }
     /**
     * Construtor para reprodução atual
     * @param nomeMusica Nome da música reproduzida
     */
    public Historico(String nomeMusica) {
        this.nomeMusica = nomeMusica;
        this.dataHora = LocalDateTime.now();
    }


    // Construtor para reprodução com data/hora fornecida (ex: via CSV)

     /**
     * Construtor para reprodução com data/hora fornecida (ex: via CSV)
     * @param nomeMusica Nome da música reproduzida
     * @param dataHora Data e hora da reprodução
     */
    public Historico(String nomeMusica,LocalDateTime dataHora) {
        this.nomeMusica = nomeMusica;
        this.dataHora = dataHora;
    }

    /**
     * Devolve o nome da música reproduzida
     * @return Nome da música
     */
    public String getMusica() {return this.nomeMusica;}

    /**
     * Devolve a data e hora da reprodução
     * @return Data e hora da reprodução
     */
    public LocalDateTime getDataHora() {return this.dataHora;}

    /**
     * Atualiza o nome da música reproduzida
     * @param musica Novo nome da música
     */
    public void setMusica(String musica) {if(musica != null )this.nomeMusica = musica;}

    /**
     * Atualiza a data e hora da reprodução
     * @param dataHora Nova data e hora
     */
    public void setDataHora(LocalDateTime dataHora) {if(dataHora != null) this.dataHora = dataHora;}

    /**
     * Representação textual do histórico de reprodução
     * @return String com os dados formatados
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb =new StringBuilder();
        sb.append("Musica: ").append(this.nomeMusica).append(";");
        sb.append("Data e Hora: ").append(this.dataHora.format(formatter) + "\n");
        return sb.toString();
    }

    /**
     * Implementação do método de clonagem do objeto Historico
     * @return Nova instância com os mesmos dados
     */
    @Override
    public Historico clone() {
        return new Historico(this);
    }


    /**
     * Verifica se dois históricos são iguais
     * @param o Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o== null || o.getClass() != this.getClass() ) return false;
        Historico outro = (Historico) o;
        return this.nomeMusica.equals(outro.getMusica()) &&
               this.dataHora.equals(outro.getDataHora());
    }
}
