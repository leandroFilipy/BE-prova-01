import org.junit.jupiter.api.*;
import senai.database.Conexao;
import senai.model.Equipamento;
import senai.service.equipamento.EquipamentoService;
import senai.service.equipamento.EquipamentoServiceImpl;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Teste de Integração - Equipamento (Isolado com SQL)")
public class EquipamentoServiceIntegrationTest {

    private EquipamentoService equipamentoService;

    // --- Scripts DDL ---
    private static final String CREATE_FORNECEDOR = """
            CREATE TABLE Fornecedor (
              id INT PRIMARY KEY AUTO_INCREMENT,
              nome VARCHAR(100) NOT NULL,
              cnpj VARCHAR(14) UNIQUE NOT NULL
            );
            """;

    private static final String CREATE_EQUIPAMENTO = """
            CREATE TABLE Equipamento (
              id INT PRIMARY KEY AUTO_INCREMENT,
              nome VARCHAR(100) NOT NULL,
              numero_serie VARCHAR(50) UNIQUE NOT NULL,
              fornecedor_id INT NOT NULL,
              FOREIGN KEY (fornecedor_id) REFERENCES Fornecedor(id)
            );
            """;

    @BeforeAll
    static void setupGlobal() throws Exception {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Equipamento");
            stmt.execute("DROP TABLE IF EXISTS Fornecedor");
            stmt.execute(CREATE_FORNECEDOR);
            stmt.execute(CREATE_EQUIPAMENTO);
        }
    }

    @AfterAll
    static void tearDownGlobal() throws Exception {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Equipamento");
            stmt.execute("DROP TABLE IF EXISTS Fornecedor");
        }
    }

    @BeforeEach
    void setup() throws Exception {
        equipamentoService = new EquipamentoServiceImpl();

        // Limpa o banco antes de cada teste usando SQL direto
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE Equipamento");
            stmt.execute("TRUNCATE TABLE Fornecedor");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Test
    @DisplayName("Deve cadastrar equipamento e validar inserção via SQL")
    void deveCadastrarEquipamento() throws SQLException {

        int idFornecedor = inserirFornecedorSQL("Dell", "11111111000199");

        Equipamento novo = new Equipamento("Notebook", "SN100", idFornecedor);
        Equipamento salvo = equipamentoService.criarEquipamento(novo);

        assertNotNull(salvo.getId());

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, fornecedor_id FROM Equipamento WHERE id = " + salvo.getId())) {

            assertTrue(rs.next(), "Deveria ter encontrado o registro no banco");
            assertEquals("Notebook", rs.getString("nome"));
            assertEquals(idFornecedor, rs.getInt("fornecedor_id"));
        }
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar criar equipamento com fornecedor inexistente")
    void deveFalharFK() {

        Equipamento equip = new Equipamento("Erro", "ERR1", 9999);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            equipamentoService.criarEquipamento(equip);
        });

        assertEquals("Fornecedor inválido ou inexistente!", ex.getMessage());
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void deveBuscarPorId() throws SQLException {

        int idFornecedor = inserirFornecedorSQL("HP", "22222222000199");
        int idEquipamento = inserirEquipamentoSQL("Impressora", "HP-JET", idFornecedor);

        Equipamento encontrado = equipamentoService.buscarPorId(idEquipamento);

        assertNotNull(encontrado);
        assertEquals("Impressora", encontrado.getNome());
        assertEquals(idFornecedor, encontrado.getFornecedorId());
    }

    @Test
    @DisplayName("Deve retornar Exception quando id fornecedor não existe")
    void deveRetornarExceptionAoBuscarPorIdInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            equipamentoService.buscarPorId(999);
        });

        assertEquals("Id do Equipamento não encontrado!", ex.getMessage());
    }

    @Test
    @DisplayName("Deve listar equipamentos por Fornecedor")
    void deveBuscarPorFornecedorId() throws SQLException {

        int idForn1 = inserirFornecedorSQL("Logitech", "333");
        int idForn2 = inserirFornecedorSQL("Razer", "444");

        inserirEquipamentoSQL("Mouse", "M1", idForn1);
        inserirEquipamentoSQL("Teclado", "T1", idForn1);
        inserirEquipamentoSQL("Headset", "H1", idForn2);

        // Ação
        List<Equipamento> lista = equipamentoService.buscarPorFornecedorId(idForn1);

        // Validação
        assertEquals(2, lista.size());
        assertEquals("Mouse", lista.get(0).getNome());
        assertEquals("Teclado", lista.get(1).getNome());
    }

    @Test
    @DisplayName("Deve atualizar equipamento")
    void deveAtualizarEquipamento() throws SQLException {
        int idForn = inserirFornecedorSQL("Samsung", "555");
        int idEquip = inserirEquipamentoSQL("Monitor Antigo", "MON-1", idForn);

        Equipamento paraAtualizar = new Equipamento(idEquip, "Monitor Novo", "MON-2", idForn);
        equipamentoService.atualizarEquipamento(paraAtualizar);

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, numero_serie FROM Equipamento WHERE id = " + idEquip)) {

            assertTrue(rs.next());
            assertEquals("Monitor Novo", rs.getString("nome"));
            assertEquals("MON-2", rs.getString("numero_serie"));
        }
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar equipamento com fornecedor inexistente")
    void deveLancarExceptionAoTentarAtualizarEquipamentoInexistente() {

        Equipamento equip = new Equipamento("Erro", "ERR1", 9999);

        equip.setId(999);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            equipamentoService.atualizarEquipamento(equip);
        });

        assertEquals("Equipamento não encontrado para atualização!", ex.getMessage());
    }

    @Test
    @DisplayName("Deve deletar equipamento")
    void deveDeletarEquipamento() throws SQLException {
        // Cenário
        int idForn = inserirFornecedorSQL("LG", "666");
        int idEquip = inserirEquipamentoSQL("TV", "TV-LG", idForn);

        // Ação
        equipamentoService.deletarEquipamento(idEquip);

        // Validação: Verificar no banco se sumiu
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM Equipamento WHERE id = " + idEquip)) {

            rs.next();
            assertEquals(0, rs.getInt(1), "O registro deveria ter sido apagado");
        }
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar equipamento com id inexistente")
    void deveLancarExceptionAoTentarDeletarEquipamentoInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            equipamentoService.deletarEquipamento(999);
        });

        assertEquals("Equipamento não encontrado para exclusão!", ex.getMessage());
    }



    private int inserirFornecedorSQL(String nome, String cnpj) throws SQLException {
        String sql = "INSERT INTO Fornecedor (nome, cnpj) VALUES (?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, cnpj);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
    }

    private int inserirEquipamentoSQL(String nome, String serie, int fornecedorId) throws SQLException {
        String sql = "INSERT INTO Equipamento (nome, numero_serie, fornecedor_id) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, serie);
            stmt.setInt(3, fornecedorId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
    }
}

