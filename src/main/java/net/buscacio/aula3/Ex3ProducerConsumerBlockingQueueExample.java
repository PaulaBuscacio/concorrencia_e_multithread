package net.buscacio.aula3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  O Padrão Produtor-Consumidor com BlockingQueue e Threads Virtuais
 *
 * O padrão produtor-consumidor é fundamental para desacoplar componentes em sistemas concorrentes. A interface BlockingQueue
 * fornece uma implementação robusta e simples deste padrão.
 *
 *  Este código implementa o padrão produtor-consumidor usando uma BlockingQueue. A beleza desta abordagem é que a
 * coordenação entre as threads é gerenciada inteiramente pela fila, sem a necessidade de wait(), notify() ou synchronized explícitos.
 * ❑ queue.put(item): O produtor chama este método. Se a fila estiver cheia (atingiu sua capacidade de 10), a thread do produtor será
 * bloqueada automaticamente até que o consumidor remova um item e libere espaço.
 * ❑ queue.take(): O consumidor chama este método. Se a fila estiver vazia, a thread do consumidor será bloqueada até que o
 * produtor adicione um novo item.
 * ❑ Essa mecânica de bloqueio fornece um controle de fluxo natural, conhecido como back-pressure. Combinar BlockingQueue com
 * threads virtuais é um padrão extremamente poderoso para construir pipelines de processamento de dados I/O-bound, pois nem o
 * produtor nem o consumidor ocuparão uma thread de plataforma enquanto estiverem bloqueados, esperando pela fila
 */
public class Ex3ProducerConsumerBlockingQueueExample {

    static void main() {
        // Uma fila com capacidade fixa para 10 itens.
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Produtor: gera números e os coloca na fila.
            executor.submit(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        System.out.println("Produzindo: " + i);
                        queue.put(i); // Bloqueia se a fila estiver cheia.
                        Thread.sleep(50); // Simula tempo de produção.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Consumidor: retira números da fila e os processa.
            executor.submit(() -> {
                try {
                    while (true) {
                        Integer value = queue.take(); // Bloqueia se a fila estiver vazia.
                        System.out.println("Consumindo: " + value);
                        Thread.sleep(100); // Simula tempo de processamento.
                        if (value == 99) break; // Condição de parada.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}