import { ɵparseCookieValue } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EnvironmentInjector, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AccessTokenService } from 'src/app/services/access-token-service/access-token.service';
import { LoginService } from 'src/app/services/loginService/login.service';
import { UserService } from 'src/app/services/user-service/user.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  constructor(private loginService : LoginService,
              private accessTokenService: AccessTokenService) {}

  loggedIn: boolean = false;

  ngOnInit(): void {
    this.accessTokenService.getAccessCodeFromParameters(this.loggedIn);
    this.isAUserLoggedIn();
  }

  isAUserLoggedIn() { 
    this.loggedIn = this.loginService.isAUserLoggedIn();
  }
  

  logout(): any {
      this.loginService.logout();
  }


  login(): any {
    this.loginService.login();
  }

  register(): any 
  {
    this.loginService.register();
  }
}
