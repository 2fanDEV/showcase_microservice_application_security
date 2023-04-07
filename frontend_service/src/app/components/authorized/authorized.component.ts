import { Component, OnInit } from '@angular/core';
import { AccessTokenService } from 'src/app/services/access-token-service/access-token.service';

@Component({
  selector: 'app-authorized',
  templateUrl: './authorized.component.html',
  styleUrls: ['./authorized.component.css']
})
export class AuthorizedComponent implements OnInit {

  constructor(private accessTokenService : AccessTokenService) { }

  loggedIn : Boolean = false;

  ngOnInit(): void {
    this.accessTokenService.getAccessCodeFromParameters(this.loggedIn);
  }

}
