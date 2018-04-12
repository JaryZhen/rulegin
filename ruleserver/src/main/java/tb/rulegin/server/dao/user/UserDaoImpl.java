package tb.rulegin.server.dao.user;

import tb.rulegin.server.common.data.User;
import tb.rulegin.server.common.data.page.TextPageLink;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jary on 2017/10/10 0010.
 */
@Component
public class UserDaoImpl implements UserDao {
    @Override
    public List<User> find() {
        return null;
    }

    @Override
    public User findById(UUID id) {
        return null;
    }

    @Override
    public ListenableFuture<User> findByIdAsync(UUID id) {
        return null;
    }

    @Override
    public boolean removeById(UUID id) {
        return false;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public List<User> findUserAdmins(TextPageLink pageLink) {
        return null;
    }

    @Override
    public List<User> findCustomerUsers(UUID customerId, TextPageLink pageLink) {
        return null;
    }
}
