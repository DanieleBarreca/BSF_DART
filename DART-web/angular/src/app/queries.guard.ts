import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class QueriesGuard implements CanActivate {

  constructor(private auth: AuthenticationService, private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {

    let permissions = this.auth.getPermissions();

    if (permissions && permissions.canQueryVCF){
      return true;
    }
    
    this.router.navigate([''])
    return false;
  }
}
