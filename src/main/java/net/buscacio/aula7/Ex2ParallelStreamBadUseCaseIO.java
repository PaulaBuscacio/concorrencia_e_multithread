package net.buscacio.aula7;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.IO.println;

/**
 * Este exemplo ilustra um antipadrão crítico. Os Parallel Streams utilizam o ForkJoinPool.commonPool() compartilhado, que por padrão
 * tem um número de threads de plataforma igual ao número de núcleos da CPU (ou num_cores - 1).
 * ❑ Se uma tarefa dentro do stream realiza uma operação de I/O bloqueante (simulada aqui com Thread.sleep()), ela monopoliza uma
 * dessas preciosas threads de plataforma, que fica ociosa esperando.
 * ❑ Se todas as threads do commonPool forem bloqueadas por operações de I/O, o pool inteiro fica saturado. Isso não apenas impede
 * que o parallelStream escale, mas também pode paralisar outras partes da aplicação que dependem do commonPool, como
 * CompletableFutures ou outras tarefas Fork/Join.
 * ❑ Para paralelismo de I/O, threads virtuais ou CompletableFuture com um executor customizado são as soluções corretas.
 */
public class Ex2ParallelStreamBadUseCaseIO {

    static void main() {
        List<Integer> ids = IntStream.rangeClosed(1, 20).boxed().toList();

        println("Iniciando chamadas de API com parallelStream()...");
        long startTime = System.currentTimeMillis();

        ids.parallelStream().forEach(id -> {
            // Simula uma chamada de rede bloqueante.
            try {
                println("Buscando dados para o ID " + id + " em " + Thread.currentThread());
                Thread.sleep(1000); // Bloqueia a thread por 1 segundo.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        long duration = System.currentTimeMillis() - startTime;
        println("Tempo total: " + duration + " ms.");
        // O tempo total será muito maior do que o esperado
    }
}