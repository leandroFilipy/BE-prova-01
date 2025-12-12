package senai.repository;

import senai.database.Conexao;
import senai.model.Fornecedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorRepository {

    public Fornecedor criarFornecedor (Fornecedor fornecedor) throws SQLException{

        String query = """
                INSERT INTO Fornecedor
                (nome,
                cnpj)
                VALUES
                (?,?)
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();


            if(rs.next()){

                fornecedor.setId(rs.getInt(1));
            }
        }
        return fornecedor;
    }

    public Fornecedor buscarFornecedorPorId (int id) throws SQLException{

        String query = """
                SELECT
                id,
                nome,
                cnpj
                FROM Fornecedor
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){

                return new Fornecedor(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj")
                );
            }
        }
        return null;
    }

    public List<Fornecedor> listarTodosFornecedores () throws SQLException{
        List<Fornecedor> fornecedores = new ArrayList<>();

        String query = """
                SELECT
                id,
                nome,
                cnpj
                FROM Fornecedor
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){

                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String cnpj = rs.getString("cnpj");

                fornecedores.add(new Fornecedor(id,nome,cnpj));
            }
        }
        return fornecedores;
    }

    public Boolean verificaSeFornecedorExiste (int id) throws SQLException{

        String query = """
                SELECT COUNT(*)
                FROM Fornecedor
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

    public void atualizarFornecedor (int id) throws SQLException {

        String query = """
                UPDATE Fornecedor
                SET nome = 'FornecedorNovo', cnpj = '88888888888888'
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deletarFornecedor (int id) throws SQLException{

        String query = """
                DELETE FROM Fornecedor
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Boolean verificarSeFornecedorExiste(int id) throws SQLException{

        String query = """
                SELECT COUNT (*)
                FROM Fornecedor
                WHERE id = ?
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
        }
        return false;
    }

}
