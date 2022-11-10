package Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GraphMenu {
    private final Graph graph;
    private final Scanner scanner = new Scanner(System.in);

    public GraphMenu(Graph graph) {
        this.graph = graph;
    }

    public void run(){
        while (true){
            switch (printOptions("Editar grafo", "Busca", "Gerar Sub Grafo", "Print", "Sair")){
                case 1 -> editMenu();
                case 2 -> searchMenu();
                case 3 -> subGraphMenu();
                case 4 -> printMenu();
                case 5 ->{
                    return;
                }
            }
        }
    }

    private void printMenu(){
        while (true) {
            switch (printOptions("Print vertices", "Print arestas", "Print travessia", "Voltar")) {
                case 1 -> System.out.println(Arrays.toString(graph.getNodes()));
                case 2 -> {
                    for (Node<?> n : graph.getNodes()) {
                        System.out.print(n.toString() + " -> ");
                        for (Node.AdjacencyHolder adj : n.getAdjacencyHolders()) {
                            System.out.print("[" + adj.getNode() + ", peso: " + adj.getWeight() + "] ");
                        }
                        System.out.println();
                    }
                }
                case 3 -> {
                    switch (printOptions("BFS", "DFS")) {
                        case 1 -> System.out.println(Arrays.toString(graph.getBfsTraversal()));
                        case 2 -> System.out.println(Arrays.toString(graph.getDfsTraversal()));
                    }
                }
                case 4 -> {
                    return;
                }
            }
            continueMenu();
        }
    }

    private void editMenu(){
        while (true) {
            switch (printOptions("Adicionar node", "Remove node", "criar adjacencia", "Voltar")) {
                case 1 -> {
                    System.out.print("Digite o rotulo do node: ");
                    Node<?> node = new Node<>(getInputString());
                    if (this.graph.add(node)) {
                        System.out.println("Adicionado com sucesso!");
                    } else {
                        System.out.println("Erro: node ja existe");
                    }
                }
                case 2 -> {
                    System.out.print("Digite o rotulo do node: ");
                    if (this.graph.remove(getInputString())) {
                        System.out.println("Removido com sucesso!");
                    } else {
                        System.out.println("Erro: node nao existe");
                    }
                }
                case 3 -> {
                    switch (printOptions("Direcionado", "Nao direcionado", "Voltar")) {
                        case 1 -> {
                            Object[] keys = getTwoNodes();
                            System.out.print("Digite o peso da aresta: ");
                            if (this.graph.newAdjacency(keys[0], keys[1], getInputInt())) {
                                System.out.println("Aresta adicionado com sucesso!");
                            } else {
                                System.out.println("Erro: nao foi possivel adicionar aresta!");
                            }
                        }
                        case 2 -> {
                            Object[] keys = getTwoNodes();
                            System.out.print("Digite o peso da aresta: ");
                            if (this.graph.newNonDirectedAdjacency(keys[0], keys[1], getInputInt())) {
                                System.out.println("Aresta adicionado com sucesso!");
                            } else {
                                System.out.println("Erro: nao foi possivel adicionar aresta!");
                            }
                        }
                    }
                }
                case 4 -> {
                    return;
                }
            }
        }
    }

    private void searchMenu(){
        Object[] keys = getTwoNodes();
        switch (printOptions("BFS", "DFS")){
            case 1 -> {
                if(this.graph.search(keys[0], keys[1])){
                    System.out.println("Existe caminho do vertice " + keys[0] + " ate " + keys[1]);
                }else{
                    System.out.println("Nao existe caminho para chegar no " + keys[1] + " pelo " + keys[0]);
                }
            }
            case 2 -> {
                if(this.graph.dfsSearch(keys[0], keys[1])){
                    System.out.println("Existe caminho do vertice " + keys[0] + " ate " + keys[1]);
                }else{
                    System.out.println("Nao existe caminho para chegar no " + keys[1] + " pelo " + keys[0]);
                }
            }
        }
        continueMenu();
    }

    private void subGraphMenu(){
        switch (printOptions("Criar arvore geradora minima", "Pegar componentes")){
            case 1 -> {
                Graph minTree = graph.genMinimumSpanningTree();
                System.out.println("Arvore criada, o menu de opcoes para ela ira aparecer agora");
                continueMenu();
                GraphMenu minTreeMenu = new GraphMenu(minTree);
                minTreeMenu.run();
                System.out.println("Voce saiu do menu da arvore criada, voltando para o grafo original...");
                continueMenu();
            }
            case 2 -> {
                ArrayList<Graph> components = graph.getComponents();
                for(Graph g : components){
                    System.out.println("Componente do grafo principal encontrado, o menu de opcoes para ele ira aparecer agora...");
                    continueMenu();
                    GraphMenu menu = new GraphMenu(g);
                    menu.run();
                    System.out.println("Voce saiu do menu do componente criado...");
                }
                System.out.println("Voce saiu do menu de todos os componentes, voltando para o grafo original...");
                continueMenu();
            }
        }
    }

    private void continueMenu(){
        System.out.println("\nprecine enter para continuar...");
        getInputString();
    }

    private Object[] getTwoNodes(){
        Object[] objects = new Object[2];
        System.out.print("Digite o rotulo do node de origem: ");
        objects[0] = getInputString();
        System.out.print("Digite o rotulo do node de destino: ");
        objects[1] = getInputString();
        return objects;
    }

    private int printOptions(String... options){
        while (true){
            try {
                System.out.println("\nDigite uma opcao: ");
                for(int i = 0; i < options.length; i++){
                    System.out.println((i + 1) + " - " + options[i]);
                }
                System.out.print("R: ");
                int in = getInputInt();
                if(in > options.length || in < 1){
                    throw new Exception();
                }
                System.out.println();
                return in;
            } catch (Exception e) {
                System.out.println("Selecione uma opcao valida...");
            }
        }
    }

    private int getInputInt(){
        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }
    private String getInputString(){
        return scanner.nextLine();
    }
}
