package senai.service.fornecedor;

import senai.model.Fornecedor;

import java.sql.SQLException;
import java.util.List;

public interface FornecedorService {
    public Fornecedor criarFornecedor(Fornecedor fornecedor) throws SQLException;

    public Fornecedor buscarPorId(int id) throws SQLException;

    public List<Fornecedor> buscarTodos() throws SQLException;

    public void atualizarFornecedor(Fornecedor fornecedor) throws SQLException;

    public void deletarFornecedor(int id) throws SQLException;
}
