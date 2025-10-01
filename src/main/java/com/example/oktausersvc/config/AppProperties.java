package com.example.oktausersvc.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Okta okta = new Okta();
    private Http http = new Http();
    private Security security = new Security();

    public Okta getOkta() {
        return okta;
    }

    public Http getHttp() {
        return http;
    }

    public Security getSecurity() {
        return security;
    }

    @Validated
    public static class Okta {
        @NotBlank
        private String domain;
        @NotBlank
        private String tokenPath;
        @NotBlank
        private String apiBase;
        @NotBlank
        private String clientId;
        @NotBlank
        private String scope;
        private Jwt jwt = new Jwt();

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getTokenPath() {
            return tokenPath;
        }

        public void setTokenPath(String tokenPath) {
            this.tokenPath = tokenPath;
        }

        public String getApiBase() {
            return apiBase;
        }

        public void setApiBase(String apiBase) {
            this.apiBase = apiBase;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public Jwt getJwt() {
            return jwt;
        }

        public void setJwt(Jwt jwt) {
            this.jwt = jwt;
        }

        @Validated
        public static class Jwt {
            @NotBlank
            private String audience;
            @NotBlank
            private String kid;
            @NotBlank
            private String pemPath;

            public String getAudience() {
                return audience;
            }

            public void setAudience(String audience) {
                this.audience = audience;
            }

            public String getKid() {
                return kid;
            }

            public void setKid(String kid) {
                this.kid = kid;
            }

            public String getPemPath() {
                return pemPath;
            }

            public void setPemPath(String pemPath) {
                this.pemPath = pemPath;
            }
        }
    }

    public static class Http {
        private int connectTimeoutMs = 3000;
        private int readTimeoutMs = 5000;

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int v) {
            this.connectTimeoutMs = v;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(int v) {
            this.readTimeoutMs = v;
        }
    }

    public static class Security {
        private ApiKey apiKey = new ApiKey();

        public ApiKey getApiKey() {
            return apiKey;
        }

        public void setApiKey(ApiKey apiKey) {
            this.apiKey = apiKey;
        }

        public static class ApiKey {
            private boolean enabled = false;
            private String value = "change-me";

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
