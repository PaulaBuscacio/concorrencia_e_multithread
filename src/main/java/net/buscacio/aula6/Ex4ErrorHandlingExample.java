package net.buscacio.aula6;

import java.util.concurrent.CompletableFuture;

import static java.lang.IO.println;

/**
 *  Este código demonstra as duas principais formas de tratamento de erros no CompletableFuture.
 * ❑ exceptionally(Function<Throwable, T>): É o equivalente assíncrono de um bloco catch. Ele é acionado apenas se o estágio anterior for concluído com uma
 * exceção. A função recebe a exceção e pode retornar um valor de fallback do mesmo tipo do CompletableFuture, permitindo que o pipeline se recupere e
 * continue.
 * ❑ handle(BiFunction<T, Throwable, U>): É mais geral, análogo a um bloco finally que pode transformar o resultado. Ele é sempre executado, independentemente
 * de o estágio anterior ter tido sucesso ou falha. A BiFunction recebe dois argumentos: o resultado (que será null se houver erro) e a exceção (que será null se
 * houver sucesso). Isso permite processar ambos os cenários em um único lugar.
 * ❑ O CompletableFuture introduziu padrões de composição funcional na concorrência Java, permitindo que os desenvolvedores pensem em fluxos de trabalho
 * concorrentes como pipelines de dados. No entanto, essa mesma expressividade pode levar a cadeias de chamadas complexas e difíceis de depurar.
 * ❑ Este é exatamente o problema que as Threads Virtuais e a Concorrência Estruturada visam resolver, trazendo a concorrência de volta a uma estrutura léxica mais
 * simples, com aparência sequencial. Portanto, o CompletableFuture pode ser visto tanto como uma ferramenta poderosa quanto como um artefato histórico das
 * restrições da era pré-Loom
 */
public class Ex4ErrorHandlingExample {

    static void main() {
        // 1. Usando exceptionally() para fornecer um valor de fallback (como um 'catch')
        CompletableFuture<String> futureWithFallback = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Falha na simulação!");
            }
            return "Sucesso!";
        }).exceptionally(ex -> {
            System.err.println("Ocorreu um erro: " + ex.getMessage());
            return "Valor de Fallback"; // Retorna um valor padrão em caso de erro.
        });

        println("Resultado com fallback: " + futureWithFallback.join());
        println("---");

        // 2. Usando handle() para processar sucesso ou falha (como um 'finally' que retorna valor)
        CompletableFuture<String> futureHandled = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Falha na simulação!");
            }
            return "Sucesso!";
        }).handle((result, ex) -> {
            if (ex!= null) {
                System.err.println("Ocorreu um erro: " + ex.getMessage());
                return "Resultado do Erro Processado";
            }
            return "Resultado de Sucesso Processado: " + result;
        });

        println("Resultado com handle: " + futureHandled.join());
    }
}