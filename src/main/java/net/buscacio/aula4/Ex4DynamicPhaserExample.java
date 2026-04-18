package net.buscacio.aula4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import static java.lang.IO.println;

/**
 * Limitando o Acesso a Recursos com Semaphore
 * ❑ Um semáforo é usado para controlar o acesso a um recurso compartilhado, limitando o número de threads que podem acessá-lo
 * simultaneamente.
 * Este padrão é especialmente relevante na era das threads virtuais. Embora possamos criar milhões de threads virtuais, os recursos que
 * elas acessam (como conexões de banco de dados) geralmente são limitados. O semáforo é a ferramenta moderna e correta para
 * limitar a concorrência a esses recursos, conforme mencionado na aula sobre threads virtuais.
 */

public class Ex4DynamicPhaserExample {

  static class Task implements Runnable {
    private final int id;
    private final Phaser phaser;

    Task(int id, Phaser phaser) {
      this.id = id;
      this.phaser = phaser;
      phaser.register(); // Registra a tarefa como um participante.
      println("Tarefa " + id + " registrada. Participantes: " + phaser.getRegisteredParties());
    }

    @Override
    public void run() {
      println("Tarefa " + id + " na Fase " + phaser.getPhase() + ".");
      phaser.arriveAndAwaitAdvance(); // Chega e espera pelos outros.

      // Apenas algumas tarefas continuam para a próxima fase.
      if (id % 2 == 0) {
        println("Tarefa " + id + " continuando para a Fase " + phaser.getPhase() + ".");
        phaser.arriveAndAwaitAdvance();
        println("Tarefa " + id + " concluiu todas as fases.");
        phaser.arriveAndDeregister(); // Conclui e se desregistra.
      } else {
        println("Tarefa " + id + " concluindo e se desregistrando na Fase " + phaser.getPhase() + ".");
        phaser.arriveAndDeregister(); // Chega, se desregistra e não espera.
      }
    }
  }

  static void main() {
    Phaser phaser = new Phaser(1); // 1 para a thread principal.

    try (ExecutorService executor = Executors.newCachedThreadPool()) {
      for (int i = 0; i < 4; i++) {
        executor.submit(new Task(i, phaser));
      }

      println("Aguardando todas as tarefas concluírem a Fase 0...");
      phaser.arriveAndAwaitAdvance();
      println("Fase 0 concluída. Participantes restantes: " + phaser.getRegisteredParties());

      println("Aguardando tarefas restantes concluírem a Fase 1...");
      phaser.arriveAndAwaitAdvance();
      println("Fase 1 concluída. Participantes restantes: " + phaser.getRegisteredParties());

      phaser.arriveAndDeregister(); // Thread principal se desregistra.
    }
    println("Phaser terminado: " + phaser.isTerminated());
  }
}