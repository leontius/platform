package cn.elvea.repository;

import cn.elvea.domain.User;
import cn.elvea.test.BaseTest;
import cn.elvea.utils.Page;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserRepositoryTest extends BaseTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void test1() {
        Page<User> page = new Page<>(1, 1);
        List<User> users = userRepository.findByPage(page);
        Assert.assertNotNull(users);
    }
}
