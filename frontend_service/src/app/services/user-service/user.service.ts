import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { User } from './model/user';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  currentUser: User;
  private loggedIn : Boolean = false; 


  constructor(private httpClient: HttpClient) {
  }

  /**
   * Setter function so we can see if a user is logged in or not
   * @param isLoggedIn 
   */
  setUserLoggedIn(isLoggedIn : boolean) { 
    this.loggedIn = isLoggedIn;
  }

  /**
   * return the value for loggedIn
   * @returns 
   */
  isLoggedIn() : Boolean { 
    return this.loggedIn;
  }

  /**
   * get the currently logged in user
   * @returns 
   */
  async getUser() : Promise<User> {
    let url = environment.url.api.baseIP + '/user-service' + '/getUser';
    this.currentUser = await firstValueFrom(this.httpClient.get<User>(url));
    return this.currentUser;
  }

  /**
   * return the currently logged in user
   * @returns 
   */
  getCurrentUser(): User  {
      return this.currentUser;
  }
}
