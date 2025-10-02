# Java OAuth Service Application

A production-ready Spring Boot 3 microservice that demonstrates secure **Okta OAuth 2.0** integration using the **Service App** pattern with `client_credentials` grant type and `private_key_jwt` authentication. This service provides essential user management capabilities including:

- System health monitoring
- User profile retrieval by ID
- Paginated user directory listing
- Secure profile updates (first/last name attributes) following least-privilege principles

---

## Technology Stack

- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.6
- **Build Tool**: Maven 3.9+
- **HTTP Client**: Spring WebClient with Reactor Netty
- **JWT Generation**: JJWT library with RSA private key (PEM format)
- **Deployment**: Standard JAR execution via `mvn spring-boot:run` or `java -jar`

### Prerequisites

Ensure your development environment meets these requirements:
- Java Development Kit (JDK) 21 or higher: verify with `java -version`
- Apache Maven 3.9 or higher: verify with `mvn -v`

---

## Okta Configuration Guide

Complete these one-time setup steps in your Okta tenant before deploying the service:

### 1. Create API Services Application

Navigate to the Okta Admin Console and create a new application:
- Go to **Applications** → **Create App Integration**
- Select **API Services** as the application type

### 2. Configure Client Authentication

Set up asymmetric key authentication for enhanced security:
- Choose **Public key / Private key** as the client authentication method
- Generate an RSA key pair (2048-bit minimum recommended for production)
- Register your **public key** in the Okta application configuration

### 3. Grant Required API Scopes

Assign the necessary Okta API scopes to your application via the **Okta API Scopes** tab:
- `okta.users.read` - Enables user profile retrieval
- `okta.users.manage` - Allows user profile modifications

### 4. Assign Administrative Permissions

Configure least-privilege access by assigning a custom admin role via the **Admin Roles** tab:

**Minimum Required Permissions:**
- View users and their details
- Edit users' profile attributes

**Best Practice:** Create a custom **Resource Set** to further restrict the application's access scope to only the necessary user groups or organizational units.

### 5. Key Rotation

When rotating cryptographic keys for security compliance:
- Generate a new RSA key pair
- Update the public key registration in Okta
- Update the local private key file referenced by `OKTA_JWT_PEM_PATH`

---

## Environment Configuration

The application follows the twelve-factor app methodology and reads all configuration from environment variables. You can set these in your shell environment or use a `.env` file for local development.

### Required Configuration

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `OKTA_DOMAIN` | ✅ | Your Okta organization domain | `https://example.okta.com` |
| `OKTA_CLIENT_ID` | ✅ | OAuth client identifier from your API Services app | `0oaxxxxxxxxxxxxxxx` |
| `OKTA_SCOPE` | ✅ | Space-separated OAuth scopes | `okta.users.read okta.users.manage` |
| `OKTA_JWT_AUDIENCE` | ✅ | Token endpoint URL for JWT audience claim | `https://example.okta.com/oauth2/v1/token` |
| `OKTA_JWT_KID` | ✅ | Key ID matching your registered public key in Okta | `key-abc123` |
| `OKTA_JWT_PEM_PATH` | ✅ | File path to your RSA private key in PEM format | `secrets/private_key.pem` |

### Optional Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `OKTA_OAUTH_TOKEN_PATH` | `/oauth2/v1/token` | OAuth token endpoint path |
| `OKTA_API_BASE` | `/api/v1` | Okta API base path |
| `HTTP_CONNECT_TIMEOUT_MS` | `3000` | HTTP connection timeout in milliseconds |
| `HTTP_READ_TIMEOUT_MS` | `5000` | HTTP read timeout in milliseconds |
| `API_KEY_ENABLED` | `false` | Enable API key authentication for endpoints |
| `API_KEY_VALUE` | `change-me` | API key value (change in production) |
| `SERVER_PORT` | `8080` | Application server port |

### Example `.env` File
```properties
OKTA_DOMAIN=https://example.okta.com
OKTA_CLIENT_ID=0oaxxxxxxxxxxxxxxx
OKTA_SCOPE=okta.users.read okta.users.manage
OKTA_JWT_AUDIENCE=https://example.okta.com/oauth2/v1/token
OKTA_JWT_KID=my-key-id
OKTA_JWT_PEM_PATH=secrets/private_key.pem
SERVER_PORT=8080