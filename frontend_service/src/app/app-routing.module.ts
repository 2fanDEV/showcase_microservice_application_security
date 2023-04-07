import { NgModule, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule, Routes } from '@angular/router';
import { AuthorizedComponent } from './components/authorized/authorized.component';
import { BlogComponent } from './components/blog/blog.component';
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { AccessTokenService } from './services/access-token-service/access-token.service';

//https://angular.io/guide/router
//https://angular.io/tutorial/tour-of-heroes/toh-pt5#add-the-approutingmodule
const routes: Routes = [
  { path: '', component: HeaderComponent },
  { path: 'login', component: LoginComponent },
  { path: 'authorized', component: AuthorizedComponent },
  { path: 'feed', component : BlogComponent },
  { path: '**', component : UserProfileComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes,  {onSameUrlNavigation: "reload"})],
  exports: [RouterModule],
})

export class AppRoutingModule {}
