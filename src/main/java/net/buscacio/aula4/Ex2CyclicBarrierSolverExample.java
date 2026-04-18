package net.buscacio.aula4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.IO.println;

/**
 * Sincronizando Trabalho Iterativo com CyclicBarrier
 * ❑ Para algoritmos paralelos que operam em fases, onde um grupo de threads precisa se sincronizar repetidamente antes de avançar, o
 * CyclicBarrier é a ferramenta adequada.
 *  No exemplo, 3 workers executam a Fase 1 e chamam barrier.await(). As duas primeiras threads a chegar ficarão bloqueadas. Quando a
 * terceira thread chega, a barreira é "quebrada", a barrierAction opcional é executada (pela última thread a chegar), e todas as três
 * threads são liberadas para iniciar a Fase 2. A barreira então se reseta automaticamente, pronta para a próxima fase. É "cíclica" porque
 * pode ser reutilizada, ao contrário do  CountDownLatch
 */

public class Ex2CyclicBarrierSolverExample {

    static class Worker implements Runnable {
        private final int id;
        private final CyclicBarrier barrier;

        public Worker(int id, CyclicBarrier barrier) {
            this.id = id;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                // Fase 1: Processamento de dados
                println("Worker " + id + " iniciando Fase 1.");
                Thread.sleep(1000 + (id * 500));
                println("Worker " + id + " concluiu Fase 1, aguardando na barreira.");
                barrier.await();

                // Fase 2: Validação de dados
                println("Worker " + id + " iniciando Fase 2.");
                Thread.sleep(1000 + (id * 500));
                println("Worker " + id + " concluiu Fase 2, aguardando na barreira.");
                barrier.await();

                println("Worker " + id + " concluiu o trabalho.");

            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void main() {
        int numberOfWorkers = 3;

        // Ação da barreira: executada quando todas as threads chegam.
        Runnable barrierAction = () -> println("\n--- Barreira quebrada! Todos os workers concluíram a fase. Próxima fase iniciada. ---\n");
        CyclicBarrier barrier = new CyclicBarrier(numberOfWorkers, barrierAction);

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers)) {
            for (int i = 0; i < numberOfWorkers; i++) {
                executor.submit(new Worker(i, barrier));
            }
        }
    }
}