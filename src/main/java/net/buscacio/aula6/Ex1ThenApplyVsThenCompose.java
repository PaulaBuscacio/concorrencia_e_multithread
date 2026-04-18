package net.buscacio.aula6;

import java.util.concurrent.CompletableFuture;

import static java.lang.IO.print;
import static java.lang.IO.println;

/**
 * a diferença fundamental entre thenApply e thenCompose.
 * ❑ thenApply(T -> U): É usado para uma transformação síncrona do resultado de um CompletableFuture. É análogo ao
 * Stream.map(). Se a função de transformação retorna um CompletableFuture, o resultado será um CompletableFuture aninhado
 * (CompletableFuture<CompletableFuture<Profile>>), o que é inconveniente.
 * ❑ thenCompose(T -> CompletableFuture<U>): É usado para encadear uma operação assíncrona que depende do resultado da
 * anterior. É análogo ao Stream.flatMap(). Ele "achata" o resultado, evitando o aninhamento e retornando um
 * CompletableFuture<Profile> simples, facilitando o encadeamento de mais operações.
 * ❑ A regra geral é: se a sua função de encadeamento retorna um valor diretamente, use thenApply. Se ela retorna outro
 * CompletableFuture, use thenCompose
 */
public class Ex1ThenApplyVsThenCompose {

    record User(int id, String name) {}
    record Profile(int userId, String details) {}

    static CompletableFuture<User> getUserById(int id) {
        return CompletableFuture.supplyAsync(() -> {
            println("Buscando usuário " + id + "...");
            return new User(id, "Usuário " + id);
        });
    }

    static CompletableFuture<Profile> getProfileForUser(User user) {
        return CompletableFuture.supplyAsync(() -> {
            println("Buscando perfil para " + user.name() + "...");
            return new Profile(user.id(), "Detalhes do perfil...");
        });
    }

    static void main() {
        // Uso incorreto com thenApply para encadear operações assíncronas
        CompletableFuture<CompletableFuture<Profile>> nestedFuture =
                getUserById(101).thenApply(Ex1ThenApplyVsThenCompose::getProfileForUser);
        println("Tipo de retorno com thenApply: " + nestedFuture.getClass().getSimpleName());
        println(nestedFuture.join()); // Isso vai mostrar um CompletableFuture<Profile>, não o Profile em si.

        // Uso correto com thenCompose para "achatar" o resultado
        CompletableFuture<Profile> flatFuture =
                getUserById(102).thenCompose(Ex1ThenApplyVsThenCompose::getProfileForUser);
        println("Tipo de retorno com thenCompose: " + flatFuture.getClass().getSimpleName());

        Profile profile = flatFuture.join(); // Bloqueia para obter o resultado final
        println("Perfil obtido: " + profile);
    }
}