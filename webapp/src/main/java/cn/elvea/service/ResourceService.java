package cn.elvea.service;

import cn.elvea.core.service.EntityService;
import cn.elvea.domain.Resource;
import cn.elvea.repository.EntityRepository;
import cn.elvea.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService extends EntityService<Resource> {
    @Autowired
    ResourceRepository resourceRepository;

    @Override
    public EntityRepository<Resource> getRepository() {
        return resourceRepository;
    }
}
