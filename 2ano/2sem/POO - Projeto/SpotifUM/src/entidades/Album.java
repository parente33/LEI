    package entidades;

    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.List;

    public class Album implements Serializable {

        private String nome; 
        private String artista;
        private List<String> musicas;

        /**
         * Construtor por omissão de Album
         */
        public Album () {
            this.nome = ""; 
            this.artista = "";
            this.musicas = new ArrayList<>();
        }

        /**
         * Construtor de copia de Album
         * @param a Outro Album
         */
        public Album (Album a) {
            this.nome = a.getNome();
            this.artista = a.getArtista();
            this.musicas = a.getMusicas();
        }

        /**
         * Construtor parameterizado de Album
         * @param nome Nome do Album
         * @param artista Nome do artista
         * @param musicas Lista com o nome das musicas
         */
        public Album (String nome,String artista, List<String> musicas) {
            this.nome = nome != null ? nome : ""; 
            this.artista = artista != null ? artista : "";
            this.musicas =musicas != null ? new ArrayList<>(musicas) : new ArrayList<>();
        }

        // Getters

        /**
         * Devolve o nome do album 
         * @return Nome do album
         */
        public String getNome()          {return this.nome;}

        /**
         * Devolve o nome do artista do album
         * @return Nome do artista do album
         */
        public String getArtista()       {return this.artista;}
        /**
         * Devolve uma cópia da lista com o nome das musicas
         * @return Lista com o nome das musicas do album
         */
        public List<String> getMusicas() {return new ArrayList<>(this.musicas);} 

        // Setters 

        /**
         * Atualiza o nome do Album
         * @param nome Novo nome do album 
         */
        public void setNome (String nome )            {if(nome != null) this.nome = nome;}

        /**
         * Atualiza o nome do artista do Album
         * @param artista Novo nome do artista do album 
         */
        public void setArtista (String artista)       {if(artista != null) this.artista = artista;}

        /**
         * Atualiza a lista de musicas do Album
         * @param musicas Nova lista das musicas do album 
         */
        public void setMusicas (List<String> musicas) {if(musicas !=null) this.musicas = new ArrayList<>(musicas);}

        /**
        * Implementação do método de clonagem do Album
        * @return Objeto do tipo Album
        */
        @Override 
        public Album clone () {return new Album(this);} 

         /**
        * Implementaçãodo método de igualdade entre dois albuns
        * @param App Album para ser comparada com o recetor
        * @return Booleano ture ou false
        */        
        @Override 
        public boolean equals (Object o) {
            if (o==this) return true; 
            if (o==null || o.getClass() != this.getClass()) return false; 
            Album a = (Album) o;
            return this.nome.equals(a.getNome()) && this.artista.equals(a.getArtista()) && this.musicas.equals(a.getMusicas());
        }


        /**
         * Método toString
         */
        @Override 
        public String toString() {
            StringBuilder sb = new StringBuilder(); 
            sb.append("Nome: ").append(this.nome).append("\n"); 
            sb.append("Artista: ").append(this.artista).append("\n");
            sb.append("Musicas no Album: ").append(this.musicas).append("\n");
            return sb.toString();

        }
    }