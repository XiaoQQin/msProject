package com.hwm.service;

import com.hwm.dao.UserDao;
import com.hwm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    @Transactional
    public boolean tx() {
        User u1=new User(1,"hwm11111111");
        userDao.tx(u1);

        User u2=new User(2,"hwm22222222");
        userDao.tx(u2);
        return true;
    }


}
