package com.github.rulegin.dao.user;

import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jary on 2017/10/10 0010.
 */
@Component
public class UserCredentialsDaoImpl implements UserCredentialsDao {
    @Override
    public List<UserCredentials> find() {
        return null;
    }

    @Override
    public UserCredentials findById(UUID id) {
        return null;
    }

    @Override
    public ListenableFuture<UserCredentials> findByIdAsync(UUID id) {
        return null;
    }

    @Override
    public boolean removeById(UUID id) {
        return false;
    }

    @Override
    public UserCredentials save(UserCredentials userCredentials) {
        return null;
    }

    @Override
    public UserCredentials findByUserId(UUID userId) {
        return null;
    }

    @Override
    public UserCredentials findByActivateToken(String activateToken) {
        return null;
    }

    @Override
    public UserCredentials findByResetToken(String resetToken) {
        return null;
    }
}
