import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class Diagrama { 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
     
        Clinica clinica = new Clinica();
        boolean executando = true;

        System.out.println("Bem-vindo ao Sistema de Atendimento Médico!");

        while (executando) {
            exibirMenu();
            int opcao = lerOpcao(scanner);

            switch (opcao) {
                case 1:
                    clinica.adicionarPaciente(scanner);
                    break;
                case 2:
                    clinica.atenderPaciente(scanner);
                    break;
                case 3:
                    clinica.mostrarFila();
                    break;
                case 4:
                    clinica.mostrarProximo();
                    break;
                case 5:
                    clinica.mostrarHistoricoGeral();
                    break;
                case 6:
                    clinica.mostrarHistoricoMedico(scanner);
                    break;
                case 0:
                    executando = false;
                    System.out.println("Encerrando o sistema. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            if (executando) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine(); 
            }
        }

        scanner.close();
    }

   
    private static void exibirMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Adicionar novo paciente (Triagem)");
        System.out.println("2. Chamar próximo paciente para atendimento");
        System.out.println("3. Exibir fila de espera");
        System.out.println("4. Ver quem é o próximo da fila");
        System.out.println("5. Exibir histórico de pacientes atendidos (Geral)");
        System.out.println("6. Exibir histórico de atendimentos por médico");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

   
    private static int lerOpcao(Scanner scanner) {
        int opcao = -1;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            
        }
        return opcao;
    }
}



enum Prioridade {
    VERMELHO(1, "Emergência (atendimento imediato)"),
    AMARELO(2, "Urgente (atendimento em até 20 min)"),
    VERDE(3, "Pouco urgente (atendimento em até 120 min)"),
    AZUL(4, "Não urgente (atendimento em até 3 horas)"); 
    private final int nivel;
    private final String descricao;

    Prioridade(int nivel, String descricao) {
        this.nivel = nivel;
        this.descricao = descricao;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.name() + " - " + this.descricao;
    }
}


class Paciente implements Comparable<Paciente> {
    
    private String nome;
    private String cpf;
    private Prioridade prioridade;
    private LocalDateTime horaChegada;
    private LocalDateTime horaAtendimento;
    private Medico medicoAtendeu;

   
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Paciente(String nome, String cpf, Prioridade prioridade) {
        this.nome = nome;
        this.cpf = cpf;
        this.prioridade = prioridade;
        this.horaChegada = LocalDateTime.now(); 
    }


    public String getNome() { return nome; }
    public Prioridade getPrioridade() { return prioridade; }
    public LocalDateTime getHoraChegada() { return horaChegada; }
    public Medico getMedicoAtendeu() { return medicoAtendeu; }
    
 
    public void setHoraAtendimento(LocalDateTime horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }
    public void setMedicoAtendeu(Medico medicoAtendeu) {
        this.medicoAtendeu = medicoAtendeu;
    }

   
    @Override
    public int compareTo(Paciente outro) {
 
        int comparacaoPrioridade = Integer.compare(this.prioridade.getNivel(), outro.prioridade.getNivel());
        
        if (comparacaoPrioridade == 0) {
      
            return this.horaChegada.compareTo(outro.horaChegada);
        }
        
        return comparacaoPrioridade;
    }

    @Override
    public String toString() {
        String info = String.format("[%s] %s (Chegada: %s)",
                this.prioridade.name(),
                this.nome,
                this.horaChegada.format(FORMATTER));

        if (this.horaAtendimento != null) {
            info += String.format(" - Atendido: %s por Dr(a). %s",
                    this.horaAtendimento.format(FORMATTER),
                    this.medicoAtendeu.getNome());
        }
        return info;
    }
}


class Medico {
    private String nome;
    private String crm;
    private List<Paciente> pacientesAtendidos;

    public Medico(String nome, String crm) {
        this.nome = nome;
        this.crm = crm;
        this.pacientesAtendidos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getCrm() {
        return crm;
    }

    public List<Paciente> getPacientesAtendidos() {
        return pacientesAtendidos;
    }


    public void atenderPaciente(Paciente paciente) {
        this.pacientesAtendidos.add(paciente);
    }

    @Override
    public String toString() {
        return "Dr(a). " + nome + " (CRM: " + crm + ")";
    }
}


class Triagem {

  
    public static Prioridade classificar(Scanner scanner) {
        System.out.println("--- Início da Triagem ---");
        System.out.println("Responda com 's' (sim) ou 'n' (não).");

  
        if (fazerPergunta("O paciente apresenta risco de vida imediato? (Inconsciente, sangramento incontrolável, dificuldade extrema de respirar)", scanner)) {
            return Prioridade.VERMELHO;
        }

        if (fazerPergunta("O paciente apresenta um sintoma de alta urgência? (Dor no peito, confusão mental súbita, dor abdominal severa)", scanner)) {
            return Prioridade.AMARELO;
        }


        if (fazerPergunta("O paciente apresenta um sintoma de urgência moderada? (Febre alta persistente, vômito que não cessa, dor moderada)", scanner)) {
            return Prioridade.VERDE;
        }

  
        if (fazerPergunta("O caso é um sintoma leve ou crônico? (Resfriado, dor de garganta, renovação de receita)", scanner)) {
            return Prioridade.AZUL;
        }

        System.out.println("Não foi possível classificar. Classificado como 'Não Urgente' por padrão.");
        return Prioridade.AZUL;
    }

    private static boolean fazerPergunta(String pergunta, Scanner scanner) {
        System.out.println(pergunta);
        while (true) {
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (resposta.equals("s")) {
                return true;
            } else if (resposta.equals("n")) {
                return false;
            } else {
                System.out.println("Resposta inválida. Digite 's' ou 'n'.");
            }
        }
    }
}


class Clinica {

 
    private PriorityQueue<Paciente> filaEspera;
    private List<Paciente> historicoAtendidos;
    private List<Medico> medicos;

    public Clinica() {
        this.filaEspera = new PriorityQueue<>();
        this.historicoAtendidos = new ArrayList<>();
        this.medicos = new ArrayList<>();
        
    
        carregarMedicosIniciais();
        carregarPacientesIniciais();
    }

 
    private void carregarMedicosIniciais() {
        medicos.add(new Medico("Ana Souza", "12345-SP"));
        medicos.add(new Medico("Bruno Costa", "67890-RJ"));
        medicos.add(new Medico("Carla Dias", "11223-MG"));
    }


    private void carregarPacientesIniciais() {
        System.out.println("Carregando pacientes iniciais...");
        String[] nomes = {"Miguel", "Arthur", "Gael", "Théo", "Heitor", "Ravi", "Davi", "Bernardo", "Noah", "Gabriel", "Helena", "Alice", "Laura", "Maria Alice", "Sophia", "Manuela", "Maitê", "Liz", "Cecília", "Isabella"};
        Random random = new Random();
        Prioridade[] prioridades = Prioridade.values();

        for (int i = 0; i < 20; i++) {
            String nome = nomes[i];
            String cpf = String.format("%03d.%03d.%03d-%02d", random.nextInt(999), random.nextInt(999), random.nextInt(999), random.nextInt(99));
            Prioridade p = prioridades[random.nextInt(prioridades.length)]; // Prioridade aleatória
            
            Paciente paciente = new Paciente(nome, cpf, p);
      
            paciente.setHoraAtendimento(LocalDateTime.now().minusMinutes(random.nextInt(180)));
            
            filaEspera.add(paciente);
        }
        System.out.println("Pacientes carregados.");
    }


    public void adicionarPaciente(Scanner scanner) {
        System.out.print("Nome do paciente: ");
        String nome = scanner.nextLine();
        System.out.print("CPF do paciente: ");
        String cpf = scanner.nextLine();

        Prioridade prioridade = Triagem.classificar(scanner);
        Paciente paciente = new Paciente(nome, cpf, prioridade);
        
        filaEspera.add(paciente);
        
        System.out.printf("Paciente %s adicionado à fila com prioridade %s.\n", nome, prioridade.name());
    }

 
    public void mostrarProximo() {
        if (filaEspera.isEmpty()) {
            System.out.println("Fila de espera vazia.");
        } else {
            System.out.println("Próximo paciente a ser atendido:");
            System.out.println(filaEspera.peek()); // peek() apenas espia o primeiro
        }
    }

   
    public void mostrarFila() {
        if (filaEspera.isEmpty()) {
            System.out.println("Fila de espera vazia.");
            return;
        }

        System.out.println("--- Fila de Espera Atual ---");
      
        PriorityQueue<Paciente> copiaFila = new PriorityQueue<>(filaEspera);
        
        int i = 1;
        while (!copiaFila.isEmpty()) {
            System.out.println(i + ". " + copiaFila.poll());
            i++;
        }
        System.out.println("-----------------------------");
    }

    public void atenderPaciente(Scanner scanner) {
        if (filaEspera.isEmpty()) {
            System.out.println("Não há pacientes na fila para atender.");
            return;
        }

 
        Medico medico = selecionarMedico(scanner);
        if (medico == null) {
            System.out.println("Atendimento cancelado.");
            return;
        }

    
        Paciente paciente = filaEspera.poll();
        
     
        paciente.setHoraAtendimento(LocalDateTime.now());
        paciente.setMedicoAtendeu(medico);

      
        medico.atenderPaciente(paciente);

       
        historicoAtendidos.add(paciente);

        System.out.println("--- Atendimento Realizado ---");
        System.out.println("Paciente: " + paciente.getNome());
        System.out.println("Atendido por: " + medico.getNome());
        System.out.println("Hora da Chegada: " + paciente.getHoraChegada().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("Hora do Atendimento: " + paciente.getHoraAtendimento().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    
    private Medico selecionarMedico(Scanner scanner) {
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico cadastrado. Cadastre um médico primeiro.");
            return null;
        }

        System.out.println("Selecione o médico para o atendimento:");
        for (int i = 0; i < medicos.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), medicos.get(i).toString());
        }

        int escolha = -1;
        while (escolha < 1 || escolha > medicos.size()) {
            System.out.print("Digite o número do médico: ");
            try {
                escolha = Integer.parseInt(scanner.nextLine());
                if (escolha < 1 || escolha > medicos.size()) {
                    System.out.println("Escolha inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
        return medicos.get(escolha - 1);
    }

   
    public void mostrarHistoricoMedico(Scanner scanner) {
        Medico medico = selecionarMedico(scanner);
        if (medico == null) {
            return;
        }

        System.out.println("--- Histórico de Atendimento: " + medico.getNome() + " ---");
        List<Paciente> historico = medico.getPacientesAtendidos();
        if (historico.isEmpty()) {
            System.out.println("Este médico ainda não atendeu nenhum paciente.");
        } else {
            for (Paciente p : historico) {
                System.out.println(p);
            }
        }
        System.out.println("--------------------------------------------------");
    }

    public void mostrarHistoricoGeral() {
        System.out.println("--- Histórico Geral de Pacientes Atendidos ---");
        if (historicoAtendidos.isEmpty()) {
            System.out.println("Nenhum paciente foi atendido ainda.");
        } else {
            for (Paciente p : historicoAtendidos) {
                System.out.println(p);
            }
        }
        System.out.println("----------------------------------------------");
    }
}