import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {EMPTY, Observable} from "rxjs";
import {environment} from "../environments/environment";
import {Report} from "./reports/Report";
import {CoverageDataService} from "./CoverageDataService";

const reportUrl = "v1/report";

@Injectable()
export class ReportService implements CoverageDataService{

  constructor(private http: HttpClient, private authService: AuthenticationService) {
  }

  submitQuery(queryId: string):Observable<number> {

    let options = this.authService.getOptions();

    return <Observable<number>> this.http.post(environment.server + reportUrl, queryId, options);
  }

  getAllReports(): Observable<Report[]> {
    let options = this.authService.getOptions();

    return <Observable<Report[]>> this.http.get(environment.server + reportUrl, options);
  }

  getReport(reportId: number): Observable<Report> {
    let options = this.authService.getOptions();

    return <Observable<Report>> this.http.get(environment.server + reportUrl+"/"+reportId, options);
  }

  getCoverageResults(queryId: string, first: number, pageSize: number, geneFilter: string, statusFilter: string): Observable<any> {
    if (queryId!=null){
      let options = this.authService.getOptions();

      options.addParam('first', first.toString());
      options.addParam('pageSize', pageSize.toString());
      if (geneFilter){
        options.addParam('geneFilter', geneFilter);
      }
      if (statusFilter){
        options.addParam('statusFilter', statusFilter);
      }

      return this.http.get(environment.server + reportUrl + "/coverage/" + queryId, options);
    }
    return EMPTY;
  }

  getReportVariants(reportId: number): Observable<any> {
    if (reportId!=null){
      let options = this.authService.getOptions();


      return this.http.get(environment.server + reportUrl + "/variants/" + reportId.toString(), options);
    }
    return EMPTY;
  }
}
