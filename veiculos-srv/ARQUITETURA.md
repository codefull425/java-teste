# ARQUITETURA

## ARQUITETURA DE BANCO DE DADOS

A modelagem do banco de dados se apoia no primeiro princípio SOLID:

**S — Single Responsibility Principle (princípio da responsabilidade única)**  
Separei as entidades mestras (veículo, pneu) da entidade de relacionamento (veiculo_pneu), principalmente porque isso distribui as responsabilidades das tabelas de acordo com o domínio explorado. Essa decisão está fortemente apoiada no primeiro fundamento do SOLID: cada tabela responde por um conceito de negócio distinto.

Uma boa separação de responsabilidades no banco de dados tende a produzir consultas mais diretas na camada de persistência (JPA/JPQL), o que ajuda na leitura do código por diferentes perfis da equipe — inclusive apoio a relatórios e exportações mais claros, pois cada tabela reflete um papel bem definido.

## Migrations Flyway (duas versões)

O Flyway executa os scripts em `src/main/resources/db/migration` em ordem (`V1`, depois `V2`). Hoje existem **duas** migrations, com responsabilidades distintas:

| Arquivo | Responsabilidade |
|---------|-------------------|
| **`V1__create_veiculo_pneu.sql`** | **Schema inicial (estrutura).** Cria as tabelas `veiculo`, `pneu` e `veiculo_pneu`, chaves estrangeiras, índices de apoio e **índices únicos parciais** em `veiculo_pneu` (`WHERE data_desvinculo IS NULL`) para garantir, no banco, que não há duas aplicações abertas na mesma posição do mesmo veículo nem o mesmo pneu com vínculo aberto em mais de um veículo. Não insere dados de negócio: só define o “esqueleto” alinhado ao modelo JPA (`ddl-auto: validate`). |
| **`V2__seed_data.sql`** | **Dados de exemplo (*seed*).** Popula veículos, pneus e registros em `veiculo_pneu` para desenvolvimento e testes manuais na API (incluindo cenários como pelo menos seis pneus distintos por veículo em aberto, `data_desvinculo` nula, e combinações pensadas para testes — por exemplo veículo com placa `ABC1D23` com posição **C** livre para exercitar `POST` de aplicar pneu). |

Separar **V1 (estrutura)** de **V2 (dados)** segue a mesma ideia de responsabilidade única: em um banco vazio o Flyway aplica primeiro o contrato do banco e, na sequência, o *seed*; evoluções futuras do schema podem vir como `V3__...`, `V4__...`, sem misturar criação de tabela com grandes blocos de `INSERT` no mesmo arquivo.

## ARQUITETURA DE PASTAS DO PROJETO

Adotei uma arquitetura em camadas, com separação explícita de responsabilidades entre as classes — novamente alinhada ao **S** do SOLID.

Essa organização inclui:

- **Repositórios:** acesso ao banco via Spring Data JPA (`VeiculoRepository`, `PneuRepository` para as tabelas mestras e `VinculoRepository` para `VeiculoPneu`, com consultas JPQL quando é preciso *fetch* ou filtros de negócio).
- **Controllers:** porta de entrada HTTP da API REST, verbos e rotas expostos ao cliente.
- **Services:** orquestração e regras de negócio (aplicar/remover pneu, listagens, detalhes).
- **DTOs:** contratos estáveis de *request* e *response* expostos pela API.
- **Mappers** (ex.: `VeiculoMapper`, `PneuMapper`, `ListagemAplicacaoPneuMapper`): traduzem entidades/domínio interno para o formato dos DTOs.

### Vantagens de um mapper dedicado

- **SRP:** o serviço fica mais enxuto (menos ruído de `builder()` e montagem de resposta).
- **Testes:** dá para testar mapeamentos de forma isolada, sem mockar repositório.
- **Reuso:** se outro fluxo precisar do mesmo formato de resposta, o mapper centraliza.

Na prática do mercado, essa combinação (camadas + controllers “finos”) costuma ser associada a ideias próximas do MVC adaptado a APIs: facilita manutenção e leitura e conversa bem com o SOLID — em especial **S** (responsabilidade única) e **O** (aberto/fechado): novas *features* tendem a surgir como novos métodos/serviços ou extensões pontuais sem reescrever o núcleo existente, desde que os contratos (DTOs) e os limites entre camadas sejam respeitados.

## DOCUMENTAÇÃO DA API (SWAGGER / OPENAPI)

O projeto usa **SpringDoc OpenAPI** (`springdoc-openapi-starter-webmvc-ui`), que integra **OpenAPI 3** ao Spring MVC e publica a **Swagger UI** para explorar e testar os endpoints sem ferramentas externas.

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` (porta padrão `8080`; ajuste se `SERVER_PORT` estiver definido).
- **Especificação JSON OpenAPI:** `http://localhost:8080/v3/api-docs`

Metadados básicos da API (título, descrição, versão) são definidos em `OpenApiConfig`. As rotas do Swagger e do documento OpenAPI ficam explicitamente liberadas em `SecurityConfig`, para que, se a API passar a exigir autenticação no futuro, a documentação interativa possa continuar acessível com ajustes mínimos.

## ENDPOINTS DA API

Base: `http://localhost:8080` (ou a porta configurada).

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/api/veiculos` | Lista todos os veículos **sem** pneus aplicados. |
| `POST` | `/api/veiculos` | Cadastra veículo (corpo: `placa`, `marca`, `quilometragemKm`, `status`). |
| `POST` | `/api/veiculos/{id}/aplicar-pneu` | Aplica pneu ao veículo na posição informada. |
| `POST` | `/api/veiculos/{id}/remover-pneu` | Remove pneu do veículo (vínculo em aberto). |
| `GET` | `/api/pneus` | Lista todos os pneus. |
| `POST` | `/api/pneus` | Cadastra pneu (`numeroFogo`, `marca`, `pressaoAtualPsi`, `status`). |
| `GET` | `/api/veiculosPneus` | Lista todas as aplicações (vínculos veículo–pneu–posição). |
| `GET` | `/api/veiculosPneus/{id}` | Detalhe do veículo **com** pneus aplicados (montagem atual). |

## DEPENDÊNCIAS PRINCIPAIS

- **spring-boot-starter-parent** (via `<parent>`): padroniza versões compatíveis do ecossistema Spring Boot, plugins e propriedades do projeto (não é uma dependência transitiva de código, mas define o *bill of materials* do Boot).

- **spring-boot-starter-data-jpa:** abstrai acesso ao banco com JPA/Hibernate (`Entity`, `Repository`, transações), alinhado ao uso de entidades e repositórios no projeto.

- **spring-boot-starter-web:** expõe a API REST (controllers, servlet stack embutida, serialização JSON com Jackson, cliente HTTP no servidor quando necessário).

- **spring-boot-starter-validation:** habilita Bean Validation (`jakarta.validation`) em DTOs e parâmetros (`@Valid`, `@NotNull`, etc.), coerente com requests validados na API.

- **spring-boot-starter-security:** filtro de segurança, CORS, política de sessão e configuração centralizada (`SecurityConfig`), mesmo quando as regras atuais são permissivas.

- **springdoc-openapi-starter-webmvc-ui:** documentação **OpenAPI 3** e **Swagger UI** dos endpoints REST; gera o JSON em `/v3/api-docs` e a interface em `/swagger-ui`.

- **postgresql** (runtime): driver JDBC oficial para conectar à base PostgreSQL em produção/desenvolvimento (URL em `application.yaml`).

- **flyway-core:** executa e versiona scripts SQL em `db/migration`, garantindo schema reproduzível e alinhado ao `ddl-auto: validate` do Hibernate.

- **flyway-database-postgresql:** módulo Flyway específico para PostgreSQL (dialeto/recursos do *vendor*), necessário para migrations corretas nesse SGBD.

- **lombok** (optional): reduz boilerplate (`@Getter`, `@Setter`, builders, etc.) nas entidades e DTOs.

## TESTES

- **spring-boot-starter-test** (test): JUnit 5, Mockito, AssertJ, `MockMvc`, etc., para testes unitários e de integração leve do Spring.

- **spring-security-test** (test): utilitários para testar segurança com `MockMvc` (ex.: `SecurityMockMvcRequestPostProcessors`) quando a API estiver protegida ou para validar a cadeia de filtros.

- **testcontainers-junit-jupiter** (test): integra containers Docker ao ciclo de testes (JUnit 5), útil para subir PostgreSQL real em testes de integração.

- **testcontainers-postgresql** (test): imagem/container PostgreSQL padronizada para esses testes, espelhando o ambiente de produção.

- **h2** (test): banco em memória para testes que não precisam de PostgreSQL (ex.: `@DataJpaTest` ou perfil de teste com H2), acelerando suites que não usam Testcontainers.
