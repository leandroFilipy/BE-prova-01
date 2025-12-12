package senai.service.fornecedor;

import senai.model.Fornecedor;
import senai.repository.FornecedorRepository;

import java.sql.SQLException;
import java.util.List;

public class FornecedorServiceImpl implements FornecedorService{

    FornecedorRepository repository = new FornecedorRepository();

    @Override
    public Fornecedor criarFornecedor(Fornecedor fornecedor) throws SQLException {

        repository.criarFornecedor(fornecedor);
        return fornecedor;
    }

    @Override
    public Fornecedor buscarPorId(int id) throws SQLException {

        if(!repository.verificaSeFornecedorExiste(id)){

            throw new RuntimeException("Id do Fornecedor não encontrado!");
        }
        Fornecedor fornecedor = repository.buscarFornecedorPorId(id);

        return fornecedor;
    }

    @Override
    public List<Fornecedor> buscarTodos() throws SQLException {
        return repository.listarTodosFornecedores();
    }

    @Override
    public void atualizarFornecedor(Fornecedor fornecedor) throws SQLException {

        if(!repository.verificaSeFornecedorExiste(fornecedor.getId())){

            throw new RuntimeException("Id do fornecedor não encontrado!");
        }
        repository.atualizarFornecedor(fornecedor.getId());
    }

    @Override
    public void deletarFornecedor(int id) throws SQLException {

        if(!repository.verificaSeFornecedorExiste(id)){

            throw new RuntimeException("Id do Fornecedor não encontrado!");
        }

        repository.deletarFornecedor(id);
    }
}
