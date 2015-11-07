package cn.elvea.service;

import cn.elvea.core.service.EntityService;
import cn.elvea.domain.Permission;
import cn.elvea.repository.EntityRepository;
import cn.elvea.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends EntityService<Permission> {
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public EntityRepository<Permission> getRepository() {
        return permissionRepository;
    }
}
