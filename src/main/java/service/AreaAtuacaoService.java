package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import model.AreaAtuacao;
import java.util.List;

@ApplicationScoped
public class AreaAtuacaoService {

    @Transactional
    public List<AreaAtuacao> listarTodas() {
        return AreaAtuacao.listAll();
    }

    @Transactional
    public AreaAtuacao buscarPorId(Long id) {
        return AreaAtuacao.findById(id);
    }

    @Transactional
    public AreaAtuacao buscarPorDescricao(String descricao) {
        return AreaAtuacao.buscarPorDescricao(descricao);
    }

    @Transactional
    public void salvar(AreaAtuacao area) {
        area.persist();
    }

    @Transactional
    public void atualizar(AreaAtuacao area) {
        AreaAtuacao areaExistente = buscarPorId(area.id);
        if (areaExistente != null) {
            area.persist();
        }
    }

    @Transactional
    public void excluir(Long id) {
        AreaAtuacao.deleteById(id);
    }
} 