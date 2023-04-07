import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import {
  throwError,
  Observable,
  catchError,
  EMPTY,
  EmptyError,
  of,
  async,
} from 'rxjs';
import { AccessTokenService } from 'src/app/services/access-token-service/access-token.service';


/**
 * This is a custom HttpInterceptor
 * 
 * This is responsible to add the JWT to the authorization header of each request
 * 
 * This was done with the documentation of angular.
 * https://angular.io/guide/http#intercepting-requests-and-responses
 * 
 */
@Injectable()
export class HttpBAInterceptor implements HttpInterceptor {
  constructor(
    private accessTokenService: AccessTokenService,
    private _snackBar: MatSnackBar
  ) {}

  alreadyDone : boolean = false;

  /**
   * This is a snackbar which opens at the 
   * bottom of the page to display additional 
   * information if something went wrong like expired token
   * @param message 
   * @param action 
   * @param duration 
   */
  snackBarOpen(
    message: string,
    action: string | undefined,
    duration: MatSnackBarConfig<any> | undefined
  ) {
    this._snackBar.open(message, action, duration);
  }

  /**
   * Actual interception function
   * @param req 
   * @param next 
   * @returns 
   */
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    /*
      Get the bearer and refresh token from the cookies
     */
    let bearerToken = this.accessTokenService.getCookieValue('access_token');
    let refreshToken = this.accessTokenService.getCookieValue(' refresh_token');

    /*
      if our token is not empty, 
      we clone our requests, delete the cookies that are 
      send automatically and add the bearer token to the authoirzation header
    */
    if (bearerToken) {
      req = req.clone({
        headers: req.headers.delete(
          'Cookie',
          req.headers.get('Cookie')?.toString()
        ),
        setHeaders: {
          Authorization: 'Bearer ' + bearerToken,
        },
      });
      /**
       * Resume with sending the request
       */
      return next.handle(req).pipe(
        catchError((err: HttpErrorResponse) => {
          // if the jwt token is expired, delete the token from our cookies and call updateRefreshedBearer
          // which is responsible to exchange the refresh token for a new set of refresh and access token
          if (err.headers.get('WWW-Authenticate')?.includes('Jwt expired at'))
            if (refreshToken != undefined && refreshToken != null) {
              this.accessTokenService.deleteCookie("access_token");
              this.updateRefreshedBearer(refreshToken);
              bearerToken =
                this.accessTokenService.getCookieValue('access_token');
              refreshToken =
                this.accessTokenService.getCookieValue(' refresh_token');
              // clone the request again and reset it for the actual request with no authorization header
              // as the token retrieved above, is not up to date
              if (bearerToken) {
                req = req.clone({
                  setHeaders: {
                    Authorization: "",
                  },
                });
              };
              // snackbar announcement to reload the page as otherwise, the token value in this
              // will be the old token
             this.snackBarOpen("TOKEN EXPIRED, RELOAD PAGE", undefined, { duration: 3000 })
              return next.handle(req);
            }
            // if a 408 is thrown, then the circuit breaker is active and we display the message over here/
            if(err.status === 408)
              this.snackBarOpen(err.error, undefined, { duration: 4000}) 
            // if the a 429 is thrown, then the snackbar tells us so
            if(err.status === 429)
              this.snackBarOpen("RateLimit exhausted", undefined, { duration: 4000}) 
            // dont try to resend the request as otherwise we are in an infinite loop
            return EMPTY;
        })
      );
    }
    // let the interceptor further handle the request if this function did
    // not already abort in before
    return next.handle(req);
  }
  /**
   * try to refresh the tokens 
   * @param refreshToken 
   */
  async updateRefreshedBearer(refreshToken: String) {
    await this.accessTokenService
      .getNewBearerTokenFromRefreshToken(refreshToken)
      .then((res: Object) => {
        console.log(res);
        document.cookie = 'access_token=' + Object(res)['access_token'];
        document.cookie = 'refresh_token=' + Object(res)['refresh_token'];
      });
  }
}
