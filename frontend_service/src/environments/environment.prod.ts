/**
 * Define environment endpoints 
 * where to send the requests
 */
export const environment = {
  production: false,
  "url": {
      "api": {
              baseIP: "https://localhost/api/v0",
              baseAuthServerPath: "/auth-server",
              baseUserServicePath: "/user-service",
              baseTokenService: "/token-service",
              tokenEndpoint: 'https://localhost/api/v0/token-service/token',
              anonTokenEndpoint: 'https://localhost/api/v0/token-service/anonymous_token',
              refreshTokenEndpoint: 'https://localhost/api/v0/token-service/refresh_token',
              hostname: "127.0.0.1"
          },
  }
}