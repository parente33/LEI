package app;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.List;

import entidades.PremiumBase;
import entidades.PremiumTop;
import entidades.Utilizador;
import entidades.Utils;
import entidades.Free;
import entidades.Plano;
import loader.LoaderManager;

public class View {
    private App app; 

    /**
	 * Construtor por omissão
	 */
    public View() {
        this.app = new App();
    }

    /**
	 * Construtor parameterizado 
	 * @param app Instância da aplicação a utilizar.
	 */
    public View(App app) {
      
        this.app = app;
    } 
     /**
     * Construtror de cópia 
     * @param view Outro Objeto View 
     */
    public View(View view) {
        this.app = view.getApp();
    }

    /**
	 * Retorna uma cópia da instância da aplicação.
	 * @return Cópia da instância de App.
	 */
    public App getApp()  {return this.app.clone();}

    /**
	 * Inicia o menu principal da aplicação, permitindo carregar dados de ficheiros CSV ou binários.
	 */
    public void startMenu() {
        String titulo = ">>>>> Bem vindo ao SpotifUM! <<<<<";
        Menu menuStart = new Menu(titulo);
        
        menuStart.adicionarOpcao("Carregar dados de texto (.csv)", () -> {
            System.out.println("A carregar ficheiros csv...");
            LoaderManager.loadCSV(app,"files");
            iniciarSessaoMenu();
        });

        menuStart.adicionarOpcao("Carregar dados binários (.bin)", () -> {
            System.out.println("A carregar ficheiro binário...");
            App estadoCarregado = app.carregaEstado("./../appState.bin");
            if (estadoCarregado != null) {
                this.app = estadoCarregado;
                System.out.println("Dados carregados com sucesso!");
                iniciarSessaoMenu();
            } else {
                System.out.println("Erro ao carregar os dados binários.");
                startMenu();
            }
            
        });

        menuStart.executar();
    }   


     /**
     * Inicia o menu de autenticação, permitindo iniciar sessão como utilizador ou administrador
     * ou ainda registar um novo utilizador.
     */
    public void iniciarSessaoMenu() {
        Menu.limparConsola();
        String titulo = "Dados carregados! Como deseja prosseguir?";
        Menu menuSessao = new Menu(titulo);

        menuSessao.adicionarOpcao("Iniciar sessão como utilizador", () -> {
            System.out.println("Introduza o seu nome: ");
            String nome = Menu.getScanner().nextLine();
            if (app.iniciaSessao(nome)) {
                System.out.println("Bem vindo, " + nome + "!");
                userMenu();
            }
            else {
                System.out.println("Utilizador não foi encontrado.");
                iniciarSessaoMenu();
            }
            
        }); 

        menuSessao.adicionarOpcao("Iniciar sessão como administrador", () -> {
            System.out.println("Introduza o seu nome: ");
            String nome = Menu.getScanner().nextLine();
            if (app.iniciaSessao(nome)) {
                System.out.println("Acesso administrativo concedido. Seja bem vindo, " + nome + "!");
                adminMenu();
            }
            else {
                System.out.println("Credenciais inválidas.");
                iniciarSessaoMenu();
            }
        
        }); 

        menuSessao.adicionarOpcao("Registar utilizador", () -> {
            registrarUtilizador();
        }); 

        menuSessao.executar();
    }


    // Funcionalidades para users ficam todas no mesmo menu, caso um user tente fazer algo a que não tem acesso, dá erro

     /**
     * Apresenta o menu de funcionalidades disponíveis para o utilizador autenticado.
     * As opções dependem do tipo de plano (Free, Premium, PremiumTop).
     * Permite gerir biblioteca, criar playlists, reproduzir conteúdos, consultar perfil, mudar plano, etc.
     */
    public void userMenu() {
        Menu.limparConsola();
        String titulo = "Sessão Iniciada como Utilizador";
        Menu menuUser = new Menu(titulo);

        // Gestão de conteúdo
        menuUser.adicionarOpcao("Adicionar conteúdo à minha biblioteca (Exclusivo Premium)", () -> {
            if (!app.checkPermissao()) {
                System.out.println("Utilizadores Free não podem adicionar conteúdos");
                userMenu();
            }
            else{
            System.out.println("\n=== Escolha o tipo de conteúdo que quer adicionar à sua biblioteca ===");
            System.out.println("1. Adicionar álbum");
            System.out.println("2. Adicionar Playlist");
            System.out.println("Opção:");

            int escolha;
            try {
                escolha = Integer.parseInt(Menu.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                userMenu();
                return;
            }

            switch (escolha) {
                case 1:
                    System.out.println("Álbuns: " + app.albunsDisponiveis());
                    System.out.println("Digite o nome do álbum: ");
                    String album = Menu.getScanner().nextLine();
                    if (app.adicionarAlbumBiblioteca(album)) {
                        System.out.println("Álbum adicionado com sucesso!\n");
                    } else {
                        System.out.println("Álbum não existe ou já está presente na sua biblioteca.");
                    }
                    userMenu();
                    break;
                case 2:
                    System.out.println("Playlists Públicas: " + app.playlistsDisponiveis());
                    System.out.println("Digite o nome da playlist: ");
                    String playlist = Menu.getScanner().nextLine();
                    if (app.adicionarPlayBiblioteca(playlist)) {
                        System.out.println("Adicionado com sucesso\n");
                    } else {
                        System.out.println("Playlist não existe, é privada, ou já existe na sua biblioteca.");
                    }
                    userMenu();
                    break;
                default:
                    System.out.println("Opção inválida.");
                    userMenu();
            }
            }
        });

        menuUser.adicionarOpcao("Remover conteúdo da minha biblioteca", () -> {
            System.out.println("\n=== Escolha o tipo de conteúdo que quer remover da sua biblioteca ===");
            System.out.println("1. Remover álbum");
            System.out.println("2. Remover Playlist");
            System.out.println("Opção:");

            int escolha;
            try {
                escolha = Integer.parseInt(Menu.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                userMenu();
                return;
            }

            switch (escolha) {
                case 1:
                    System.out.println("Digite o nome do álbum: ");
                    String album = Menu.getScanner().nextLine();
                    if (app.removerAlbumBiblioteca(album)) {
                        System.out.println("Álbum removido com sucesso!\n");
                    } else {
                        System.out.println("Álbum não existe na sua biblioteca.\n");
                    }
                    userMenu();
                    break;
                case 2:
                    System.out.println("Digite o nome da playlist: ");
                    String playlist = Menu.getScanner().nextLine();
                    if (app.removerPlayBiblioteca(playlist)) {
                        System.out.println("Removida com sucesso!\n");
                    } else {
                        System.out.println("Playlist não existe na sua biblioteca.\n");
                    }
                    userMenu();
                    break;
                default:
                    System.out.println("Opção inválida.");
                    userMenu();
            }
        });

        menuUser.adicionarOpcao("Criar nova playlist (Exclusivo Premium)", () -> {
            System.out.println("Digite o nome da playlist: ");
            String nome = Menu.getScanner().nextLine();
            System.out.println("Músicas Disponíveis: " + app.musicas());
            System.out.println("Digite as músicas que pretende adicionar separadas por virgula: ");
            String musicas = Menu.getScanner().nextLine();
            boolean isPublica = selecionarPrivacidade(Menu.getScanner()).equals("Publica");
            if (app.checkMusicas(musicas)!= null) {
                System.out.println("Música " + app.checkMusicas(musicas)+" inválida");
                userMenu();
            } else if (!app.criarPlaylist(nome,musicas,isPublica)) {
                System.out.println("Utilizadores com Plano Free não podem criar playlists ou nome de playlist já existe.");
                userMenu();
            } else {
                System.out.println("Playlist " + nome + " adicionada com sucesso!\n");
                userMenu();
            }
        });

        // Reprodução
        menuUser.adicionarOpcao("Reproduzir conteúdo", () -> {
            Utilizador user = app.getUtilizador();
            System.out.println("\n=== Escolha o tipo de conteúdo que quer reproduzir ===");
            System.out.println("1. Playlist da minha biblioteca (de forma sequencial || Exclusivo Premium)");
            System.out.println("2. Playlist da minha biblioteca (de forma aleatória || Exclusivo Premium)");
            System.out.println("3. Álbum da minha biblioteca (Exclusivo Premium)");
            System.out.println("4. Música aleatória");
            System.out.println("5. Música à escolha (Exclusivo Premium)");
            System.out.println("Opção: ");

            int escolha;
            try {
                escolha = Integer.parseInt(Menu.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                userMenu();
                return;
            }
            switch (escolha) {
                case 1:
                    if (!app.checkPermissao()) {System.out.println("Utilizador Free não pode reproduzir playlists");userMenu();break;}
                    reproduzirPlaylistBiblioteca(user);
                    userMenu();
                    break;
                case 2:
                    if (!app.checkPermissao()) {System.out.println("Utilizador Free não pode reproduzir playlists");userMenu();break;}
                    reproduzirPlaylistBibliotecaAleat(user);
                    userMenu();
                    break;
                case 3:
                    if (!app.checkPermissao()) {System.out.println("Utilizador Free não pode reproduzir albuns");userMenu();break;}
                    reproduzirAlbumBiblioteca(user);
                    userMenu();
                    break;
                case 4:
                    String musica = app.escolheRandomMusic();
                    System.out.println("A reproduzir " + musica + ":");
                    System.out.print(app.reproduzirMusica(musica));
                    userMenu();
                    break;
                case 5:
                    System.out.println("Músicas Disponiveis: " + app.musicas());
                    System.out.println("Digite a música que quer ouvir: ");
                    String music = Menu.getScanner().nextLine();
                    String letra = app.reproduzirMusicaEscolhida(music);
                    if (letra == null) { 
                        System.out.println("Utilizadores com plano Free não podem selecionar música para reproduzir ou música é inválida");
                    } else {
                        System.out.println("A reproduzir " + music + ":");
                        System.out.println(letra);
                    }
                    userMenu();
                    break;
                default:
                    System.out.println("Opção inválida.");
                    userMenu();;
            }
        });

        // Playlists
        menuUser.adicionarOpcao("Gerar playlists (Exclusivo PremiumTop)", () -> {
            Utilizador user = app.getUtilizador();
            if (!(user.getPlano() instanceof PremiumTop)) {
                System.out.println("\nFuncionalidade exclusiva para utilizadores PremiumTop");
                userMenu();
                return;
            }
            System.out.println("\n=== Escolha o tipo de playlist que quer gerar ===");
            System.out.println("1. Gerar playlist dos meus favoritos");
            System.out.println("2. Gerar playlist dos meus favoritos com duração específica");
            System.out.println("3. Gerar playlist dos meus favoritos, exclusivamente com músicas explícitas");
            System.out.println("Opção: ");

            int escolha;
            try {
                escolha = Integer.parseInt(Menu.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                userMenu();
                return;
            }

            switch (escolha) {
                case 1:
                    System.out.println("Qual o nome da playlist? ");
                    String nome = Menu.getScanner().nextLine();
                    if (!app.gerarPlayMusicasPreferidas(nome)) {
                        System.out.println("Nome de playlist inválido");
                        userMenu();
                        break;}
                    System.out.println("Playlist adiciona com sucesso na sua biblioteca");
                    userMenu();
                    break;
                case 2:
                    System.out.println("Qual o nome da playlist? ");
                    String name = Menu.getScanner().nextLine();
                    System.out.println("E qual a duração em minutos ?");
                    int minutos = Integer.parseInt(Menu.getScanner().nextLine());
                    if (!app.gerarPlayMusicasPreferidasComTempo(name, minutos*60)){
                        System.out.println("Nome de playlist inválido ou minutos inválidos");
                        userMenu();
                        break;}
                    System.out.println("Playlist adiciona com sucesso na sua biblioteca");
                    userMenu();
                    break;
                case 3:
                    System.out.println("Qual o nome da playlist? ");
                    String play = Menu.getScanner().nextLine();
                    if (!app.gerarPlayMusicasPreferidasExplicitas(play)){
                        System.out.println("Nome de playlist inválido ou não existem músicas explícitas nos seus gostos");
                        userMenu();
                        break;}
                    System.out.println("Playlist adiciona com sucesso na sua biblioteca");
                    userMenu();
                    break;
                default:
                    System.out.println("Opção inválida.");
                    userMenu();;
            }
        });

        // Perfil
        menuUser.adicionarOpcao("A minha informação", () -> {
            System.out.println(app.getUtilizador().toString());
            userMenu();
        }); 
        menuUser.adicionarOpcao("Mudar o meu plano", () -> {
            Plano p =  selecionarPlano(Menu.getScanner());
            System.out.println(app.upgradePlano(p));
            userMenu();
        }); 
        // Sistema
        menuUser.adicionarOpcao("Salvar estado do programa", () -> {
            app.guardaEstado();
            System.out.println("Dados guardados com sucesso!");
            userMenu();
        });

        menuUser.adicionarOpcao("Terminar sessão", () -> {
            //app.setUtilizador(null);
            System.out.println("\nSesão terminada com sucesso!");
            iniciarSessaoMenu();
        });

        menuUser.executar();
    }   
     /**
     * Apresenta o menu de funcionalidades disponíveis para o administrador.
     * Permite consultar estatísticas, listar informação do sistema e guardar estado.
     */
    public void adminMenu() {
        Menu.limparConsola();
        String titulo = "Sessão Iniciada como Administrador";
        Menu menuAdmin = new Menu(titulo);

        // Estatísticas
        menuAdmin.adicionarOpcao("Consultar estatísticas", () -> {
            queryMenu();
        }); 

        // Gestão de Conteúdo 
        menuAdmin.adicionarOpcao("Listar informação", () -> {
            System.out.println("\n=== Escolha o tipo de informação que quer visualizar ===");
            System.out.println("1. Listar todas as músicas");
            System.out.println("2. Listar todos os álbuns");
            System.out.println("3. Listar todos os artistas");
            System.out.println("4. Listar todas as playlists");
            System.out.println("5. Listar todos os utilizadores");
            System.out.println("6. Listar informação sobre utilizador específico");
            System.out.println("Opção: ");

            int escolha;
            try {
                escolha = Integer.parseInt(Menu.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                userMenu();
                return;
            }

            switch (escolha) {
                case 1:
                    System.out.println(app.getMapMusicas());
                    adminMenu();
                    break;
                case 2:
                    System.out.println(app.getMapAlbuns());
                    adminMenu();
                    break;
                case 3:
                    System.out.println(app.getMapArtistas());
                    adminMenu();
                    break;
                case 4:
                    System.out.println(app.getMapPrivatePlaylists());
                    System.out.println(app.getMapPublicPlaylists());
                    adminMenu();
                    break;
                case 5:
                    System.out.println(app.getMapUtilizadores());
                    adminMenu();
                    break;
                case 6:
                    System.out.println("Introduza o nome do utilizador: ");
                    String nome = Menu.getScanner().nextLine();
                    if (app.existeUtilizador(nome)) {
                        System.out.println(app.getUtilizador(nome).toString());
                    } else {
                        System.out.println("Pedimos desculpa, mas parece que essa pessoa não existe.");
                    }
                    adminMenu();
                    break;
                default:
                    System.out.println("Opção inválida.");
                    adminMenu();
            }
        });

        // Sistema
        menuAdmin.adicionarOpcao("Salvar estado do programa", () -> {
            app.guardaEstado();
            System.out.println("Dados guardados com sucesso!");
            adminMenu();
        }); 

        menuAdmin.adicionarOpcao("Terminar sessão", () -> {
            //app.setUtilizador(null);
            System.out.println("\nSesão terminada com sucesso!");
            iniciarSessaoMenu();
        });

        menuAdmin.executar();
    }   

     /**
     * Apresenta o menu de estatísticas e queries disponíveis para o administrador.
     * Permite consultar a música mais tocada, artista mais escutado, utilizadores mais ativos, etc.
     */
    public void queryMenu() {
        Menu.limparConsola();
        String titulo = "Estatísticas e Consultas";
        Menu menuQueries = new Menu(titulo);    

        // Músicas
        menuQueries.adicionarOpcao("Música mais reproduzida", () -> {
            System.out.println(app.solveQuery(1,null,null));
            queryMenu();
        }); 

        menuQueries.adicionarOpcao("Artista mais escutado", () -> {
            System.out.println(app.solveQuery(2,null,null));
            queryMenu();
        }); 

        menuQueries.adicionarOpcao("Género musical mais popular", () -> {
            System.out.println(app.solveQuery(5, null, null));
            queryMenu();
        }); 

        // Utilizadores
        menuQueries.adicionarOpcao("Utilizador mais ativo (num período)", () -> {
            System.out.println("Digite o periodo de inicio e o periodo de fim : (Formato dd/MM/aaaa)");
            String inicio = Menu.getScanner().nextLine();
            String fim = Menu.getScanner().nextLine();
            LocalDateTime data_inicio = Utils.stringtoDateTime(inicio);
            LocalDateTime data_fim = Utils.stringtoDateTime(fim);
            if (data_inicio!=null && data_fim != null){
                System.out.println(app.solveQuery(3,Utils.stringtoDateTime(inicio), Utils.stringtoDateTime(fim)));
                queryMenu();
            }
            else {
                System.out.println("Data inválida, insira no formato dd/MM/aaaa");
                queryMenu();
            }
            
        }); 

        menuQueries.adicionarOpcao("Utilizador mais ativo (desde sempre)", () -> {
            System.out.println(app.solveQuery(3, null, null));
            queryMenu();
        }); 

        menuQueries.adicionarOpcao("Utilizador com mais pontos", () -> {
            System.out.println(app.solveQuery(4, null, null)); 
            queryMenu();
                       
        }); 

        // Playlists
        menuQueries.adicionarOpcao("Número de playlists públicas (total)", () -> {
            System.out.println(app.solveQuery(6, null, null));
            queryMenu();
        }); 

        menuQueries.adicionarOpcao("Utilizador com mais playlists", () -> {
            System.out.println(app.solveQuery(7, null, null));
            queryMenu();
        }); 

        // Sistema
        menuQueries.adicionarOpcao("Voltar atrás", () -> {
            adminMenu();
        });

        menuQueries.executar();
    }

    /**
    * Mostra um menu de reprodução para uma música específica,
    * permitindo ao utilizador ouvir ou avançar a música.
    *
    * @param m Nome da música a ser reproduzida
    */
    public void menuReproduz(String m) {
        String titulo = "Próxima Musica : "+m;
        Menu reproduz = new Menu(titulo); 
        reproduz.adicionarOpcao("Ouvir Música",() -> {
            System.out.println(this.app.reproduzirMusica(m));
        });
        reproduz.adicionarOpcao("Avançar Música",() -> {
            System.out.println("Avançando Música...");
        });
        reproduz.executar();
    }

    // Úteis
    /**
    * Permite ao utilizador selecionar um plano de subscrição.
    * Repete a solicitação até uma opção válida ser fornecida.
    *
    * @param sc Scanner para leitura da entrada do utilizador
    * @return Instância do plano selecionado (Free, PremiumBase ou PremiumTop)
    */
    private static Plano selecionarPlano(Scanner sc) {
        while (true) {
            System.out.println("\nEscolha o seu plano:");
            System.out.println("1. Free");
            System.out.println("2. PremiumBase");
            System.out.println("3. PremiumTop");
            System.out.print("Opção: ");

            try {
                int escolha = Integer.parseInt(sc.nextLine());
                switch (escolha) {
                    case 1:
                        return new Free();
                    case 2:
                        return new PremiumBase();
                    case 3:
                        return new PremiumTop();
                    default:
                        System.out.println("Opção inválida. Tente novamente");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, insira um inteiro entre 1 e 3.");
            }
        }
    }

    /**
    * Permite ao utilizador escolher a privacidade de uma playlist.
    * Repete a solicitação até uma entrada válida ser fornecida.
    *
    * @param sc Scanner para leitura da entrada do utilizador
    * @return "Publica" ou "Privada"
    */
    private static String selecionarPrivacidade(Scanner sc) {
        while (true) {
            System.out.println("\nEscolha a Privacidade:");
            System.out.println("1. Publica");
            System.out.println("2. Privada");
            System.out.print("Opção: ");

            try {
                int escolha = Integer.parseInt(sc.nextLine());
                switch (escolha) {
                    case 1:
                        return "Publica";
                    case 2:
                        return "Privada";
                    default:
                        System.out.println("Opção inválida. Tente novamente");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, insira um inteiro entre 1 e 2.");
            }
        }
    }

    /**
    * Registra um novo utilizador na aplicação.
    * Solicita os dados necessários, escolhe plano e cria a conta.
    * Inicia a sessão se o registo for bem-sucedido.
    */
    private void registrarUtilizador() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduza o seu nome: ");
            String nome = sc.nextLine();

            System.out.println("Introduza o seu email: ");
            String email = sc.nextLine();

            System.out.println("Introduza a sua morada: ");
            String morada = sc.nextLine();
            
            Plano plano = selecionarPlano(sc);
            int pontos = 0;
            if (plano instanceof PremiumTop) {
                pontos = 100;
            }

            Utilizador novoUtilizador = new Utilizador(nome,email,morada,plano,pontos,null,null,null);

            if (app.addUtilizador(novoUtilizador)) {
                System.out.println("Registo bem sucedido! Bem-vindo " + nome + "!");
                iniciarSessaoMenu();
            } else {
                System.out.println("Erro: Email já registado.");
                iniciarSessaoMenu();
            }
    }

    /**
    * Reproduz uma playlist da biblioteca pessoal do utilizador na ordem original.
    *
    * @param user Utilizador autenticado
    */
    private void reproduzirPlaylistBiblioteca(Utilizador user) {
        System.out.println("\nPlaylists disponíveis: " + user.getPlaylists());
        System.out.println("\nDigite a playlist que quer ouvir: ");
        String playlist = Menu.getScanner().nextLine();
        
        if (!user.getPlaylists().contains(playlist)) {
            System.out.println("\nPlaylist não encontrada na sua biblioteca.");
            return;
        }
        
        List<String> resultado = app.reproduzirPlaylist(playlist);
        if (resultado != null) {
            System.out.println("\n=== A reproduzir a playlist: " + playlist + " ===");
            for (String m:resultado) menuReproduz(m);
        } else {
            System.out.println("\nErro ao reproduzir a playlist");
        }
        System.out.println("\nFim da playlist!");
    }

    /**
     * Reproduz uma playlist da biblioteca do utilizador em ordem aleatória.
     *
     * @param user Utilizador autenticado
     */
    private void reproduzirPlaylistBibliotecaAleat(Utilizador user) {
        System.out.println("\nPlaylists disponíveis: " + user.getPlaylists());
        System.out.println("\nDigite a playlist que quer ouvir: ");
        String playlist = Menu.getScanner().nextLine();
        
        if (!user.getPlaylists().contains(playlist)) {
            System.out.println("\nPlaylist não encontrada na sua biblioteca.");
            return;
        }
        
        List<String> resultado = app.reproduzPlaylistAleatoria(playlist);
        if (resultado != null) {
            System.out.println("\n=== A reproduzir aleatoriamente a playlist: " + playlist + " ===");
            for(String m:resultado) menuReproduz(m);
        } else {
            System.out.println("\nPlaylist não encontrada na sua biblioteca");
        }
        System.out.println("\nFim da playlist!");
    }

    /**
    * Reproduz um álbum da biblioteca do utilizador.
    *
    * @param user Utilizador autenticado
    */
    private void reproduzirAlbumBiblioteca(Utilizador user) {
        System.out.println("\nÁlbuns disponíveis: " + user.getAlbuns());
        System.out.println("\nDigite o álbum que quer ouvir: ");
        String album = Menu.getScanner().nextLine();

        if (!user.getAlbuns().contains(album)) {
            System.out.println("\nÁlbum não encontrado na sua biblioteca.");
            return;
        }

        List<String> resultado = app.reproduzirAlbum(album);
        if (resultado != null) {
            System.out.println("\n=== A reproduzir o álbum: " + album + " ===");
            for(String m:resultado) menuReproduz(m);
        } else {
            System.out.println("\nErro ao reproduzir o álbum.");
        }
        System.out.println("\nFim do álbum!");
    }
}