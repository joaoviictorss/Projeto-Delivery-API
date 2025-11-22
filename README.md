# Delivery Tech API

Sistema de delivery desenvolvido com Spring Boot e Java 21.

## 🚀 Tecnologias
- **Java 21 LTS** (versão mais recente)
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL / H2 Database
- Maven
- ModelMapper
- Swagger/OpenAPI
- JWT (JSON Web Tokens)

## ⚡ Recursos Modernos Utilizados
- Records (Java 14+)
- Text Blocks (Java 15+)
- Pattern Matching (Java 17+)
- Virtual Threads (Java 21)

## 🏃‍♂️ Como executar

### Pré-requisitos
- JDK 21 instalado
- Maven 3.6+
- MySQL 8.0+ (opcional, pode usar H2)

### Executar a aplicação
```bash
# Usando Maven Wrapper
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

### Acessar a aplicação
- API: http://localhost:8080
- Health Check: http://localhost:8080/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## 🧪 Testes

O projeto possui uma suíte completa de testes automatizados:

### Executar todos os testes
```bash
mvn test
```

### Executar testes específicos
```bash
# Testes unitários
mvn test -Dtest=ClienteServiceTest
mvn test -Dtest=PedidoServiceTest

# Testes de integração
mvn test -Dtest=ClienteControllerIT
mvn test -Dtest=PedidoControllerIT
```

### Gerar relatório de cobertura
```bash
mvn clean test jacoco:report
```

O relatório estará disponível em: `target/site/jacoco/index.html`

📖 **Para mais detalhes sobre testes, consulte [TESTES.md](./TESTES.md)**

## 📋 Endpoints Principais

### Clientes
- `POST /api/clientes` - Cadastrar cliente
- `GET /api/clientes` - Listar clientes
- `GET /api/clientes/{id}` - Buscar cliente por ID
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Inativar cliente
- `GET /api/clientes/buscar?nome={nome}` - Buscar por nome

### Pedidos
- `POST /api/pedidos` - Criar pedido
- `GET /api/pedidos` - Listar pedidos
- `GET /api/pedidos/{id}` - Buscar pedido por ID
- `PATCH /api/pedidos/{id}/status` - Atualizar status
- `DELETE /api/pedidos/{id}` - Cancelar pedido
- `GET /api/pedidos/clientes/{id}/pedidos` - Histórico do cliente

### Produtos
- `POST /api/produtos` - Cadastrar produto
- `GET /api/produtos` - Listar produtos
- `GET /api/produtos/{id}` - Buscar produto por ID
- `PUT /api/produtos/{id}` - Atualizar produto
- `DELETE /api/produtos/{id}` - Remover produto

### Restaurantes
- `POST /api/restaurantes` - Cadastrar restaurante
- `GET /api/restaurantes` - Listar restaurantes
- `GET /api/restaurantes/{id}` - Buscar restaurante por ID
- `PUT /api/restaurantes/{id}` - Atualizar restaurante
- `GET /api/restaurantes/relatorio-vendas` - Relatório de vendas

## 🔧 Configuração

### application.properties
- Porta: 8080
- Banco: MySQL (configurável para H2)
- Profile: development

### Variáveis de Ambiente
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/delivery_db
spring.datasource.username=root
spring.datasource.password=123456
```

## 📦 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/delivery_api/Projeto/Delivery/API/
│   │       ├── config/          # Configurações (ModelMapper, Swagger)
│   │       ├── controller/      # Controllers REST
│   │       ├── dto/             # DTOs (Request/Response)
│   │       ├── entity/          # Entidades JPA
│   │       ├── enums/           # Enumeradores
│   │       ├── exceptions/      # Tratamento de exceções
│   │       ├── projection/      # Projeções JPA
│   │       ├── repository/     # Repositories JPA
│   │       └── service/         # Services e implementações
│   └── resources/
│       ├── application.properties
│       ├── application-test.properties
│       ├── schema.sql
│       └── data.sql
└── test/
    └── java/
        └── com/delivery_api/Projeto/Delivery/API/
            ├── service/          # Testes unitários
            └── controller/       # Testes de integração
```

## 🐳 Docker

### Construir imagem
```bash
docker build -t delivery-api:latest .
```

### Executar container
```bash
docker run -p 8080:8080 delivery-api:latest
```

## 📊 Qualidade de Código

- ✅ Testes automatizados (Unitários e Integração)
- ✅ Cobertura de código (meta: 80%)
- ✅ Validação de dados com Bean Validation
- ✅ Tratamento de exceções global
- ✅ Documentação Swagger/OpenAPI
- ✅ Padrões de código limpo

## 👨‍💻 Desenvolvedor
[Seu Nome] - [Sua Turma]  
Desenvolvido com JDK 21 e Spring Boot 3.5.7

## 📚 Documentação Adicional

- [Guia de Testes](./TESTES.md) - Documentação completa sobre testes
- Swagger UI: http://localhost:8080/swagger-ui.html
