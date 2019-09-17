package com.yourproject.auth.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Document
public class User implements UserDetails {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private List<GrantedAuthority> authorities;

    private User() {}

    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     */
    public User(String username, String password, List<GrantedAuthority> authorities) {
        this(username, password, true, true, true, true, authorities);
    }

    User(String username, String encodedPassword, boolean enabled, boolean accountNonExpired, boolean
            credentialsNonExpired, boolean accountNonLocked, List<GrantedAuthority> authorities) {
        if (((username == null) || "".equals(username)) || (encodedPassword == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.username = username;
        this.password = encodedPassword;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableList(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public static Builder copyFrom(User user) {
        Builder builder = builder();

        if (user.getUsername() != null) {
            builder.username(user.getUsername());
        }

        if (user.getPassword() != null) {
            builder.password(user.getPassword());
        }

        if (user.getAuthorities() != null && user.getAuthorities().size() > 0) {
            builder.authorities(user.getAuthorities());
        }

        builder.accountNonExpired(user.isAccountNonExpired());
        builder.accountNonLocked(user.isAccountNonLocked());
        builder.credentialsNonExpired(user.isCredentialsNonExpired());
        builder.enabled(user.isEnabled());

        return builder;
    }

    /**
     * Creates a Builder
     *
     * @return the Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds the user to be added. At minimum the username, password, and authorities
     * should provided. The remaining attributes have reasonable defaults.
     */
    public static class Builder {
        private Function<String, String> passwordEncoderFunction;
        private String username;
        private String password;
        private List<GrantedAuthority> authorities;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;
        private boolean enabled = true;

        /**
         * Creates a new instance
         */
        private Builder() {
        }

        public Builder passwordEncoderFunction(Function<String, String> passwordEncoderFunction) {
            Assert.notNull(passwordEncoderFunction, "password encoder function cannot be null");
            this.passwordEncoderFunction = passwordEncoderFunction;
            return this;
        }

        /**
         * Populates the username. This attribute is required.
         *
         * @param username the username. Cannot be null.
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            Assert.notNull(password, "password cannot be null");
            this.password = password;
            return this;
        }

        /**
         * Populates the roles. This method is a shortcut for calling
         * {@link #authorities(String...)}, but automatically prefixes each entry with
         * "ROLE_". This means the following:
         *
         * <code>
         *     builder.roles("USER","ADMIN");
         * </code>
         *
         * is equivalent to
         *
         * <code>
         *     builder.authorities("ROLE_USER","ROLE_ADMIN");
         * </code>
         *
         * <p>
         * This attribute is required, but can also be populated with
         * {@link #authorities(String...)}.
         * </p>
         *
         * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot be null,
         * contain null values or start with "ROLE_"
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(
                    roles.length);
            for (String role : roles) {
                Assert.isTrue(!role.startsWith("ROLE_"), role
                        + " cannot start with ROLE_ (it is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return authorities(authorities);
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities the authorities for this user. Cannot be null, or contain
         * null values
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public Builder authorities(GrantedAuthority... authorities) {
            return authorities(Arrays.asList(authorities));
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities the authorities for this user. Cannot be null, or contain
         * null values
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        /**
         * Populates the authorities. This attribute is required.
         *
         * @param authorities the authorities for this user (i.e. ROLE_USER, ROLE_ADMIN,
         * etc). Cannot be null, or contain null values
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         * @see #roles(String...)
         */
        public Builder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        /**
         * Defines if the account is expired or not. Default is false.
         *
         * @param accountNonExpired true if the account is expired, false otherwise
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        /**
         * Defines if the account is locked or not. Default is false.
         *
         * @param accountNonLocked true if the account is locked, false otherwise
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        /**
         * Defines if the credentials are expired or not. Default is false.
         *
         * @param credentialsNonExpired true if the credentials are expired, false otherwise
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        /**
         * Defines if the account is enabled or not. Default is false.
         *
         * @param enabled true if the account is enabled, false otherwise
         * @return the {@link Builder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            String encodedPassword = this.passwordEncoderFunction.apply(password);
            return new User(username, encodedPassword, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        }
    }
}