package cn.elvea.service;

import cn.elvea.core.service.EntityService;
import cn.elvea.domain.User;
import cn.elvea.repository.EntityRepository;
import cn.elvea.repository.UserRepository;
import cn.elvea.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService extends EntityService<User> {
    @Autowired
    UserRepository userRepository;

    @Override
    public EntityRepository<User> getRepository() {
        return userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void insert(User user) {
        if (user.getId() == null) {
            user.setSalt(SecurityUtils.generateSalt());
            user.setPassword(SecurityUtils.entryptPassword(user.getPassword(), user.getSalt()));
        }
        super.save(user);
    }
}
