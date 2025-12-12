import org.junit.jupiter.api.*;
import senai.database.Conexao;
import senai.model.Fornecedor;
import senai.service.fornecedor.FornecedorService;
import senai.service.fornecedor.FornecedorServiceImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste de Integração - EquipamentoService com Banco Real (Teste)")
public class FornecedorServiceIntegrationTest {

    private FornecedorService fornecedorService;


    private static final String SQL_CREATE_TABLE =
            """
            CREATE TABLE Fornecedor (
              id INT PRIMARY KEY AUTO_INCREMENT,
              nome VARCHAR(100) NOT NULL,
              cnpj VARCHAR(14) UNIQUE NOT NULL
            );
            """;


    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS Fornecedor;";


    private static final String SQL_TRUNCATE_TABLE = "TRUNCATE TABLE Fornecedor;";


    @BeforeAll
    static void setupDatabase() throws Exception {
        // 1. Conecta ao banco de TESTE
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            // 2. Destrói a tabela (caso exista de um teste anterior falho)
            stmt.execute(SQL_DROP_TABLE);

            // 3. Cria a tabela
            stmt.execute(SQL_CREATE_TABLE);

            System.out.println("Tabela 'Fornecedor' criada no banco de teste.");

        } catch (Exception e) {
            System.err.println("Erro ao configurar o banco de teste (BeforeAll)");
            e.printStackTrace();
            throw e; // Falha o setup se não conseguir criar a tabela
        }
    }

    @AfterAll
    static void tearDownDatabase() throws Exception {
        // 4. Destrói a tabela ao final de TODOS os testes
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_DROP_TABLE);
            System.out.println("Tabela 'Fornecedor' destruída.");

        } catch (Exception e) {
            System.err.println("Erro ao limpar o banco de teste (AfterAll)");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupTest() throws Exception {
        // 5. Limpa os dados da tabela ANTES de cada teste
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_TRUNCATE_TABLE);

        } catch (Exception e) {
            System.err.println("Erro ao limpar a tabela (BeforeEach)");
            e.printStackTrace();
        }

        fornecedorService = new FornecedorServiceImpl();
    }

    @Test
    @DisplayName("Deve cadastrar um Fornecedor e salvá-lo no banco")
    void deveCadastrarFornecedor() throws SQLException {

        var fornecedor = new Fornecedor(
                "NOMETESTE",
                "CNPJTESTE"
        );
        Fornecedor fornecedorNovo = fornecedorService.criarFornecedor(fornecedor);

        assertNotNull(fornecedorNovo);

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                     SELECT  nome
                            ,cnpj
                     FROM Fornecedor WHERE id =
                     """ + fornecedorNovo.getId())) {

            assertTrue(rs.next());
            assertEquals("NOMETESTE", rs.getString("nome"));
            assertEquals("CNPJTESTE", rs.getString("cnpj"));
        }
    }

    @Test
    @DisplayName("Deve buscar um fornecedor por ID")
    void deveBuscarPorId() throws SQLException {

        // Insere direto no banco para garantir
        int idGerado;
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                    INSERT INTO Fornecedor (nome, cnpj)
                    VALUES ('FornecedorX', '12345678901234')
                    """, Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            idGerado = keys.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Fornecedor fornecedor = fornecedorService.buscarPorId(idGerado);

        assertNotNull(fornecedor);
        assertEquals("FornecedorX", fornecedor.getNome());
        assertEquals("12345678901234", fornecedor.getCnpj());
    }

    @Test
    @DisplayName("Deve retornar exception ao buscar fornecedor com id invalido!")
    void DeveRetornarExceptionAoBuscarFornecedorComIdInvalido() throws SQLException{
        RuntimeException exception = assertThrows(RuntimeException.class, ()-> {
            fornecedorService.buscarPorId(9999);
        });
        assertEquals("Id do Fornecedor não encontrado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar todos os fornecedores cadastrados")
    void deveBuscarTodos()throws SQLException {

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                    INSERT INTO Fornecedor (nome, cnpj)
                    VALUES ('Fornecedor1', '11111111111111'),
                           ('Fornecedor2', '22222222222222');
                    """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Fornecedor> lista = fornecedorService.buscarTodos();

        assertEquals(2, lista.size());
        assertEquals("Fornecedor1", lista.get(0).getNome());
        assertEquals("Fornecedor2", lista.get(1).getNome());
    }

    @Test
    @DisplayName("Deve atualizar um fornecedor corretamente")
    void deveAtualizarFornecedor() throws SQLException {

        int idGerado;

        // Insere um fornecedor inicial
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                    INSERT INTO Fornecedor (nome, cnpj)
                    VALUES ('FornecedorAntigo', '99999999999999')
                    """, Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            idGerado = keys.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Atualiza via service
        Fornecedor atualizado = new Fornecedor("FornecedorNovo", "88888888888888");
        atualizado.setId(idGerado);

        fornecedorService.atualizarFornecedor(atualizado);

        // Busca no banco para validar
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, cnpj FROM Fornecedor WHERE id = " + idGerado)) {

            assertTrue(rs.next());
            assertEquals("FornecedorNovo", rs.getString("nome"));
            assertEquals("88888888888888", rs.getString("cnpj"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve retornar exception ao tentar atualizar fornecedor inexistente")
    void DeveRetornarExceptionAoAtualizarFornecedorComIdInvalido() throws SQLException{

        var fornecedor = new Fornecedor(999, "TESTE", "TESTE");
        RuntimeException exception = assertThrows(RuntimeException.class, ()-> {
            fornecedorService.atualizarFornecedor(fornecedor);
        });
        assertEquals("Id do fornecedor não encontrado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar fornecedor")
    void DeveDeletarFornecedor() throws SQLException{
        int idGerado;
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                    INSERT INTO Fornecedor (nome, cnpj)
                    VALUES ('FornecedorX', '12345678901234')
                    """, Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            idGerado = keys.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        fornecedorService.deletarFornecedor(idGerado);

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, cnpj FROM Fornecedor WHERE id = " + idGerado)) {

            assertFalse(rs.next());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve retornar exception ao buscar fornecedor com id invalido!")
    void DeveRetornarExceptionAoDeletarFornecedorComIdInvalido() throws SQLException{
        RuntimeException exception = assertThrows(RuntimeException.class, ()-> {
            fornecedorService.deletarFornecedor(9999);
        });
        assertEquals("Id do Fornecedor não encontrado!", exception.getMessage());
    }
}
