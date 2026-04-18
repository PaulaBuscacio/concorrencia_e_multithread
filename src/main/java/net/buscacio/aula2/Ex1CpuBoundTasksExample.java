package net.buscacio.aula2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.IO.println;

/**
 *  Cargas de Trabalho CPU-Bound com um Pool Fixo de Threads de Plataforma
 *
 * A estratégia aqui é limitar o número de threads ao número de núcleos de CPU disponíveis. A razão para isso é
 * que tarefas CPU-bound mantêm o processador constantemente ocupado. Se criarmos mais threads do que núcleos, o sistema
 * operacional será forçado a realizar trocas de contexto frequentes, pausando uma thread para executar outra. Essa troca de contexto
 * tem um custo (overhead) e, em vez de acelerar, degrada a performance geral do sistema. Este exemplo estabelece o modelo de
 * "gerenciamento de recursos escassos", onde as threads de plataforma são vistas como um recurso valioso que precisa ser
 * cuidadosamente alocado
 */
public class Ex1CpuBoundTasksExample {

    static void main(String[] args) {
        // O número de threads é dimensionado de acordo com os núcleos do processador.
        int coreCount = Runtime.getRuntime().availableProcessors();
        println("Número de núcleos de CPU: " + coreCount);

        // try-with-resources garante que o executor seja desligado.
        try (ExecutorService executor = Executors.newFixedThreadPool(coreCount)) {
            // Submete N tarefas CPU-bound.
            for (int i = 0; i < coreCount * 2; i++) {
                final int taskNumber = i;
                executor.submit(() -> {
                    println("Iniciando tarefa CPU-bound " + taskNumber + " em " + Thread.currentThread());
                    // Simula um trabalho computacionalmente intensivo.
                    long result = performIntensiveCalculation();
                    println("Tarefa " + taskNumber + " concluída com resultado " + result + " em " + Thread.currentThread());
                });
            }
        } // executor.shutdown() é chamado automaticamente aqui.
    }

    private static long performIntensiveCalculation() {
        // Simulação de uma tarefa que mantém a CPU ocupada.
        long sum = 0;
        for (int i = 0; i < 1_000_000_000; i++) {
            sum += i;
        }
        return sum;
    }
}
