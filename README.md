# AppDash

A web dashboard for managing [jvmctl] applications.

[jvmctl]: https://github.com/nla/jvmctl

## Configuration

AppDash uses OpenID Connect for authentication. Create a 'developer' role for the users you wish to have access.

    OIDC_CLIENT_ID=
    OIDC_SECRET=
    OIDC_URL=https://keycloak.example.org/auth/realms/master/.well-known/openid-configuration