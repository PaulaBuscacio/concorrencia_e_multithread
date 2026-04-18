package net.buscacio.aula3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.IO.println;

/**
 * Gerenciando Listeners de Eventos com CopyOnWriteArrayList
 * <p>
 * Para coleções que são lidas com muito mais frequência do que são modificadas, como listas de listeners de eventos, o
 * CopyOnWriteArrayList oferece uma estratégia de concorrência otimizada.
 * <p>
 * Este é o caso de uso ideal para CopyOnWriteArrayList. A estratégia "copiar ao escrever" significa que toda operação de modificação
 * (add, set, remove) cria uma cópia inteiramente nova do array subjacente. As operações de leitura, por outro lado, são muito rápidas,
 * pois não exigem locks e operam em um snapshot imutável do array.
 * Quando a Thread 1 está iterando sobre a lista para notificar os listeners, ela está trabalhando em uma cópia do array que existia no
 * momento em que o laço for-each começou. Quando a Thread 2 chama addListener, uma nova cópia do array é criada, incluindo o
 * novo listener. A Thread 1 não vê essa mudança e completa sua iteração sem erro. As iterações subsequentes verão a lista atualizada.
 * Isso evita a ConcurrentModificationException de forma elegante. Por outro lado, o custo destas modificações é alto, tornando esta
 * coleção inadequada para cenários com escritas frequentes.
 */

public class Ex2CopyOnWriteListenerExample {

  // Interface para o listener.
  interface EventListener {
    void onEvent(String event);
  }

  // Classe que notifica os listeners.
  static class Notifier {
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(EventListener listener) {
      listeners.add(listener);
      println("Listener adicionado. Lista atual: " + listeners.size());
    }

    public void notifyListeners(String event) {
      println("Notificando " + listeners.size() + " listeners sobre o evento: " + event);
      // A iteração é segura e não lança ConcurrentModificationException.
      for (EventListener listener : listeners) {
        listener.onEvent(event);
        try {
          // Simula um trabalho de notificação.
          Thread.sleep(10);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  static void main() throws InterruptedException {
    Notifier notifier = new Notifier();
    notifier.addListener(event -> println("Listener A recebeu: " + event));
    notifier.addListener(event -> println("Listener B recebeu: " + event));

    try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
      // Thread 1: Notifica os listeners continuamente.
      executor.submit(() -> {
        for (int i = 0; i < 5; i++) {
          notifier.notifyListeners("Evento " + i);
        }
      });

      // Thread 2: Adiciona um novo listener enquanto a notificação está ocorrendo.
      executor.submit(() -> {
        try {
          Thread.sleep(50); // Espera um pouco para a notificação começar.
          notifier.addListener(event -> println("Listener C (novo) recebeu: " + event));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });

      executor.shutdown();
      executor.awaitTermination(10, TimeUnit.SECONDS);
    }
  }
}