import { Component, OnInit } from '@angular/core';
import { GroupAdminService } from '../group-admin.service';
import { AuthenticationService } from '../authentication.service';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { GroupUserAddComponent } from '../group-user-add/group-user-add.component';

@Component({
  selector: 'app-group-user-management',
  templateUrl: './group-user-management.component.html',
  styleUrls: ['./group-user-management.component.css']
})
export class GroupUserManagementComponent implements OnInit {
  searchLoginString: string;
  searchEmailString: string;
  message: string;
  users= [];

  constructor(private adminService: GroupAdminService, private auth: AuthenticationService, public modalService: BsModalService) { }

  ngOnInit() {
    this.getData();
  }

  private getData() {
    this.adminService.getAllUsers().subscribe(
      (data) => {
        if (data['status'] == "OK"){
          this.users = data['payload']
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized to see users")
        }else if (data['status'] == "ERROR"){
          window.alert("Error while fetching users: \n"+data['message'])          
        }
      }
    )
  }

  getPermissions(user){
    return user.permissions[this.auth.getUserGroup()];
  }

  setPermissions(userName, permissions){
    this.adminService.setPermissionsForUser(userName, permissions).subscribe(
      (data) => {
        if (data['status'] == "OK"){
          this.getData();
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized to set permissions for user "+userName )
        }else if (data['status'] == "ERROR"){
          window.alert("Error while setting permissions for user "+userName +"\n"+data['message'])          
        }
    })
  }

  removeUser(userName){
    if (window.confirm('Are you sure to remove user '+userName)){
      this.adminService.removeExistingUserFromGroup(userName).subscribe(
        (data) => {
          if (data['status'] == "OK"){
            this.getData();
          }else if (data['status'] == "AUTHORIZATION_ERROR") {
            window.alert("Not authorized to remove user "+userName )
          }else if (data['status'] == "ERROR"){
            window.alert("Error while removing user "+userName +"\n"+data['message'])          
          }
      })
    }
  }

  resetPassword(userName){
    if (window.confirm('Are you sure to reset password for user '+userName)){      
      this.adminService.resetPasswordForUser(userName).subscribe(
        (data) => {
          if (data['status'] == "OK"){
            window.alert("Temporary Password: "+data['payload']);
          }else if (data['status'] == "AUTHORIZATION_ERROR") {
            window.alert("Not authorized to reset password for user "+userName )
          }else if (data['status'] == "ERROR"){
            window.alert("Error while resetting password for user "+userName +"\n"+data['message'])          
          }
      })
    }
  }

  showNewUserModal(){
    let modalRef = this.modalService.show(GroupUserAddComponent);
    (modalRef.content as GroupUserAddComponent).userSubmitted.subscribe(
      () => this.getData()
    );
  }

}
