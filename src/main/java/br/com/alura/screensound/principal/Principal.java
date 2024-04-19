package br.com.alura.screensound.principal;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.model.TipoArtista;
import br.com.alura.screensound.repository.ArtistaRepository;
import br.com.alura.screensound.service.ConsultaChatGPT;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final ArtistaRepository artistaRepository;

    private Scanner leitura = new Scanner(System.in);

    public Principal(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    *** Screen Sound Músicas ***
                    
                    1 - Cadastrar artistas
                    2 - Cadastrar músicas
                    3 - Listar músicas
                    4 - Buscar músicas por artistas
                    5 - Pesquisar dados sobre um artista
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 0:
                    System.out.println("Encerrando aplicação...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void cadastrarArtistas() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("S")) {
            System.out.println("Informe o nome do artista:");
            var nome = leitura.nextLine();

            System.out.println("Informe o tipo do artista: (solo, dupla ou banda)");
            var tipo = leitura.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase());

            Artista artista = new Artista(nome, tipoArtista);

            artistaRepository.save(artista);

            System.out.println("Cadastrar novo artista? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastrar música de que artista?");
        var nome = leitura.nextLine();

        Optional<Artista> artista = artistaRepository.findByNomeContainingIgnoreCase(nome);

        if (artista.isPresent()) {
            System.out.println("Informe o título da música:");
            var titulo = leitura.nextLine();

            Musica musica = new Musica(titulo, artista.get());

            artista.get().getMusicas().add(musica);

            artistaRepository.save(artista.get());
        } else {
            System.out.println("Artista não encontrado");
        }
    }

    private void listarMusicas() {
        List<Artista> artistas = artistaRepository.findAll();

        artistas.forEach(artista -> artista.getMusicas().forEach(System.out::println));
    }

    private void buscarMusicasPorArtista() {
        System.out.println("Buscar músicas de que artista?");
        var nome = leitura.nextLine();

        List<Musica> musicas = artistaRepository.buscaMusicasPorArtista(nome);

        musicas.forEach(System.out::println);
    }

    private void pesquisarDadosDoArtista() {
        System.out.println("Pesquisar dados sobre qual artista?");
        var nome = leitura.nextLine();

        var resposta = ConsultaChatGPT.obterInformacao(nome);

        System.out.println(resposta.trim());
    }
}
