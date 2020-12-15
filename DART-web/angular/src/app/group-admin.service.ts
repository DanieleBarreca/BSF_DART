import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';

const groupAdminURL = "v1/group-admin";

@Injectable()
export class GroupAdminService {

  constructor(private http: HttpClient, private auth:AuthenticationService) { }

  getUserWithMail(email: string){
    let endpoint = "/user/"+email;
    //GET
    return this.http.get(environment.server+groupAdminURL+endpoint,this.auth.getOptions());
  }

  getAllUsers() {
    let endpoint = "/user"
    //GET
    return this.http.get(environment.server+groupAdminURL+endpoint,this.auth.getOptions());

  }

  addExistingUserToGroup(user: string){
    let endpoint = "/user/"+user
    //PUT
    return this.http.put(environment.server+groupAdminURL+endpoint,{}, this.auth.getOptions());
  }

  removeExistingUserFromGroup(user: string) {
    let endpoint = "/user/"+user
    //DELETE
    return this.http.delete(environment.server+groupAdminURL+endpoint, this.auth.getOptions());
  }

  addNewUserToGroup(user: any){
    let endpoint = "/user"
    //POST
    return this.http.post(environment.server+groupAdminURL+endpoint,user, this.auth.getOptions());
  }

  setPermissionsForUser(user: string, permissions: any) {
    let endpoint = "/user/"+user+"/permissions"
    //POST
    return this.http.post(environment.server+groupAdminURL+endpoint,permissions, this.auth.getOptions());
  }

  resetPasswordForUser(user: string){
    let endpoint = "/user/"+user+"/password"
    //GET
    return this.http.get(environment.server+groupAdminURL+endpoint, this.auth.getOptions());
  }

  getVcfs() {
    let endpoint = "/vcf"

    return this.http.get(environment.server + groupAdminURL+ endpoint, this.auth.getOptions());

  }

  removeVCF(vcfID) {
    let endpoint = "/vcf/"+vcfID;

    return this.http.delete(environment.server + groupAdminURL+ endpoint, this.auth.getOptions());

  }

  getBEDs() {
    let endpoint = "/bed"

    return this.http.get(environment.server + groupAdminURL+ endpoint, this.auth.getOptions());

  }

  removeBED(bedID) {
    let endpoint = "/bed/"+bedID;

    return this.http.delete(environment.server + groupAdminURL+ endpoint, this.auth.getOptions());

  }

  postBED(bedFile: File, genome: string) {
    let endpoint = "/bed";

    let formData: FormData = new FormData();
    formData.append('file', bedFile, bedFile.name);

    let options=this.auth.getOptionsForFileUpload();
    options.addParam('genome', genome);

    return this.http.post(environment.server + groupAdminURL + endpoint, formData, options);
  }

}
