/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.neucloud.server.dao.user;


import cn.neucloud.server.common.data.User;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface UserDao extends Dao<User> {

    /**
     * Save or update user object
     *
     * @param user the user object
     * @return saved user entity
     */
    User save(User user);

    /**
     * Find user by email.
     *
     * @param email the email
     * @return the user entity
     */
    User findByEmail(String email);
    
    /**
     * Find user admin users by userId and page link.
     *
     * @param pageLink the page link
     * @return the list of user entities
     */
    List<User> findUserAdmins(TextPageLink pageLink);
    
    /**
     * Find customer users by userId, customerId and page link.
     *
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of user entities
     */
    List<User> findCustomerUsers(UUID customerId, TextPageLink pageLink);
    
}
