package com.example.iaassessment.bootstrap;

import com.example.iaassessment.entity.*;
import com.example.iaassessment.repository.*;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, FormRepository formRepository, QuestionRepository questionRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.formRepository = formRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (RoleName rn : RoleName.values()) {
            roleRepository.findByName(rn).orElseGet(() -> {
                RoleEntity r = new RoleEntity(); r.setName(rn); return roleRepository.save(r);
            });
        }
        seedForms();
        seedUsers();
    }

    /**
     * Cria automaticamente dois usuários de exemplo:
     * - user@local: sem nenhuma role, portanto só tem acesso ao Formulário 1.
     * - admin@local: com a role ADMIN, tendo acesso ao Formulário 1 e ao Formulário 2,
     *   além do dashboard administrativo.
     */
    private void seedUsers() {
        RoleEntity adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();

        if (!userRepository.existsByEmail("user@local")) {
            UserEntity user = new UserEntity();
            user.setName("Usuário Padrão");
            user.setEmail("user@local");
            user.setPassword(passwordEncoder.encode("user123"));
            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("admin@local")) {
            UserEntity admin = new UserEntity();
            admin.setName("Admin");
            admin.setEmail("admin@local");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }

    private void seedForms() {
        if (formRepository.findByCode("FORM1").isEmpty()) {
            FormEntity f = new FormEntity();
            f.setCode("FORM1");
            f.setTitle("Diagnóstico Geral de Prontidão em IA");
            f.setDescription("Para toda a equipe — qualquer pessoa autenticada pode responder.");
            f.setAllowedRoles(List.of());
            formRepository.save(f);
            addForm1Questions(f);
        }
        if (formRepository.findByCode("FORM2").isEmpty()) {
            FormEntity f = new FormEntity();
            f.setCode("FORM2");
            f.setTitle("Diagnóstico Técnico de Prontidão em IA");
            f.setDescription("Restrito a usuários com a role ADMIN.");
            f.setAllowedRoles(List.of(RoleName.ADMIN));
            formRepository.save(f);
            addForm2Questions(f);
        }
    }

    private void addForm1Questions(FormEntity form) {
        int order=1;
        List<QuestionEntity> questions = new ArrayList<>();
        questions.add(likert(form, order++, "m1q1", "Existe uma direção declarada da alta liderança sobre o papel da IA no futuro da companhia.", "Organização (Estratégia e Direção)", null, false));
        questions.add(likert(form, order++, "m1q2", "A liderança patrocina ativamente as iniciativas de IA, de modo que elas se conectam entre si em vez de ficarem isoladas.", "Organização (Estratégia e Direção)", null, false));
        questions.add(likert(form, order++, "m1q3", "A liderança sênior tem clareza de como a IA acelera as top metas estratégicas do ano.", "Organização (Estratégia e Direção)", null, false));
        questions.add(likert(form, order++, "m1q4", "Na prática, as decisões sobre IA acontecem de forma reativa e dispersa, sem um direcionamento central.", "Organização (Estratégia e Direção)", null, true));
        questions.add(ladder(form, order++, "m1q5", "Qual frase melhor descreve o momento da liderança em relação à IA?", "Organização (Estratégia e Direção)", List.of("Resiste ou ignora o tema.", "Quer usar, mas não sabe por onde começar.", "Cobra o uso, mas não dá direcionamento nem orçamento.", "Alinhada, com estratégia clara de desdobramento.")));
        questions.add(open(form, order++, "m1q6", "Quais as 3 principais iniciativas/metas que hoje tiram o sono do board/CEO?", "Organização (Estratégia e Direção)"));

        questions.add(likert(form, order++, "m2q1", "Nossos rituais e reuniões abrem espaço intencional para discutir experimentos com IA — o que funcionou e o que falhou.", "Times (Processos, Rituais e Colaboração)", null, false));
        questions.add(likert(form, order++, "m2q2", "Os times usam IA de forma intencional para resolver problemas que impactam o cliente, não apenas para experimentar ferramentas.", "Times (Processos, Rituais e Colaboração)", null, false));
        questions.add(likert(form, order++, "m2q3", "Conseguimos medir e acompanhar o ganho de produtividade ou o impacto das ferramentas de IA adotadas.", "Times (Processos, Rituais e Colaboração)", null, false));
        questions.add(likert(form, order++, "m2q4", "O uso de IA nos times é pontual e depende de poucos entusiastas.", "Times (Processos, Rituais e Colaboração)", null, true));
        questions.add(ladder(form, order++, "m2q5", "Como o conhecimento sobre IA circula entre os departamentos?", "Times (Processos, Rituais e Colaboração)", List.of("Não circula; cada um faz o seu.", "Restrito a poucos entusiastas isolados.", "TI centraliza e dita as regras.", "Troca fluida, rituais compartilhados, aprendizado em rede.")));
        questions.add(open(form, order++, "m2q6", "Qual é o ritual/processo mais manual e repetitivo que, se otimizado por IA, liberaria o time para a estratégia?", "Times (Processos, Rituais e Colaboração)"));

        questions.add(likert(form, order++, "m3q1", "Os colaboradores demonstram curiosidade e iniciativa própria para adotar IA no dia a dia.", "Pessoas (Crenças, Medos e Hábitos)", null, false));
        questions.add(likert(form, order++, "m3q2", "Há clareza de que a IA veio para potencializar o trabalho humano, não para substituir postos.", "Pessoas (Crenças, Medos e Hábitos)", null, false));
        questions.add(likert(form, order++, "m3q3", "A empresa oferece um plano estruturado de desenvolvimento para preparar as pessoas — não apenas cobra o uso.", "Pessoas (Crenças, Medos e Hábitos)", null, false));
        questions.add(likert(form, order++, "m3q4", "Muitas pessoas ainda veem a IA como ameaça ao próprio emprego e evitam usá-la.", "Pessoas (Crenças, Medos e Hábitos)", null, true));
        questions.add(ladder(form, order++, "m3q5", "Onde a empresa está na preparação de pessoas para a era da IA?", "Pessoas (Crenças, Medos e Hábitos)", List.of("Sem ação estruturada; esforço individual.", "Ações pontuais, sem trilhas nem novos papéis.", "Trilhas em construção e revisão de cargos.", "Desenvolvimento, papéis e atração já adaptados.")));
        questions.add(singleUnscored(form, order++, "m3q6", "Qual é hoje seu maior desafio de talentos? (diagnóstico — não pontuado)", "Pessoas (Crenças, Medos e Hábitos)", List.of("Atrair/contratar quem domine IA", "Redefinir papéis e descritivos de cargo", "Upskilling em soft skills para ambiguidade", "Já estamos adaptados")));
        questions.add(open(form, order++, "m3q7", "Quais crenças ou “mitos” sobre IA ainda travam a evolução cultural da empresa?", "Pessoas (Crenças, Medos e Hábitos)"));

        questions.add(section("a1", "Seu contato com ferramentas de IA generativa (ChatGPT, Copilot, Gemini, Claude):", "Literacia e Oportunidade Individual", List.of("Nunca ouvi falar", "Já ouvi, nunca usei", "Testei poucas vezes", "Uso de vez em quando", "Uso com frequência no trabalho"), QuestionType.LADDER, order++));
        questions.add(section("a2", "Com que frequência usa IA no trabalho hoje?", "Literacia e Oportunidade Individual", List.of("Nunca", "Algumas vezes por mês", "Semanalmente", "Diariamente"), QuestionType.LADDER, order++));
        questions.add(sectionMulti(form, order++, "a3", "Para quê você já usou IA? (marque todas)", "Literacia e Oportunidade Individual", List.of("Escrever/revisar textos", "Resumir documentos", "Gerar ideias", "Planilhas/dados", "Traduzir", "Apresentações/materiais", "Buscar informação", "Programar"), "Ainda não usei"));
        questions.add(likert(form, order++, "a4", "Sei descrever bem o que quero para a IA e reformular quando o resultado não vem bom.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "a5", "Confiro o resultado da IA antes de usar em algo importante.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "a6", "Sei que a IA pode “inventar” informação com aparência de certeza, e levo isso em conta.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "a7", "Sei quais informações não devo inserir em ferramentas de IA (dados sigilosos, pessoais, de clientes).", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "a8", "Entendo em que tipos de tarefa a IA ajuda mais e em quais não é confiável.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "a9", "Costumo apresentar resultados da IA sem conferir, mesmo quando não entendo bem como ela chegou neles.", "Literacia e Oportunidade Individual", null, true));

        questions.add(objective(form, order++, "c1", "O que significa a IA “alucinar”?", "Literacia e Oportunidade Individual", List.of("Ficar mais lenta", "Gerar informação falsa com aparência de verdadeira", "Recusar-se a responder", "Precisar de internet"), 1));
        questions.add(objective(form, order++, "c2", "Forma mais confiável de reduzir erros factuais numa resposta de IA:", "Literacia e Oportunidade Individual", List.of("Pedir com mais educação", "Repetir a mesma pergunta", "Conferir em fonte confiável e pedir que cite fontes", "Usar letras maiúsculas"), 2));
        questions.add(objective(form, order++, "c3", "Qual informação NÃO deve ser inserida numa ferramenta de IA pública?", "Literacia e Oportunidade Individual", List.of("Um texto genérico de marketing", "Dados pessoais/sigilosos de clientes", "Uma dúvida de português", "Um resumo público da empresa"), 1));
        questions.add(objective(form, order++, "c4", "O que mais melhora a qualidade da resposta da IA?", "Literacia e Oportunidade Individual", List.of("Um pedido claro, com contexto e exemplos", "Um pedido bem curto", "Escrever tudo em inglês", "Enviar várias vezes"), 0));
        questions.add(objective(form, order++, "c5", "Sobre as respostas da IA generativa, é correto afirmar:", "Literacia e Oportunidade Individual", List.of("São sempre verdadeiras e atualizadas", "Podem errar e devem ser verificadas", "Nunca inventam nada", "Substituem qualquer especialista"), 1));
        questions.add(objective(form, order++, "c6", "Para uma tarefa repetitiva e de regras fixas, normalmente a melhor escolha é:", "Literacia e Oportunidade Individual", List.of("Sempre IA generativa", "Automação/regras quando os passos são fixos; IA quando exige linguagem/julgamento", "Nunca automatizar", "Fazer sempre à mão"), 1));

        questions.add(likert(form, order++, "b1", "Boa parte do meu tempo vai para tarefas repetitivas e previsíveis.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b2", "Meu trabalho envolve escrever, revisar ou responder muitos textos e e-mails.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b3", "Preciso ler e resumir muitos documentos ou relatórios.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b4", "Trabalho bastante com planilhas, dados ou números.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b5", "Passo parte do meu tempo procurando ou consultando informações em arquivos, planilhas ou fontes digitais (documentos, planilhas, e-mails, sistemas, intranet, web).", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b6", "Crio apresentações, propostas, materiais ou conteúdo.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b7", "Respondo perguntas parecidas de clientes ou colegas com frequência.", "Literacia e Oportunidade Individual", null, false));
        questions.add(likert(form, order++, "b8", "Tarefas operacionais consomem meu dia e sobra pouco tempo para o estratégico.", "Literacia e Oportunidade Individual", null, false));
        questions.add(open(form, order++, "b9a", "Qual tarefa da sua rotina é mais repetitiva, manual ou demorada — algo que você faz seguindo quase sempre os mesmos passos?", "Literacia e Oportunidade Individual"));
        questions.add(ladder(form, order++, "b9b", "Essa tarefa segue passos previsíveis, que poderiam virar uma receita/checklist?", "Literacia e Oportunidade Individual", List.of("Não — cada caso é diferente, exige julgamento.", "Em parte — há padrão, mas com muitas exceções.", "Bastante — padrão claro com poucas exceções.", "Totalmente — mesmos passos, sempre iguais.")));

        questionRepository.saveAll(questions);
    }

    private void addForm2Questions(FormEntity form) {
        int order=1; List<QuestionEntity> q = new ArrayList<>();
        q.add(ladder(form, order++, "q41", "Onde moram os dados da empresa e quão acessíveis eles são para a IA?", "Dados & Infraestrutura", List.of("Dispersos em silos e planilhas sem integração.", "Parcialmente consolidados, acesso manual.", "Centralizados, mas não utilizáveis pela IA.", "Centralizados e acessíveis à IA por interfaces/APIs governadas.")));
        q.add(likert(form, order++, "q42", "Conseguimos avaliar, caso a caso, se a IA tem acurácia suficiente para a tarefa — internamente ou com apoio especializado.", "Capacidade técnica & validação", null, false));
        q.add(likert(form, order++, "q43", "Avaliamos criticamente o output da IA e só usamos/entregamos resultados que compreendemos e sabemos explicar e defender.", "Capacidade técnica & validação", null, false));
        q.add(likert(form, order++, "q44", "Cada processo automatizado tem um responsável humano definido, que responde pelos resultados e por erros.", "Governança & conformidade", null, false));
        q.add(likert(form, order++, "q45", "Conhecemos as exigências de privacidade do nosso setor (incl. LGPD) e as ferramentas de IA que usamos as respeitam.", "Governança & conformidade", null, false));
        q.add(ladder(form, order++, "q46", "Qual é a orientação estratégica predominante da adoção de IA?", "Estratégia & ROI", List.of("Só corte de custos.", "Sobretudo custos.", "Equilíbrio entre eficiência e crescimento.", "Orientada a crescimento e inovação.")));
        q.add(likert(form, order++, "q47", "Medimos o custo e a acurácia atuais dos processos manuais (linha de base), o que permite estimar o impacto da IA quantitativamente.", "Estratégia & ROI", null, false));
        q.add(likert(form, order++, "q48", "Adotamos ferramentas de IA principalmente pelo preço da assinatura, sem avaliar complexidade, frequência de uso, privacidade e integração com a stack.", "Estratégia & ROI", null, true));
        q.add(open(form, order++, "q49", "Qual é hoje o maior obstáculo técnico ou de dados para usar IA na empresa?", "Aberta"));
        questionRepository.saveAll(q);
    }

    private QuestionEntity base(FormEntity form, int order, String key, String label, String section, QuestionType type) {
        QuestionEntity q = new QuestionEntity(); q.setForm(form); q.setDisplayOrder(order); q.setQuestionKey(key); q.setLabel(label); q.setSectionTitle(section); q.setType(type); q.setRequired(type != QuestionType.OPEN); return q;
    }
    private QuestionEntity likert(FormEntity form,int order,String key,String label,String section,String help,boolean rev){QuestionEntity q=base(form,order,key,label,section,QuestionType.LIKERT);q.setReversed(rev);q.setHelpText(help);return q;}
    private QuestionEntity ladder(FormEntity form,int order,String key,String label,String section,List<String> options){QuestionEntity q=base(form,order,key,label,section,QuestionType.LADDER);q.getOptions().addAll(options);return q;}
    private QuestionEntity open(FormEntity form,int order,String key,String label,String section){return base(form,order,key,label,section,QuestionType.OPEN);}
    private QuestionEntity singleUnscored(FormEntity form,int order,String key,String label,String section,List<String> options){QuestionEntity q=base(form,order,key,label,section,QuestionType.SINGLE_UNSCORED);q.getOptions().addAll(options);return q;}
    private QuestionEntity objective(FormEntity form,int order,String key,String label,String section,List<String> options,int correct){QuestionEntity q=base(form,order,key,label,section,QuestionType.OBJECTIVE);q.getOptions().addAll(options);q.setCorrectOptionIndex(correct);return q;}
    private QuestionEntity section(String key,String label,String section,List<String> options,QuestionType type,int order){QuestionEntity q=new QuestionEntity(); q.setForm(formRepository.findByCode("FORM1").orElseThrow()); q.setQuestionKey(key);q.setLabel(label);q.setSectionTitle(section);q.setType(type);q.setDisplayOrder(order);q.getOptions().addAll(options);return q;}
    private QuestionEntity sectionMulti(FormEntity form,int order,String key,String label,String section,List<String> options,String none){QuestionEntity q=base(form,order,key,label,section,QuestionType.MULTI_CHECK);q.getOptions().addAll(options);q.getOptions().add(none);return q;}
}
