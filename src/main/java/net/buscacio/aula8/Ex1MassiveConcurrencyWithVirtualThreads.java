package net.buscacio.aula8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.lang.IO.println;

/**
 * As threads virtuais tornam o modelo "uma thread por requisição" não apenas viável, mas a abordagem recomendada e escalável para
 * aplicações I/O-bound.
 * ❑ Notas
 * ❑ Este exemplo cumpre a principal promessa das threads virtuais: escalabilidade massiva com código simples e bloqueante. O código
 * parece sequencial e é fácil de ler e depurar, mas por baixo dos panos, a JVM está gerenciando eficientemente 200.000 tarefas
 * concorrentes.
 * ❑ Antes do Projeto Loom, alcançar essa escala exigiria programação assíncrona complexa com callbacks ou CompletableFutures.
 * Agora, o modelo "uma thread por tarefa" é a forma mais simples e performática de lidar com cargas de trabalho I/O-bound. A thread
 * deixa de ser um recurso físico escasso para se tornar um objeto lógico barato.
 */

public class Ex1MassiveConcurrencyWithVirtualThreads {

    static void main() {
        long startTime = System.currentTimeMillis();

        // Simula o recebimento de 200.000 requisições, cada uma tratada
        // em sua própria thread virtual.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 200_000).forEach(i -> {
                executor.submit(() -> {
                    // Simula uma tarefa que faz I/O, como uma chamada a um banco de dados
                    // ou a uma API externa.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } // O try-with-resources garante que o executor espere todas as tarefas terminarem.

        long duration = System.currentTimeMillis() - startTime;
        println("Processadas 200.000 tarefas em " + duration + " ms.");
    }
}