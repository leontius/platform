package cn.elvea.core.spring.web;

import cn.elvea.domain.IdEntity;
import cn.elvea.core.service.EntityService;

public abstract class BaseEntityController<T extends IdEntity> extends BaseController {
    private EntityService<T> entityService = null;

    public void setEntityService(EntityService<T> entityService) {
        this.entityService = entityService;
    }
}
