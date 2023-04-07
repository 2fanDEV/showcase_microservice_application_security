import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { LoginService } from 'src/app/services/loginService/login.service';
import { UserService } from 'src/app/services/user-service/user.service';
import { environment } from 'src/environments/environment';
import { UserProfileStats } from './model/UserProfileStats';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
})
export class UserProfileComponent implements OnInit {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private httpClient: HttpClient,
    private userService: UserService,
    private loginService: LoginService
  ) {}

   userPosts : boolean = true;
   userLikedPosts : boolean = false;
   userComment: boolean = false;
   profileUser: UserProfileStats;
   baseIP: String = environment.url.api.baseIP;
   username: string = "";

   /**
    * If the user is not logged in, then 
    * reroute to login 
    * 
    * Otherwise get the username to show on profile 
    * from the URL
    */
  async ngOnInit(): Promise<void> {
    if(!this.userService.isLoggedIn())
    this.loginService.login();

    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    
    this.username = this.activatedRoute.snapshot.url[0].path;

    /**
     * Get the information for a user from statistics-service
     */
    await this.getUserProfileOverviewInformation(this.username).then((res) => {
      this.profileUser = res;
    });
  }

  /**
   * Switch case to see which blogs are supposed to load
   * @param $event 
   */
  tabChange($event : number) 
  {
    switch($event)
    {
      case 0: 
        this.userPosts = true;
        this.userLikedPosts = false;
        this.userComment = false;
        break;
      case 1: 
        this.userLikedPosts = true;
        this.userPosts = false;
        this.userComment = false;
        break;
      case 2: 
        this.userPosts = false;
        this.userLikedPosts = false;
        this.userComment = true;
        break;
      default:
        this.userPosts = true;
        break;
    }
  }

  /**
   * The promise that sends a request to the statistics service
   * @param username 
   * @returns 
   */
  async getUserProfileOverviewInformation(username: string): Promise<UserProfileStats> {
    return firstValueFrom(
      this.httpClient.get<any>(
        this.baseIP +
          '/statistics-service/user/getUserStats?username=' + username
      )
    );
  }
}
