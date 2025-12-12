package senai.service.equipamento;

import senai.model.Equipamento;
import senai.model.Fornecedor;
import senai.repository.EquipamentoRepository;
import senai.repository.FornecedorRepository;

import java.sql.SQLException;
import java.util.List;

public class EquipamentoServiceImpl implements EquipamentoService{

    EquipamentoRepository repository = new EquipamentoRepository();
    FornecedorRepository fornecedorRepository = new FornecedorRepository();

    @Override
    public Equipamento criarEquipamento(Equipamento equipamento) throws SQLException {


        if(!fornecedorRepository.verificaSeFornecedorExiste(equipamento.getFornecedorId())){

            throw new RuntimeException("Fornecedor inválido ou inexistente!");
        }
            equipamento = repository.criarEquipamento(equipamento);


        return equipamento;
    }

    @Override
    public Equipamento buscarPorId(int id) throws SQLException {

        Equipamento equipamento = repository.buscarEquipamentoPorId(id);
        if(equipamento == null){

            throw new RuntimeException("Id do Equipamento não encontrado!");
        }

        return equipamento;
    }

    @Override
    public List<Equipamento> buscarPorFornecedorId(int fornecedorId) throws SQLException {

        List<Equipamento> equipamentos = repository.listarEquipamentosDeAcordoComFornecedor(fornecedorId);
        return equipamentos;
    }

    @Override
    public void atualizarEquipamento(Equipamento equipamento) throws SQLException {

        if(!repository.verificaSeIdEquipamentoExiste(equipamento.getId())){

            throw new RuntimeException("Equipamento não encontrado para atualização!");
        }
        repository.atualizarEquipamento(equipamento.getId());
    }

    @Override
    public void deletarEquipamento(int id) throws SQLException {

        if(!repository.verificaSeIdEquipamentoExiste(id)){

            throw new RuntimeException("Equipamento não encontrado para exclusão!");
        }

        repository.deletarEquipamento(id);
    }
}
