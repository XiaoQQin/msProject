package com.hwm.service;

import com.hwm.domain.MsUser;
import com.hwm.domain.User;

public interface UserService {
    User getUserById(int id);
    boolean tx();

}
