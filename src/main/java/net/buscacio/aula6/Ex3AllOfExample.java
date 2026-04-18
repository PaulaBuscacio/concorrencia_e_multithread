package net.buscacio.aula6;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.lang.IO.println;

/**
 * O método CompletableFuture.allOf() é usado para esperar que um conjunto de CompletableFutures seja concluído. Ele retorna um
 * CompletableFuture<Void> que é concluído quando todos os CompletableFutures fornecidos são concluídos. No entanto, ele não retorna os
 * resultados individuais dos CompletableFutures, apenas sinaliza que todos terminaram.
 * Para obter os resultados, você precisa processar a lista original de CompletableFutures após a conclusão do allOf. Isso geralmente é
 * feito usando thenApply ou thenCompose para transformar o resultado do allOf em uma lista dos resultados individuais.
 */
public class Ex3AllOfExample {

    static CompletableFuture<String> downloadData(String source) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int delay = 1000 + (int) (Math.random() * 1000);
                Thread.sleep(delay);
                println("Dados de " + source + " baixados em " + delay + "ms.");
            } catch (Exception e) {}
            return "Dados de " + source;
        });
    }

    static void main() {
        List<String> sources = List.of("API_1", "API_2", "API_3", "API_4");

        List<CompletableFuture<String>> futures = sources.stream()
                .map(Ex3AllOfExample::downloadData)
                .collect(Collectors.toList());

        // CompletableFuture.allOf retorna CompletableFuture<Void>.
        // Ele serve apenas para sinalizar que todas as tarefas terminaram.
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // Para obter os resultados, precisamos processar a lista original de futures
        // após a conclusão do allDoneFuture.
        CompletableFuture<List<String>> allResultsFuture = allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join) // join() é seguro aqui porque sabemos que todos já terminaram.
                        .collect(Collectors.toList())
        );

        List<String> results = allResultsFuture.join();
        println("\nTodos os downloads concluídos. Resultados:");
        results.forEach(System.out::println);
    }
}