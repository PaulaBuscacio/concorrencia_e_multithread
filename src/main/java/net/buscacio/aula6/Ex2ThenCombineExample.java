package net.buscacio.aula6;

import java.util.concurrent.CompletableFuture;

import static java.lang.IO.println;

/**
 * Quando duas tarefas assíncronas podem ser executadas em paralelo e seus resultados precisam ser combinados, thenCombine é a
 * escolha certa.
 * ❑ Este código demonstra o thenCombine. As chamadas getOrderHistory() e getShippingPreferences() são disparadas e executam em
 * paralelo. thenCombine espera que ambas as tarefas sejam concluídas. Quando isso acontece, ele executa a BiFunction fornecida, que
 * recebe os resultados de ambas as tarefas como argumentos e produz um único resultado combinado.
 * ❑ É importante diferenciar de thenCompose, que é para operações assíncronas sequenciais e dependentes. thenCombine é para
 * operações paralelas e independentes.
 */
public class Ex2ThenCombineExample {

  static CompletableFuture<String> getOrderHistory() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
      return "Histórico de Pedidos";
    });
  }

  static CompletableFuture<String> getShippingPreferences() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1200);
      } catch (Exception e) {
      }
      return "Preferências de Envio";
    });
  }

  static void main() {
    println("Iniciando busca de dados do cliente...");

    CompletableFuture<String> customerDataFuture =
        getOrderHistory().thenCombine(getShippingPreferences(), (history, prefs) -> "Dados do Cliente:\n- " + history + "\n- " + prefs);

    String customerData = customerDataFuture.join();
    println(customerData);
  }
}