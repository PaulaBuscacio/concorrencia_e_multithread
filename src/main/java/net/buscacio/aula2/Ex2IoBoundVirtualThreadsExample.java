package net.buscacio.aula2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.lang.IO.println;

/**
 *  Alta Vazão de I/O com newVirtualThreadPerTaskExecutor
 *
 *  Em contraste com as tarefas CPU-bound, as tarefas I/O-bound passam a maior parte do tempo esperando por respostas da rede, do disco ou de outros
 * sistemas. As threads virtuais foram projetadas especificamente para escalar aplicações com um grande número dessas tarefas.
 *
 *  As threads virtuais são gerenciadas pela JVM, não diretamente pelo sistema operacional. Elas são "montadas" em um
 * pequeno pool de threads de plataforma chamadas de carrier threads. Quando uma thread virtual executa uma operação de I/O bloqueante (como o
 * Thread.sleep() aqui), a JVM a "desmonta" da carrier thread, liberando a thread de plataforma para executar outra thread virtual. Assim que a operação de
 * I/O é concluída, a JVM "remonta" a thread virtual em uma carrier thread disponível para continuar sua execução. Esse processo permite que um pequeno
 * número de threads de plataforma gerencie milhões de threads virtuais concorrentes, alcançando uma vazão (throughput) massiva para cargas de
 * trabalho I/O-bound.
 *  A existência do newVirtualThreadPerTaskExecutor representa uma mudança filosófica no propósito da classe Executors. Anteriormente, seu papel era
 * criar pools para limitar a concorrência e reutilizar um recurso escarso. Agora, sua funcionalidade mais poderosa é criar um executor que permite
 * concorrência massiva, tratando threads como um conceito lógico abundante. Isso reflete uma transição de um modelo de concorrência restrito por
 * recursos para um modelo centrado na tarefa.
 */
public class Ex2IoBoundVirtualThreadsExample {

    static void main() {
        // Cria um executor que lança uma nova thread virtual para cada tarefa.
        // Não há pooling de threads virtuais, pois elas são extremamente leves.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 100_000).forEach(i -> {
                executor.submit(() -> {
                    // Simula uma chamada de rede bloqueante (I/O).
                    try {
                        println("Iniciando tarefa I/O-bound " + i + " em " + Thread.currentThread());
                        Thread.sleep(1000); // Simula 1 segundo de espera.
                        println("Tarefa I/O-bound " + i + " concluída.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });

        } // O executor aguarda a conclusão de todas as tarefas antes de fechar.
    }
}