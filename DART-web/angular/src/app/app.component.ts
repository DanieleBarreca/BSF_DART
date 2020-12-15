import { Component } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { Router } from '../../node_modules/@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { UserComponent } from './user/user.component';
import { UserChangePasswordComponent } from './user-change-password/user-change-password.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(public authService:AuthenticationService, public router: Router, private modalService: BsModalService){

  }

  loggedIn() : boolean {
    return (this.authService.getUser()!=null);
  }

  logout(){
    this.authService.logout();
    this.router.navigate(["/login"])
  }

  setUserGroup(group){
    this.authService.setUserGroup(group);
    this.router.navigate(["/"])
  }

  canViewQueries(){
    if (this.loggedIn() && this.authService.getPermissions() && this.authService.getPermissions().canQueryVCF){
      return true;
    }

    return false;
  }

  canAdminGroup(){
    if (this.loggedIn() && this.authService.getPermissions() && this.authService.getPermissions().isAdmin){
      return true;
    }

    return false;
  }

  showUserInfo(){
    let user = this.authService.getUserWithDetails();
    if (user!=null) {
      this.modalService.show(UserComponent,{class: 'wide-modal', initialState: {user: this.authService.getUserWithDetails()}})
    }
  }

  showChangePassword(){
      this.modalService.show(UserChangePasswordComponent)
  }

}
