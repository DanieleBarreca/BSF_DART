import {Observable} from "rxjs";

export interface CoverageDataService {
  getCoverageResults(queryId: string, first: number, pageSize: number, geneFilter: string, statusFilter:string): Observable<any>;
}
