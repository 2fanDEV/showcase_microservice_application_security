import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/environments/environment';
import { AccessTokenService } from '../access-token-service/access-token.service';
import { UserService } from '../user-service/user.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

constructor(private httpClient: HttpClient,
    private route : ActivatedRoute,
    private accessTokenService : AccessTokenService,
    private userService : UserService,
    ) { }

  loggedIn: boolean = false;
 
/**
 * If there is an access_token and refresh token present
 * return true, else return false.
 * @returns 
 */
isAUserLoggedIn() { 
    if(this.accessTokenService.doesCookieExistByName("refresh_token") && this.accessTokenService.doesCookieExistByName("access_token"))
    {
      this.loggedIn=true;
      this.userService.setUserLoggedIn(this.loggedIn);
    }
    return this.loggedIn;
  }
  
/**
 * Delete all cookies 
 * Set loggedIn to false
 * reroute to logout endpoint of auth-service 
 * 
 * the deletion of the cookies was done with the help of 
 * this stackoverflow post, as i did not know how to delete them from the cookies tab: 
 * 
 * https://stackoverflow.com/a/60931519
 */
logout(): any {
    document.cookie = "access_token" + "=" + ";max-age=0";
    document.cookie = "refresh_token" + "=" + ";max-age=0";
    document.cookie = "JSESSIONID" + "=" + ";max-age=0";
    this.loggedIn = false;
    this.userService.setUserLoggedIn(this.loggedIn);
    let urlBaseAPI: string = environment.url.api.baseIP;
    window.location.href= urlBaseAPI + "/auth-service/logout"
}

/**
 * go to authorization endpoint of auth-service as
 * there is no token present
 * 
 * 
 * the path is done with the documentation of the oauth2 RFC 
 * and the providersettings from the spring authorization server
 * and the settings we declared in the registeredclient repository bean
 * 
 * Ref:
 * https://www.rfc-editor.org/rfc/rfc6749#section-4.1.1
 *  
 */
login(): any {
     let urlBaseAPI: string = environment.url.api.baseIP;
     window.location.href= urlBaseAPI + "/auth-service/oauth2/authorize?response_type=code&client_id=frontend-client&scope=openid&redirect_uri=https://" + environment.url.api.hostname + "/authorized";
}

/**
 * go to registering page of auth service
 */
register(): any 
{
    let urlBaseAPI: string = environment.url.api.baseIP;
    window.location.href= urlBaseAPI + "/auth-service/register";
}
}
