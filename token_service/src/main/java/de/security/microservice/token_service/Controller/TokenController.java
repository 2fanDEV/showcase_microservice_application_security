package de.security.microservice.token_service.Controller;


import de.security.microservice.token_service.feign.AuthServerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


/**
 * This is a controller
 * responsible to retrieve the tokens
 * from the authorization server with the credentials
 * for the frontend-service since we dont want to
 * send out requests in the frontend with the client secret
 */
@RestController
@RequestMapping("/api/services/token-service")
public class TokenController {

	AuthServerFeign authServerFeign;

	BCryptPasswordEncoder bCryptPasswordEncoder;


	@Value("${custom-frontend-client_id}")
	String frontEndClientId;

	@Value("${custom-frontend-client_secret}")
	String frontEndClientSecret;

	@Value("${custom-anonymous-client_secret}")
	String anonymousClientSecret;

	@Value("${custom-anonymous-client_id}")
	String anonymousClientId;

	@Value("${host-value}")
	String hostValue;

	@Autowired
	TokenController(AuthServerFeign authServerFeign) {
		this.authServerFeign = authServerFeign;
	}

	/**
	 * getting the anonymous token with the grant_type
	 * client credentials that we set in the registeredClient repository
	 *
	 * Ref:
	 * https://www.rfc-editor.org/rfc/rfc6749#section-4.1.1
	 *
	 * @return {@link String}
	 */
	@PostMapping("/anonymous_token")
	public String getAnonymousToken() {
		String grantType = "grant_type=client_credentials&";
		String clientId = "client_id=" + anonymousClientId + "&";
		String clientSecret = "client_secret=" + anonymousClientSecret;
		String bodyQueryString = grantType + clientId + clientSecret;
		return authServerFeign.getAnonymousToken(bodyQueryString);
	}


	/**
	 * getting the access token for a logged in user
	 * that sending the authorization code retrieved in the request
	 *
	 * Ref for the body param string: https://www.rfc-editor.org/rfc/rfc6749#section-4.1.3
	 *
	 * @param authorizationCode
	 * @return {@link String}
	 */
	@CrossOrigin
	@PostMapping("/token")
	public String getBearerToken(@RequestBody String authorizationCode)
	{
		System.out.println("\n\n AUTH-CODE: " + authorizationCode + "\n\n");

		String grantType = "grant_type=authorization_code&";
		String client_secret = "client_secret=" + frontEndClientSecret + "&";
		String client_id = "client_id=" + frontEndClientId + "&";
		String redirect_uri = "redirect_uri=" + this.hostValue + "/authorized&";
		String scope = "scope=openid&";
		String code = "code=" + authorizationCode;

		String bodyQueryString = grantType + client_id + client_secret + redirect_uri + scope + code;
		System.out.println(bodyQueryString);
		return authServerFeign.getBearerToken(bodyQueryString);
	}

	/**
	 * refreshing the access token when it expires with
	 * the refresh token that is coming with the access token
	 * we are also getting a new refresh token with this call
	 *
	 * Ref for the bodyparam string:
	 * https://www.rfc-editor.org/rfc/rfc6749#section-2.3.1
	 * https://www.rfc-editor.org/rfc/rfc6749#section-6
	 *
	 * @param refreshToken
	 * @return
	 */
	@PostMapping("/refresh_token")
	public String getRefreshedBearerToken(@RequestBody String refreshToken)
	{
		String grantType = "grant_type=refresh_token&";
		String client_secret = "client_secret=" + frontEndClientSecret + "&";
		String client_id = "client_id=" + frontEndClientId + "&";
		String redirect_uri = "redirect_uri=" + this.hostValue + "/authorized&";
		String scope = "scope=openid&";
		String refresh_token = "refresh_token=" + refreshToken;

		String bodyQueryString = grantType + client_id + client_secret + redirect_uri + scope + refresh_token;
		System.out.println(bodyQueryString);

		return authServerFeign.getRefreshedBearerToken(bodyQueryString);
	}

}
