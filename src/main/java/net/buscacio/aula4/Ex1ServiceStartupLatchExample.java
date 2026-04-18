package net.buscacio.aula4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.IO.println;

/**
 * Coordenando a Inicialização de Serviços com CountDownLatch
 * <p>
 * Um cenário comum em aplicações de servidor é garantir que todos os serviços essenciais estejam prontos antes de começar a aceitar
 * tráfego. O CountDownLatch é perfeito para isso.
 * É crucial entender que o CountDownLatch é um mecanismo de uso único (one-shot); uma vez que a contagem chega a zero, ele não
 * pode ser resetado. É ideal para cenários do tipo "espere por N eventos acontecerem uma vez".
 *
 */

public class Ex1ServiceStartupLatchExample {

  record ServiceInitializer(String serviceName, int startupTime, CountDownLatch latch) implements Runnable {

    @Override
    public void run() {
      try {
        println("Inicializando " + serviceName + "...");
        Thread.sleep(startupTime);
        println(serviceName + " inicializado.");
        println("CountDown: " + latch.getCount());
        latch.countDown(); // Decrementa o contador do latch.
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  static void main() throws InterruptedException {
    int numberOfServices = 3;
    CountDownLatch startupLatch = new CountDownLatch(numberOfServices);

    try (ExecutorService executor = Executors.newFixedThreadPool(numberOfServices)) {
      executor.submit(new ServiceInitializer("DatabaseService", 3000, startupLatch));
      executor.submit(new ServiceInitializer("CacheService", 5000, startupLatch));
      executor.submit(new ServiceInitializer("MessagingService", 7000, startupLatch));


      println("Thread principal aguardando a inicialização dos serviços...");
      startupLatch.await(); // Bloqueia até o contador chegar a zero.
      println("CountDown: " + startupLatch.getCount());
      println("Todos os serviços foram inicializados. Aplicação pronta!");
    }
  }
}