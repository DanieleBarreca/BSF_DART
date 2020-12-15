import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';



import { AuthenticationService } from './authentication.service';
import {environment} from '../environments/environment';

const vcfUrl = 'v1/vcf';

@Injectable()
export class VcfService {

  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  getVcfs() {
    return this.http.get(environment.server + vcfUrl, this.auth.getOptions());
  }

  getVcfById(fileId: number): Observable<Object> {
    let options = this.auth.getOptions();
    options.addParam('fileId',fileId.toString());

    return this.http.get(environment.server + vcfUrl, options);
  }

}
