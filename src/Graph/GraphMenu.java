package Graph;

import java.io.IOException;
import java.util.*;

public class GraphMenu {
    private final Graph graph;
    private String defSaveLocation = "src/Graph/SavedGraphs";
    public GraphMenu(Graph graph) {
        this.graph = graph;
    }
    public GraphMenu(){
        if (printOptions("Carregar grafo em um arquivo", "Gerar grafo aleatorio") == 1) {
            System.out.println("Digite o local do arquivo para carregar:");
            String input = getInputString();
            System.out.println("Carregando arquivo:");
            this.graph = Graph.loadFromFile(input);
        } else {
            System.out.println("O grafo vai ser direcionado(1) ou nao direcionado(0)?");
            if (getInputInt() == 1) {
                graph = new Graph(true);
            } else {
                graph = new Graph();
            }
            System.out.println("Digite a quantidade de nodes para adicionar:");
            int qntdV = getInputInt();
            for (int i = 0; i < qntdV; i++) {
                graph.add(i);
            }
            System.out.println("O grafo sera conexo?(0 - nao)(1 - sim)");
            Random rand = new Random();
            if(getInputInt() == 1){
                if(!graph.isDirected()){
                    Queue<Node<?>> queue = new LinkedList<>(graph.getNodesList());
                    while (!queue.isEmpty()){
                        Node<?> node = queue.poll();
                        for(Node<?> n : queue){
                            graph.newAdjacency(node.getValue(), n, rand.nextInt(0, 100));
                        }
                    }
                }else {
                    for(Node<?> n : graph.getNodes()){
                        for(Node<?> nodeToAdd : graph.getNodes()){
                            graph.newAdjacency(n.getValue(), nodeToAdd, rand.nextInt(1, 100));
                        }
                    }
                }

            }else{
                System.out.println("Digite a quantidade de arestas para adicionar:");
                int qntdA = getInputInt();
                for (int i = 0; i < qntdA; i++) {
                    int n1 = rand.nextInt(qntdV);
                    int n2 = rand.nextInt(qntdV);
                    if(!graph.newAdjacency(n1, n2, rand.nextInt(1, 100))){
                        i--; // repete caso nao foi possivel adicionar adjacencia
                    }
                }
            }
        }
    }

    public void setNewSaveLocation(String location){
        this.defSaveLocation = location;
    }

    public void run(){
        while (true){
            switch (printOptions("Editar grafo", "Busca", "Verificacoes", "Gerenciar Sub Grafos", "Print", "Salvar", "Sair")){
                case 1 -> editMenu();
                case 2 -> searchMenu();
                case 3 -> checksMenu();
                case 4 -> subGraphMenu();
                case 5 -> printMenu();
                case 6 -> saveGraphMenu();
                case 7 -> {
                    return;
                }
            }
        }
    }

    private void checksMenu(){
        System.out.println("Digite uma opcao para verificar se e real:");
        while (true){
            switch (printOptions("Conexo", "Euleriano", "Ciclico", "Contem node", "Sair")){
                case 1 -> {
                    if(graph.isConnected()){
                        System.out.println("O Grafo é conexo!");
                    }else {
                        System.out.println("O Grafo não é conexo!");
                    }
                }
                case 2 -> {
                    if(graph.isEulerian()){
                        System.out.println("O Grafo é euleriano!");
                    }else {
                        System.out.println("O Grafo não é euleriano!");
                    }
                }
                case 3 -> {
                    if(graph.isCyclic()){
                        System.out.println("O Grafo é ciclico!");
                    }else {
                        System.out.println("O Grafo não é ciclico!");
                    }
                }
                case 4 -> {
                    System.out.println("Digite o rotulo do node");
                    if(graph.get(getInputString()) == null){
                        System.out.println("O grafo nao contem este rotulo!");
                    }else {
                        System.out.println("O grafo contem este rotulo!");
                    }
                }
                case 5 -> {
                    return;
                }
            }
        }
    }
    private void saveGraphMenu(){
        System.out.println("Digite o local para salvar o arquivo (vazio para usar o local padrao)");
        String location = getInputString();
        if(location.isEmpty()) location = defSaveLocation;
        String name = null;
        if(!location.isEmpty()) {
            System.out.println("Digite o nome do arquivo:");
            name = getInputString();
        }
        try {
            if(graph.saveToFile(location, name, false)){
                System.out.println("Salvo com sucesso!");
            }else{
                System.out.println("Nao foi possivel salvar");
            }
        }catch (IOException e){
            System.out.println("Arquivo ja existe! Gostaria de substituilo? (0 - nao; 1 - sim)");
            if(getInputInt() == 1){
                try {
                    if(graph.saveToFile(location, name, true)){
                        System.out.println("Salvo com sucesso!");
                    }else{
                        System.out.println("Nao foi possivel salvar");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void printMenu(){
        while (true) {
            switch (printOptions(
                    "Print vertices", "Print arestas", "Print travessia",
                    "Print nodes a distancia X", "Print caminho mais curto", "Print caminho mais longo",
                    "Print centralidade de proximidade", "Print centralidade de intermediacao", "Voltar")) {
                case 1 -> {
                    System.out.println(Arrays.toString(graph.getNodes()));
                    System.out.println("O grafo tem " + graph.size() + " vertices!");
                }
                case 2 -> {
                    System.out.println("O grafo é " + (graph.isDirected() ? "direcionado!" : "nao direcionado!"));
                    for (Node<?> n : graph.getNodes()) {
                        System.out.print(n.toString() + " -> ");
                        for (Node.AdjacencyHolder adj : n.getAdjacencyHolders()) {
                            System.out.print("[" + adj.getNode() + ", peso: " + adj.getWeight() + "] ");
                        }
                        System.out.println();
                    }
                    System.out.println("O grafo tem " + graph.edgesCount() + " arestas!");
                }
                case 3 -> {
                    switch (printOptions("BFS", "DFS")) {
                        case 1 -> System.out.println(graph.getBfsTraversal());
                        case 2 -> System.out.println(graph.getDfsTraversal());
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
                    System.out.println("Digite o node para calcular:");
                    System.out.println(graph.getNodeBetwenessCentrality(getInputString()));
                }
                case 8 -> {
                    System.out.println("Digite o node para calcular:");
                    System.out.println(graph.getNodeClosenessCentrality(getInputString()));
                }
                case 9 -> {
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
        System.out.println("Digite uma opcao:");
        switch (printOptions("Buscar conectividade","Pegar todas adjacencias","Pegar node com maior N de adjacencias","Pegar node com menor N de adjacencias","Sair")){
            case 1->{
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
            }
            case 2 -> {
                String node = getInputString();
                System.out.print(node + " -> [ ");
                for(Object value : graph.getAdjacencies(node)){
                    System.out.print(value + " - p: " + graph.getWeight(node, value));
                }
                System.out.println("]");
            }
            case 3 -> {
                Object value = graph.getHighestNOfConnections();
                System.out.print(value);
                System.out.println(" com " + graph.getAdjacencies(value).size() + " adjacencias");
            }
            case 4 -> {
                Object value = graph.getLowestNOfConnections();
                System.out.print(value);
                System.out.println(" com " + graph.getAdjacencies(value).size() + " adjacencias");
            }
            case 5 -> {
                return;
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
                    if(components == null){
                        System.out.println("O grafo é conexo, portanto, nâo possui mais componentes!");
                    }else{
                        for (Graph g : components) {
                            System.out.println("Componente do grafo principal encontrado, o menu de opcoes para ele ira aparecer agora...");
                            continueMenu();
                            GraphMenu menu = new GraphMenu(g);
                            menu.run();
                            System.out.println("Voce saiu do menu do componente criado...");
                        }
                        System.out.println("Voce saiu do menu de todos os componentes, voltando para o grafo original...");
                    }
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
