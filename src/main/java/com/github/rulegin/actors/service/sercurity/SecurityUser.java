package com.github.rulegin.actors.service.sercurity;

import com.github.rulegin.common.data.User;
import com.github.rulegin.common.data.id.UserId;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityUser extends User {

    private static final long serialVersionUID = -797397440703066079L;

    private Collection<GrantedAuthority> authorities;
    private boolean enabled;
    private UserPrincipal userPrincipal;

    public SecurityUser() {
        super();
    }

    public SecurityUser(UserId id) {
        super(id);
    }

    public SecurityUser(User user, boolean enabled, UserPrincipal userPrincipal) {
        super(user);
        this.enabled = enabled;
        this.userPrincipal = userPrincipal;
    }

    //public Collection<? extends GrantedAuthority> getAuthorities() {
    //    if (authorities == null) {
    //        authorities = Stream.of(SecurityUser.this.getAuthority())
    //                .map(authority -> new SimpleGrantedAuthority(authority.name()))
    //                .collect(Collectors.toList());
    //    }
    //    return authorities;
    //}

    public boolean isEnabled() {
        return enabled;
    }

    public UserPrincipal getUserPrincipal() {
        return userPrincipal;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUserPrincipal(UserPrincipal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

}
