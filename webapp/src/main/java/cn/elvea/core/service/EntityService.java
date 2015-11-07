package cn.elvea.core.service;

import cn.elvea.domain.IdEntity;
import cn.elvea.repository.EntityRepository;
import cn.elvea.utils.Page;

import java.util.List;

public abstract class EntityService<T extends IdEntity> extends BaseService {
    public abstract EntityRepository<T> getRepository();

    public T findById(Long id) {
        return getRepository().findById(id);
    }

    public List<T> findByPage(Page<T> page) {
        return getRepository().findByPage(page);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

    public void delete(Long id) {
        getRepository().delete(id);
    }

    public void save(T entity) {
        if (entity.getId() != null && entity.getId() > 0) {
            update(entity);
        } else {
            insert(entity);
        }
    }

    public void insert(T entity) {
        getRepository().insert(entity);
    }

    public void update(T entity) {
        getRepository().update(entity);
    }
}
