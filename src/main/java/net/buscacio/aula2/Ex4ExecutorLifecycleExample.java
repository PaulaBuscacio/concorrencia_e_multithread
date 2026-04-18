package net.buscacio.aula2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.IO.println;

/**
 Um erro comum em aplicações concorrentes é não desligar adequadamente os ExecutorService. Isso pode causar vazamentos de recursos e impedir
que a JVM finalize.
 */

public class Ex4ExecutorLifecycleExample {

   static void main() {
        // A partir do Java 19, ExecutorService implementa AutoCloseable.
        // O uso de try-with-resources é a forma preferida e mais segura.
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> println("Tarefa em execução."));
        } // executor.shutdown() é chamado automaticamente.
        println("Executor desligado via try-with-resources.");

        // Padrão de desligamento manual (para Java < 19 ou cenários complexos).
        ExecutorService manualExecutor = Executors.newFixedThreadPool(2);
        try {
            manualExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                println("Tarefa no executor manual concluída.");
            });
        } finally {
            shutdownAndAwaitTermination(manualExecutor);
        }
        println("Executor manual desligado.");
    }

    static void shutdownAndAwaitTermination(ExecutorService pool) {
        // Desabilita novas tarefas de serem submetidas.
        pool.shutdown();
        try {
            // Espera um tempo razoável para as tarefas existentes terminarem.
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancela tarefas em execução.
                pool.shutdownNow();
                // Espera um tempo razoável para as tarefas responderem ao cancelamento.
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("O pool não terminou.");
            }
        } catch (InterruptedException ie) {
            // (Re)Cancela se a thread atual for interrompida.
            pool.shutdownNow();
            // Preserva o status de interrupção.
            Thread.currentThread().interrupt();
        }
    }
}