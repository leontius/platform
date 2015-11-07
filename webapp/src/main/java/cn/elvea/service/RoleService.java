package cn.elvea.service;

import cn.elvea.core.service.EntityService;
import cn.elvea.domain.Role;
import cn.elvea.repository.EntityRepository;
import cn.elvea.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends EntityService<Role> {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public EntityRepository<Role> getRepository() {
        return roleRepository;
    }
}
