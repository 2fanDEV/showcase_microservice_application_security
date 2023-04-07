import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute, Params, RouterModule } from '@angular/router';
import { firstValueFrom, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccessTokenService {
  code: String = '';
  constructor(private route: ActivatedRoute, private httpClient: HttpClient) {}

  /**
   * After we login and do the OAuth Flow, we get an 
   * Access Code that is inside the redirected URI
   * 
   * We are taking this access_code and send a request to 
   * the token service which then retrieves the 
   * access_token (JWT) from the authorization server.
   * 
   * Also in the case our JWT is expired
   * we look out for an refresh token in our cookies 
   * which then we use to get a new JWT and new refresh token.
   * @param loggedIn 
   */
  getAccessCodeFromParameters(loggedIn: Boolean) {
    this.getCookieValue("access_token");
    this.route.queryParams.subscribe(async (parameters) => {
      this.code = parameters['code'];
      if (
        this.code === undefined &&
        !this.doesCookieExistByName("refresh_token")
      ) {
        await this.getAnonymousBearerToken().then((x: Object) => {
          document.cookie = 'access_token=' + Object(x)['access_token'];
        });
      } else if( this.code != null && !this.doesCookieExistByName("refresh_token"))
      {
        this.getBearerToken(this.code, loggedIn);
      } else if( this.code == null && !this.doesCookieExistByName("access_token") && this.doesCookieExistByName("refresh_token"))
      {
        this.deleteCookie("refresh_token");
      }
    });
  }

  /**
   * Getting a Cookie Value by the Name of the cookie.
   * 
   * I have been using this stackoverflow post as a reference
   * but ended up doing a different approach
   * but this answer helped me with it especially the document.cookie.split(";") in line 62
   * https://stackoverflow.com/a/34308089
   * 
   * @param cookieName 
   */
  getCookieValue(cookieName: string) : string {
    if(this.doesCookieExistByName(cookieName))
    {

      let cookieArray : string[] = document.cookie.split(";");
      let searchedCookie: string = "";
      cookieArray.forEach((cookie) =>
      {
        if(cookie.startsWith(cookieName))
        {
          searchedCookie = cookie;
          searchedCookie =  searchedCookie.substring(cookieName.length + 1, searchedCookie.length);
        }
      })
        return searchedCookie;
    }
    return "";
  }

  // needed this for a bug in blog
  // just a timeout function
  timeOut(timeOutInMillis : number) : Promise<unknown> {
    return new Promise(resolve => setTimeout(resolve, timeOutInMillis));
  }

  /**
   * set the age of a cookie to 0 so it is deleted
   * @param cookieName 
   */
  deleteCookie(cookieName : string)
  {
    document.cookie = cookieName + "=" + ";max-age=0";
  }

  /**
   * Check if a cookie exists by its name.
   * @param cookieName 
   * @returns 
   */
  doesCookieExistByName(cookieName: String) : boolean {
    let allCookies = document.cookie;
    let searchingCookieName = cookieName + "=";
    if(allCookies.indexOf("; " + searchingCookieName) == -1)
      if(allCookies.indexOf(searchingCookieName) == -1)
          return false;
      else
          return true;
    return true;
  }

  /**
   * Get an anonymous token
   * @returns 
   */
  async getAnonymousBearerToken() : Promise<Object> {
    let url = environment.url.api.anonTokenEndpoint;
    return firstValueFrom(this.httpClient.post(url, null));
  }

  /**
   * Get a new JWT post function if there is a refresh token as function parameter present
   * @param refresh_token 
   * @returns 
   */
  getNewBearerTokenFromRefreshToken(refresh_token: String) : Promise<Object>
  {
      let url = environment.url.api.refreshTokenEndpoint;
      return firstValueFrom(this.httpClient.post<Object>(url, refresh_token));
    //  this.httpClient.post(url, document.cookie.)
  }

  /**
   * Get a JWT Token by sending the access token to the token endpoint
   * @param code 
   * @param loggedIn 
   * @returns 
   */
  getBearerToken(code: String, loggedIn: Boolean): boolean {
    let url = environment.url.api.tokenEndpoint;
    this.httpClient.post(url, this.code).subscribe(
      (x: Object) => {
        document.cookie = 'access_token=' + Object(x)['access_token'];
        document.cookie = 'refresh_token=' + Object(x)['refresh_token'];
        loggedIn = true;
        window.location.href="/";
      },
    );

    return true;
  }
}
