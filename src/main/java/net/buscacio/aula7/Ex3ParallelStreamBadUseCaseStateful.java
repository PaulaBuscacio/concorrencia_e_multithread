package net.buscacio.aula7;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.IO.println;

/**
 *  Este código demonstra o perigo de lambdas com estado (stateful lambdas). A primeira tentativa de adicionar elementos a um ArrayList
 * padrão de dentro de um forEach paralelo resultará em uma condição de corrida. Múltiplas threads tentarão modificar a estrutura
 * interna do ArrayList ao mesmo tempo, levando a resultados inconsistentes (elementos perdidos) e potencialmente a exceções.
 * ❑ As operações de stream devem ser livres de efeitos colaterais. A maneira correta de agregar resultados de um stream paralelo é usar
 * as operações de terminal projetadas para isso, como os Collectors. O Collectors.toList() usa internamente um mecanismo thread-safe
 * para combinar os resultados parciais de cada thread em uma lista final.
 * ❑ A decisão de projeto de fazer o parallelStream() usar um commonPool global e compartilhado é a causa raiz de suas maiores
 * armadilhas. Embora conveniente, cria um cenário de "tragédia dos comuns", onde um stream mal comportado pode impactar
 * negativamente toda a aplicação. Isso torna o parallelStream() uma ferramenta altamente especializada, não um acelerador de
 * propósito geral
 */
public class Ex3ParallelStreamBadUseCaseStateful {

    static void main() {
        List<Integer> resultList = new ArrayList<>();

        // TENTATIVA INCORRETA de coletar resultados.
        // A ordem de execução não é garantida em múltiplas threads
        // modificarão a lista não-thread-safe simultaneamente.
        IntStream.range(0, 1000).parallel().forEach(i -> {
            resultList.add(i);
        });

        // O tamanho da lista provavelmente não será 1000.
        println("Tamanho da lista (incorreto): " + resultList.size());

        // A FORMA CORRETA é usar coletores thread-safe.
        List<Integer> correctList = IntStream.range(0, 1000)
                .parallel()
                .boxed()
                .collect(Collectors.toList());

        println("Tamanho da lista (correto): " + correctList.size());
    }
}