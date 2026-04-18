package net.buscacio.aula3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.IO.println;

/**
 *  A Falha do Bloqueio de Granularidade Grossa vs. o Poder do ConcurrentHashMap
 *
 * A forma mais antiga de tornar uma coleção "thread-safe" era usar os wrappers Collections.synchronized.... No entanto, essa
 * abordagem cria um gargalo de performance significativo em cenários de alta concorrência.
 *
 *  Este código contrasta diretamente a performance de um synchronizedMap com um ConcurrentHashMap. O synchronizedMap utiliza
 * um único lock (bloqueio) para todo o mapa. Isso significa que apenas uma thread pode acessar o mapa por vez, seja para leitura ou
 * escrita, serializando todas as operações e criando um gargalo de contenção severo.
 * O ConcurrentHashMap, por outro lado, utiliza uma técnica sofisticada chamada fine-grained locking ou lock striping. Em vez de um
 * único lock global, ele divide o mapa em segmentos (ou bins) e utiliza um lock para cada segmento. Isso permite que múltiplas threads
 * escrevam em diferentes partes do mapa simultaneamente. As operações de leitura geralmente são não-bloqueantes e podem ocorrer
 * em paralelo com as escritas. O resultado, como o teste demonstra, é uma escalabilidade e performance muito superiores, tornando o
 * ConcurrentHashMap a escolha padrão para mapas concorrentes em Java.
 */
public class Ex1MapContentionExample {

     static void main() throws InterruptedException {
        println("Testando Collections.synchronizedMap...");
        runTest(Collections.synchronizedMap(new HashMap<>()));

        println("\nTestando ConcurrentHashMap...");
        runTest(new ConcurrentHashMap<>());
    }

    private static void runTest(Map<String, Integer> map) throws InterruptedException {
        int numThreads = Runtime.getRuntime().availableProcessors() * 2;
        int operationsPerThread = 100_000;

        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            long startTime = System.nanoTime();

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = Thread.currentThread().getName() + "-" + j;
                        map.put(key, j);
                        map.get(key);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            println("Tempo total: " + duration + " ms");
            println("Tamanho final do mapa: " + map.size());
        }
    }
}