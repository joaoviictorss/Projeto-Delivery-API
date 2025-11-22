# 🧪 Guia de Testes - Projeto Delivery API

Este documento descreve a estratégia de testes implementada no projeto e como executá-los.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Estrutura de Testes](#estrutura-de-testes)
- [Executando os Testes](#executando-os-testes)
- [Cobertura de Código](#cobertura-de-código)
- [Tipos de Testes](#tipos-de-testes)
- [Boas Práticas](#boas-práticas)

## 🎯 Visão Geral

O projeto implementa uma suíte completa de testes automatizados seguindo a pirâmide de testes:

```
        /\
       /  \      E2E Tests (Futuro)
      /____\
     /      \    Integration Tests
    /________\
   /          \  Unit Tests
  /____________\
```

### Estratégia de Testes

- **Testes Unitários**: Testam componentes isolados (Services) usando mocks
- **Testes de Integração**: Testam fluxos completos através dos controllers
- **Meta de Cobertura**: 80% de cobertura de código

## 📁 Estrutura de Testes

```
src/test/java/com/delivery_api/Projeto/Delivery/API/
├── service/
│   ├── ClienteServiceTest.java          # Testes unitários do ClienteService
│   └── PedidoServiceTest.java           # Testes unitários do PedidoService
├── controller/
│   ├── ClienteControllerIT.java         # Testes de integração do ClienteController
│   └── PedidoControllerIT.java         # Testes de integração do PedidoController
└── ProjetoDeliveryApiApplicationTests.java
```

## 🚀 Executando os Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes Específicos

```bash
# Testes unitários do ClienteService
mvn test -Dtest=ClienteServiceTest

# Testes unitários do PedidoService
mvn test -Dtest=PedidoServiceTest

# Testes de integração do ClienteController
mvn test -Dtest=ClienteControllerIT

# Testes de integração do PedidoController
mvn test -Dtest=PedidoControllerIT
```

### Executar com Perfil de Teste

```bash
mvn test -Dspring.profiles.active=test
```

### Executar e Gerar Relatório de Cobertura

```bash
mvn clean test jacoco:report
```

O relatório será gerado em: `target/site/jacoco/index.html`

## 📊 Cobertura de Código

### Configuração JaCoCo

O projeto utiliza o plugin JaCoCo para análise de cobertura:

- **Meta de Cobertura**: 80% de linhas
- **Relatório**: Gerado automaticamente após `mvn test`
- **Localização**: `target/site/jacoco/index.html`

### Verificar Cobertura

```bash
# Executar testes e gerar relatório
mvn clean test jacoco:report

# Verificar se a cobertura atinge a meta (80%)
mvn test jacoco:check
```

### Visualizar Relatório

Abra o arquivo `target/site/jacoco/index.html` no navegador para ver:
- Cobertura por pacote
- Cobertura por classe
- Linhas não cobertas
- Métricas detalhadas

## 🧪 Tipos de Testes

### 1. Testes Unitários

Testam componentes isolados usando mocks para dependências.

#### Exemplo: ClienteServiceTest

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;
    
    @InjectMocks
    private ClienteServiceImpl clienteService;
    
    @Test
    void deveCadastrarClienteComSucesso() {
        // Arrange, Act, Assert
    }
}
```

**Características:**
- Rápidos (executam em milissegundos)
- Isolados (não dependem de banco de dados)
- Usam mocks para dependências
- Testam lógica de negócio

### 2. Testes de Integração

Testam fluxos completos através da API usando banco H2 em memória.

#### Exemplo: ClienteControllerIT

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIT {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void deveCriarClienteComDadosValidos() throws Exception {
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }
}
```

**Características:**
- Testam fluxos completos
- Usam banco H2 em memória
- Validam requisições e respostas HTTP
- Testam integração entre camadas

## ✅ Cenários Testados

### ClienteServiceTest

✅ Cadastro de cliente com dados válidos  
✅ Validação de email duplicado  
✅ Atualização de cliente  
✅ Validação de dados obrigatórios  
✅ Tratamento de exceções  

### PedidoServiceTest

✅ Criação de pedido com dados válidos  
✅ Validação de cliente inexistente  
✅ Validação de restaurante inexistente  
✅ Validação de cliente inativo  
✅ Validação de restaurante inativo  
✅ Atualização de status do pedido  
✅ Validação de pedido já entregue  
✅ Listagem de pedidos por cliente  
✅ Listagem de pedidos por status  
✅ Listagem de pedidos por período  

### ClienteControllerIT

✅ POST /api/clientes - Criação com dados válidos  
✅ POST /api/clientes - Validação de dados inválidos  
✅ POST /api/clientes - Validação de email duplicado  
✅ GET /api/clientes - Listagem de clientes  
✅ GET /api/clientes/{id} - Busca por ID  
✅ PUT /api/clientes/{id} - Atualização  
✅ DELETE /api/clientes/{id} - Inativação  
✅ GET /api/clientes/buscar - Busca por nome  

### PedidoControllerIT

✅ POST /api/pedidos - Criação com dados válidos  
✅ POST /api/pedidos - Validação de cliente inexistente  
✅ POST /api/pedidos - Validação de restaurante inexistente  
✅ POST /api/pedidos - Validação de cliente inativo  
✅ POST /api/pedidos - Validação de restaurante inativo  
✅ GET /api/pedidos/{id} - Busca por ID  
✅ GET /api/pedidos - Listagem com filtros  
✅ PATCH /api/pedidos/{id}/status - Atualização de status  
✅ DELETE /api/pedidos/{id} - Cancelamento  
✅ GET /api/pedidos/clientes/{id}/pedidos - Histórico do cliente  
✅ GET /api/pedidos/restaurantes/{id}/pedidos - Pedidos do restaurante  
✅ POST /api/pedidos/calcular - Cálculo de total  

## 🎯 Boas Práticas

### 1. Nomenclatura de Testes

Use nomes descritivos que expliquem o comportamento testado:

```java
@Test
@DisplayName("Deve cadastrar cliente com sucesso quando dados são válidos")
void deveCadastrarClienteComSucesso() { }
```

### 2. Padrão AAA (Arrange-Act-Assert)

```java
@Test
void exemploTeste() {
    // Arrange - Preparar dados
    ClienteRequestDTO dto = new ClienteRequestDTO();
    dto.setNome("João");
    
    // Act - Executar ação
    ClienteResponseDTO resultado = service.cadastrar(dto);
    
    // Assert - Verificar resultado
    assertNotNull(resultado);
    assertEquals("João", resultado.getNome());
}
```

### 3. Isolamento entre Testes

- Use `@BeforeEach` para setup comum
- Use `@Transactional` em testes de integração para rollback automático
- Limpe dados entre testes quando necessário

### 4. Verificação de Comportamentos

```java
// Verificar que método foi chamado
verify(repository, times(1)).save(any(Cliente.class));

// Verificar que método NÃO foi chamado
verify(repository, never()).delete(any());
```

### 5. Testes de Exceções

```java
@Test
void deveLancarExcecaoQuandoEmailDuplicado() {
    when(repository.existsByEmail(email)).thenReturn(true);
    
    BusinessException exception = assertThrows(
        BusinessException.class, 
        () -> service.cadastrar(dto)
    );
    
    assertEquals("Email já cadastrado", exception.getMessage());
}
```

## 🔧 Configuração

### application-test.properties

O arquivo `src/main/resources/application-test.properties` configura:

- Banco H2 em memória para testes
- Logging reduzido
- Desabilitação de cache
- Isolamento de dados entre testes

### Dependências de Teste

As seguintes dependências estão configuradas no `pom.xml`:

- **JUnit 5**: Framework de testes
- **Mockito**: Criação de mocks
- **Spring Boot Test**: Suporte para testes Spring
- **JaCoCo**: Análise de cobertura

## 📈 Métricas de Qualidade

### Cobertura Atual

Execute `mvn test jacoco:report` para verificar:

- Cobertura de linhas
- Cobertura de branches
- Cobertura de métodos
- Cobertura de classes

### Meta de Cobertura

- **Mínimo**: 80% de cobertura de linhas
- **Ideal**: 90%+ para componentes críticos

## 🐛 Troubleshooting

### Testes Falhando

1. Verifique se o banco H2 está configurado corretamente
2. Confirme que o perfil `test` está ativo
3. Verifique logs em `application-test.properties`

### Cobertura Baixa

1. Identifique classes não testadas no relatório JaCoCo
2. Adicione testes para métodos não cobertos
3. Execute `mvn test jacoco:check` para validar meta

### Erros de Compilação

1. Execute `mvn clean install`
2. Verifique se todas as dependências estão corretas
3. Confirme que o JDK 21 está configurado

## 📚 Recursos Adicionais

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

## 🎓 Próximos Passos

Para melhorar ainda mais a qualidade dos testes:

1. ✅ Adicionar testes para ProdutoService e RestauranteService
2. ✅ Adicionar testes de integração para ProdutoController e RestauranteController
3. ✅ Implementar testes de performance
4. ✅ Adicionar testes de segurança
5. ✅ Configurar CI/CD para executar testes automaticamente

---

**Desenvolvido com ❤️ para garantir qualidade e confiabilidade do código**

