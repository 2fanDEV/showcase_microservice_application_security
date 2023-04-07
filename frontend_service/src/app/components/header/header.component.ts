import { Component, OnInit } from '@angular/core';
import { _MatTabGroupBase } from '@angular/material/tabs';
import { ActivatedRoute, Router } from '@angular/router';
import { AccessTokenService } from 'src/app/services/access-token-service/access-token.service';
import { LoginService } from 'src/app/services/loginService/login.service';
import { User } from 'src/app/services/user-service/model/user';
import { UserService } from 'src/app/services/user-service/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  constructor(
    private userService: UserService,
    private loginService: LoginService,
    private routeLocator: ActivatedRoute,
    private router : Router,
    private accessTokenService : AccessTokenService
  ) {}

  user: User;
  userName: String;
  loggedIn: Boolean = false;
  async ngOnInit() {

    if(this.router.navigated)
      this.routeToFeed();

    this.loggedIn = this.loginService.isAUserLoggedIn();
    console.log('HEADER LOGIN? ' + this.loggedIn);
    if (this.loggedIn) {
      await this.userService.getUser().then((res : User) => { 
         this.user = res;
         this.userName = res.userName;
      });
      console.log(this.user);
    }
  }

  routeToFeed() : void { 
    this.router.navigateByUrl("/feed");
  }


  routeToProfile() : void { 
    if(this.userName != undefined)
    {
    this.router.navigateByUrl("/" + this.userName);
    }
  }


}
