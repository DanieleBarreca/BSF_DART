import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class HomeGuard implements CanActivate {
  constructor(private auth: AuthenticationService, private router: Router) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {

    let permissions = this.auth.getPermissions();

    if (permissions && permissions.canQueryVCF) {
      this.router.navigate(['queries']);
      return false;
    }else if (permissions && permissions.canViewReport){
      this.router.navigate(['reports'])
      return false;
    }else if (permissions && permissions.isAdmin){
      this.router.navigate(['group-admin'])
      return false;
    }    


    return true;
  }
}
