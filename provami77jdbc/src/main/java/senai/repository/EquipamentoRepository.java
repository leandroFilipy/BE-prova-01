package senai.repository;

import senai.database.Conexao;
import senai.model.Equipamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoRepository {

    public Equipamento criarEquipamento (Equipamento equipamento) throws SQLException{

        String query = """
                INSERT INTO
                Equipamento
                (nome,
                numero_serie,
                fornecedor_id)
                VALUES
                (?,?,?)
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getNumeroSerie());
            stmt.setInt(3, equipamento.getFornecedorId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){

                equipamento.setId(rs.getInt(1));
            }

        }
        return equipamento;
    }

    public Boolean verificarSeFornecedorExiste (int id) throws SQLException {

        String query = """
                SELECT COUNT(*)
                FROM Equipamento
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){

                return rs.getInt(1) > 0;
            }
        }
        return false;

    }

    public Equipamento buscarEquipamentoPorId (int id) throws SQLException{

        String query = """
                SELECT 
                id,
                nome,
                numero_serie,
                fornecedor_id
                FROM Equipamento 
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){

                return new Equipamento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("numero_serie"),
                        rs.getInt("fornecedor_id")
                );
            }
        }
        return null;
    }

    public Boolean verificaSeIdEquipamentoExiste (int id) throws SQLException{

        String query = """
                SELECT COUNT(*)
                FROM Equipamento 
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){

                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void atualizarEquipamento (int id) throws SQLException{

        String query = """
                UPDATE Equipamento
                SET nome = 'Monitor Novo', numero_serie = 'MON-2'
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Equipamento> listarEquipamentosDeAcordoComFornecedor (int id) throws SQLException{
        List<Equipamento> equipamentos = new ArrayList<>();

        String query = """
                SELECT
                nome,
                numero_serie,
                fornecedor_id
                FROM Equipamento 
                WHERE fornecedor_id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){


                String nome2 = rs.getString("nome");
                String numero_serie = rs.getString("numero_serie");
                int idFornecedor2 = rs.getInt("fornecedor_id");
                equipamentos.add(new Equipamento(nome2, numero_serie, idFornecedor2));
            }
        }
        return equipamentos;
    }

    public void deletarEquipamento (int id) throws SQLException{

        String query = """
                DELETE FROM Equipamento
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}
