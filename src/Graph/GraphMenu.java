package Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GraphMenu {
    private final Graph graph;

    public GraphMenu(Graph graph) {
        this.graph = graph;
    }
    public GraphMenu(){
        System.out.println("Digite o local do arquivo para carregar:");
        this.graph = Graph.loadFromFile(getInputString());
    }

    public void run(){
        while (true){
            switch (printOptions("Editar grafo", "Busca", "Gerenciar Sub Grafos", "Print", "Salvar", "Sair")){
                case 1 -> editMenu();
                case 2 -> searchMenu();
                case 3 -> subGraphMenu();
                case 4 -> printMenu();
                case 5 -> saveGraph();
                case 6 -> {
                    return;
                }
            }
        }
    }

    private void saveGraph(){
        System.out.println("Digite o local para salvar o arquivo (vazio para usar o local padrao)");
        String location = getInputString();
        String name = null;
        if(!location.isEmpty()) {
            System.out.println("Digite o nome do arquivo:");
            name = getInputString();
        }
        if(graph.saveToFile(location, name)){
            System.out.println("Salvo com sucesso!");
        }else{
            System.out.println("Nao foi possivel salvar");
        }
    }

    private void printMenu(){
        while (true) {
            switch (printOptions("Print vertices", "Print arestas", "Print travessia", "Print nodes a distancia X", "Print caminho mais curto", "Print caminho mais longo", "Voltar")) {
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
                    System.out.println("Digite o rotulo do node de origem:");
                    String node = getInputString();
                    System.out.println("Digite a distancia:");
                    int dist = getInputInt();
                    System.out.println(graph.getNodesAtDistance(node, dist));
                }
                case 5 -> {
                    Object[] keys = getOrgDstInput();
                    System.out.println(graph.getShortestPath(keys[0], keys[1]));
                }
                case 6 -> {
                    Object[] keys = getOrgDstInput();
                    System.out.println(graph.getLongestPath(keys[0], keys[1]));
                }
                case 7 -> {
                    return;
                }
            }
            continueMenu();
        }
    }

    private void editMenu(){
        while (true) {
            switch (printOptions("Adicionar node", "Remover node", "Criar adjacencia", "Remover adjacencia", "Voltar")) {
                case 1 -> {
                    System.out.print("Digite o rotulo do node: ");
                    if (this.graph.add(getInputString())) {
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
                    Object[] keys = getOrgDstInput();
                    System.out.print("Digite o peso da aresta: ");
                    if (this.graph.newAdjacency(keys[0], keys[1], getInputInt())) {
                        System.out.println("Aresta adicionado com sucesso!");
                    } else {
                        System.out.println("Erro: nao foi possivel adicionar aresta!");
                    }
                }
                case 4 -> {
                    Object[] keys = getOrgDstInput();
                    this.graph.removeAdjacency(keys[0], keys[1]);
                }
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void searchMenu(){
        Object[] keys = getOrgDstInput();
        switch (printOptions("BFS", "DFS")){
            case 1 -> {
                if(this.graph.bfsSearch(keys[0], keys[1])){
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

    private void subGraphMenu() {
        while (true) {
            switch (printOptions("Criar arvore geradora minima", "Pegar componentes", "Verificar clique", "Verificar clique Maximal", "Voltar")) {
                case 1 -> {
                    Graph minTree = graph.genMinimumSpanningTree();
                    System.out.println("Arvore criada, o menu de opcoes para ela ira aparecer agora");
                    continueMenu();
                    GraphMenu minTreeMenu = new GraphMenu(minTree);
                    minTreeMenu.run();
                    System.out.println("Voce saiu do menu da arvore criada, voltando para o grafo original...");
                }
                case 2 -> {
                    ArrayList<Graph> components = graph.getComponents();
                    for (Graph g : components) {
                        System.out.println("Componente do grafo principal encontrado, o menu de opcoes para ele ira aparecer agora...");
                        continueMenu();
                        GraphMenu menu = new GraphMenu(g);
                        menu.run();
                        System.out.println("Voce saiu do menu do componente criado...");
                    }
                    System.out.println("Voce saiu do menu de todos os componentes, voltando para o grafo original...");
                }
                case 3 -> {
                    System.out.println("Digite as vertices que estao dentro do clique:");
                    Object[] keys = getXNodesInput();
                    if(graph.isClique(keys)){
                        System.out.println("O grupo de vertices digitado é um clique!");
                    }else{
                        System.out.println("O grupo de vertices digitado não é um clique!");
                    }
                }
                case 4 -> {
                    System.out.println("Digite as vertices que estao dentro do clique:");
                    Object[] keys = getXNodesInput();
                    if(graph.isMaximalClique(keys)){
                        System.out.println("O grupo de vertices digitado é um clique!");
                    }else{
                        System.out.println("O grupo de vertices digitado não é um clique!");
                    }
                }
                case 5 -> {
                    return;
                }
            }
            continueMenu();
        }
    }

    private void continueMenu(){
        System.out.println("\nPrecione enter para continuar...");
        getInputString();
    }

    private Object[] getOrgDstInput(){
        Object[] objects = new Object[2];
        System.out.print("Digite o rotulo do node de origem: ");
        objects[0] = getInputString();
        System.out.print("Digite o rotulo do node de destino: ");
        objects[1] = getInputString();
        return objects;
    }

    private Object[] getXNodesInput(){
        ArrayList<Object> keys = new ArrayList<>();
        int count = 1;
        while (true){
            System.out.println("Digite o rotulo do vertice " + count + ": (vazio para continuar)");
            count++;
            String string = getInputString();
            if(string.isEmpty()) break;
            keys.add(string);
        }
        return keys.toArray();
    }

    private int printOptions(String... options){
        while (true){
            try {
                System.out.println("\nDigite uma opcao: ");
                for(int i = 0; i < options.length; i++){
                    System.out.println((i + 1) + " - " + options[i]);
                }
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

    private static int getInputInt(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("R: ");
        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }
    private static String getInputString(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("R: ");
        return scanner.nextLine();
    }
}
