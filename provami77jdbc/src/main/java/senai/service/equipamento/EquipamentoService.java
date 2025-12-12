package senai.service.equipamento;

import senai.model.Equipamento;

import java.sql.SQLException;
import java.util.List;

public interface EquipamentoService {
    Equipamento criarEquipamento(Equipamento equipamento) throws SQLException;
    Equipamento buscarPorId(int id) throws SQLException;
    List<Equipamento> buscarPorFornecedorId(int fornecedorId) throws SQLException;
    void atualizarEquipamento(Equipamento equipamento) throws SQLException;
    void deletarEquipamento(int id) throws SQLException;
}
