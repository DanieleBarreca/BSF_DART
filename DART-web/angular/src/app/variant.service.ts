import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import { AuthenticationService } from './authentication.service';
import {EMPTY, Observable} from "rxjs";
import {CoverageDataService} from "./CoverageDataService";

const variantUrl = "v1/queryVCF";
const variantCoverageUrl = variantUrl + "/coverage";

@Injectable()
export class VariantService implements CoverageDataService{

  constructor(private http: HttpClient, private auth: AuthenticationService ) {}

  submitQuery(query: any):Observable<string> {
    let options = this.auth.getOptionsForText();
    if (this.auth.getQueryToken()){
      options.addParam("userToken",this.auth.getQueryToken())
    }

    return <Observable<string>> this.http.post(environment.server + variantUrl, query, options);
  }

  getResults(queryId: string, first: number = null, pageSize: number = null) {
    if (queryId!=null) {
      let options = this.auth.getOptions();
      if (first) {
        options.addParam('first', first.toString());
      }
      if (pageSize) {
        options.addParam('pageSize', pageSize.toString());
      }

      return this.http.get(environment.server + variantUrl + "/" + queryId, options);
    }
    return EMPTY;
  }

  getCoverageResults(queryId: string, first: number, pageSize: number, geneFilter: string, statusFilter:string) {
    if (queryId!=null){
      let options = this.auth.getOptions();
      options.addParam('first', first.toString());
      options.addParam('pageSize', pageSize.toString());
      if (geneFilter){
        options.addParam('geneFilter', geneFilter);
      }
      if (statusFilter){
        options.addParam('statusFilter', statusFilter);
      }

      return this.http.get(environment.server + variantCoverageUrl + "/" + queryId, options);
    }
    return EMPTY;
  }

}
