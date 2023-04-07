import { NgModule, } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppMaterialModule } from './app.material.module';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'
import { AccessTokenService } from './services/access-token-service/access-token.service';
import { AuthorizedComponent } from './components/authorized/authorized.component';
import { BlogComponent } from './components/blog/blog.component';
import { HttpBAInterceptor } from './misc/interceptor/HttpBAInterceptor';
import { DatePipe } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { CommentComponent } from './components/comment/comment.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    AuthorizedComponent,
    BlogComponent,
    HeaderComponent,
    CommentComponent,
    UserProfileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    AppMaterialModule,
    HttpClientModule,
    AppRoutingModule,
  ],
  providers: [DatePipe, AccessTokenService,
    { provide: HTTP_INTERCEPTORS, 
      useClass: HttpBAInterceptor,
       multi: true 
      }],
  bootstrap: [AppComponent]
})
export class AppModule { }
