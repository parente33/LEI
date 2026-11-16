package loader;
import app.App;
public class LoaderManager{
    /**
     * Método que orquestra o Load das entidades
     * @param app Objeto App onde vai ser carregada a informação
     * @param dir_path Caminho para o diretório onde se encontram os ficheiros 'csv'
     */
    public static void loadCSV(App app,String dir_path) {
        ArtistaLoader.loadArtistas(app,dir_path);
        AlbumLoader.loadAlbuns(app, dir_path);
        PlaylistLoader.loadPlaylist(app, dir_path);
        UserLoader.loadUtilizadores(app, dir_path);
        
    }
}