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
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.TextPageData;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.entity.AbstractEntityService;
import cn.neucloud.server.dao.exception.DataValidationException;
import cn.neucloud.server.dao.exception.IncorrectParameterException;
import cn.neucloud.server.dao.util.DataValidator;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static cn.neucloud.server.dao.util.Validator.validateId;
import static cn.neucloud.server.dao.util.Validator.validatePageLink;
import static cn.neucloud.server.dao.util.Validator.validateString;


@Service
@Slf4j
public class UserServiceImpl extends AbstractEntityService implements UserService {

    private static final int DEFAULT_TOKEN_LENGTH = 30;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserCredentialsDao userCredentialsDao;

    
	@Override
	public User findUserByEmail(String email) {
	    log.trace("Executing findUserByEmail [{}]", email);
		validateString(email, "Incorrect email " + email);
		return userDao.findByEmail(email);
	}

	@Override
	public User findUserById(UserId userId) {
	    log.trace("Executing findUserById [{}]", userId);
		validateId(userId, "Incorrect userId " + userId);
		return userDao.findById(userId.getId());
	}

    @Override
    public ListenableFuture<User> findUserByIdAsync(UserId userId) {
        log.trace("Executing findUserByIdAsync [{}]", userId);
        validateId(userId, "Incorrect userId " + userId);
        return userDao.findByIdAsync(userId.getId());
    }

    @Override
    public User saveUser(User user) {
        log.trace("Executing saveUser [{}]", user);
        userValidator.validate(user);
        User savedUser = userDao.save(user);
        if (user.getId() == null) {
            UserCredentials userCredentials = null ;//new UserCredentials();
            //userCredentials.setEnabled(false);
            //userCredentials.setActivateToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
            //userCredentials.setUserId(new UserId(savedUser.getUuidId()));
            userCredentialsDao.save(userCredentials);
        }
        return savedUser;
    }

    @Override
    public UserCredentials findUserCredentialsByUserId(UserId userId) {
        log.trace("Executing findUserCredentialsByUserId [{}]", userId);
        validateId(userId, "Incorrect userId " + userId);
        return userCredentialsDao.findByUserId(userId.getId());
    }

    @Override
    public UserCredentials findUserCredentialsByActivateToken(String activateToken) {
        log.trace("Executing findUserCredentialsByActivateToken [{}]", activateToken);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        return userCredentialsDao.findByActivateToken(activateToken);
    }

    @Override
    public UserCredentials findUserCredentialsByResetToken(String resetToken) {
        log.trace("Executing findUserCredentialsByResetToken [{}]", resetToken);
        validateString(resetToken, "Incorrect resetToken " + resetToken);
        return userCredentialsDao.findByResetToken(resetToken);
    }

    @Override
    public UserCredentials saveUserCredentials(UserCredentials userCredentials) {
        log.trace("Executing saveUserCredentials [{}]", userCredentials);
        userCredentialsValidator.validate(userCredentials);
        return userCredentialsDao.save(userCredentials);
    }
    
    @Override
    public UserCredentials activateUserCredentials(String activateToken, String password) {
        log.trace("Executing activateUserCredentials activateToken [{}], password [{}]", activateToken, password);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        validateString(password, "Incorrect password " + password);
        UserCredentials userCredentials = userCredentialsDao.findByActivateToken(activateToken);
        if (userCredentials == null) {
            throw new IncorrectParameterException(String.format("Unable to find user credentials by activateToken [%s]", activateToken));
        }
        //if (userCredentials.isEnabled()) {
        //    throw new IncorrectParameterException("User credentials already activated");
        //}
        //userCredentials.setEnabled(true);
        //userCredentials.setActivateToken(null);
        //userCredentials.setPassword(password);
        
        return saveUserCredentials(userCredentials);
    }

    @Override
    public UserCredentials requestPasswordReset(String email) {
        log.trace("Executing requestPasswordReset email [{}]", email);
        validateString(email, "Incorrect email " + email);
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IncorrectParameterException(String.format("Unable to find user by email [%s]", email));
        }
        UserCredentials userCredentials = userCredentialsDao.findByUserId(user.getUuidId());
        if (!userCredentials.isEnabled()) {
            throw new IncorrectParameterException("Unable to reset password for inactive user");
        }
        userCredentials.setResetToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
        return saveUserCredentials(userCredentials);
    }


    @Override
    public void deleteUser(UserId userId) {
        log.trace("Executing deleteUser [{}]", userId);
        validateId(userId, "Incorrect userId " + userId);
        UserCredentials userCredentials = userCredentialsDao.findByUserId(userId.getId());
        userCredentialsDao.removeById(userCredentials.getUuidId());
        deleteEntityRelations(userId);
        userDao.removeById(userId.getId());
    }

    @Override
    public TextPageData<User> findUserAdmins(TextPageLink pageLink) {
        log.trace("Executing findUserAdmins, userId [{}], pageLink [{}]", pageLink);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<User> users = userDao.findUserAdmins( pageLink);
        return new TextPageData<>(users, pageLink);
    }

    @Override
    public TextPageData<User> findCustomerUsers(  UserId customerId, TextPageLink pageLink) {
        log.trace("Executing findCustomerUsers, userId [{}], customerId [{}], pageLink [{}]", customerId, pageLink);
        validateId(customerId, "Incorrect customerId " + customerId);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<User> users = userDao.findCustomerUsers(customerId.getId(), pageLink);
        return new TextPageData<>(users, pageLink);
    }


    private DataValidator<User> userValidator =
            new DataValidator<User>() {
                @Override
                protected void validateDataImpl(User user) {
                    if (StringUtils.isEmpty(user.getEmail())) {
                        throw new DataValidationException("User email should be specified!");
                    }
                    
                    validateEmail(user.getEmail());
                    
//                   Authority authority = user.getAuthority();
//                    if (authority == null) {
//                        throw new DataValidationException("User authority isn't defined!");
//                    }
   //                 TenantId userId = user.getUserId();
   //                 if (userId == null) {
   //                     userId = new TenantId(ModelConstants.NULL_UUID);
   //                     user.setUserId(userId);
   //                 }
   //                 UserId customerId = user.getCustomerId();
   //                 if (customerId == null) {
   //                     customerId = new UserId(ModelConstants.NULL_UUID);
   //                     user.setCustomerId(customerId);
   //                 }
                    
     /*               switch (authority) {
                        case SYS_ADMIN:
                            if (!userId.getId().equals(ModelConstants.NULL_UUID)
                                    || !customerId.getId().equals(ModelConstants.NULL_UUID)) {
                                    throw new DataValidationException("System administrator can't be assigned neither to user nor to customer!");
                            }
                            break;
                        case TENANT_ADMIN:
                            if (userId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("Tenant administrator should be assigned to user!");
                            } else if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
                                throw new DataValidationException("Tenant administrator can't be assigned to customer!");
                            }
                            break;
                        case CUSTOMER_USER:
                            if (userId.getId().equals(ModelConstants.NULL_UUID)
                                || customerId.getId().equals(ModelConstants.NULL_UUID) ) {
                                throw new DataValidationException("Customer user should be assigned to customer!");
                            }
                            break;
                        default:
                            break;
                    }
                    */
                    User existentUserWithEmail = findUserByEmail(user.getEmail());
                    if (existentUserWithEmail != null && !isSameData(existentUserWithEmail, user)) {
                        throw new DataValidationException("User with email '" + user.getEmail() + "' "
                                + " already present in database!");
                    }
              /*      if (!userId.getId().equals(ModelConstants.NULL_UUID)) {
                        Tenant user = tenantDao.findById(user.getUserId().getId());
                        if (user == null) {
                            throw new DataValidationException("User is referencing to non-existent user!");
                        }
                    }*/
                 //   if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
                        //Customer customer = customerDao.findById(user.getUserId().getId());
                        //if (customer == null) {
                          //  throw new DataValidationException("User is referencing to non-existent customer!");
                        //} else if (!customer.getUserId().getId().equals(userId.getId())) {
                        //    throw new DataValidationException("User can't be assigned to customer from different user!");
                       // }
                   // }
                 }
    };
    
    private DataValidator<UserCredentials> userCredentialsValidator =
            new DataValidator<UserCredentials>() {
        
                @Override
                protected void validateCreate(UserCredentials userCredentials) {
                    throw new IncorrectParameterException("Creation of new user credentials is prohibited.");
                }
                
                @Override
                protected void validateDataImpl(UserCredentials userCredentials) {
                    if (userCredentials.getUserId() == null) {
                        throw new DataValidationException("User credentials should be assigned to user!");
                    }
                    if (userCredentials.isEnabled()) {
                        if (StringUtils.isEmpty(userCredentials.getPassword())) {
                            throw new DataValidationException("Enabled user credentials should have password!");
                        }
                        if (StringUtils.isNotEmpty(userCredentials.getActivateToken())) {
                            throw new DataValidationException("Enabled user credentials can't have activate token!");
                        }
                    }
                    //UserCredentials existingUserCredentialsEntity = userCredentialsDao.findById(userCredentials.getId().getId());
                    //if (existingUserCredentialsEntity == null) {
                    //    throw new DataValidationException("Unable to update non-existent user credentials!");
                    //}
                    //User user = findUserById(userCredentials.getUserId());
                    //if (user == null) {
                    //    throw new DataValidationException("Can't assign user credentials to non-existent user!");
                    //}
                }
    };


    
}
