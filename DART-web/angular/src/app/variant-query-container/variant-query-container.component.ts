import {timer as observableTimer, Observable, EMPTY} from 'rxjs';
import { Component, Input, Output, EventEmitter, OnChanges, ViewChild, TemplateRef, SimpleChanges } from '@angular/core';
import { QueryService } from '../query.service';
import {ActivatedRoute, Router} from '@angular/router';
import { VariantFilterQueryComponent } from '../variant-filter-query/variant-filter-query.component';
import { VariantService } from '../variant.service';
import { takeWhile, switchMap, filter, tap } from "rxjs/operators";
import * as clonedeep from 'lodash.clonedeep'

@Component({
  selector: 'app-variant-query-container',
  templateUrl: './variant-query-container.component.html',
  styleUrls: ['./variant-query-container.component.css']
})
export class VariantQueryContainerComponent implements OnChanges {
  @ViewChild('queryComponent') variantQuery: VariantFilterQueryComponent;

  @Input() cachedQueryInfo: any;
  @Input() showButtons = true;
  @Input() editableQuery = false;

  @Output() onCopyQuery = new EventEmitter<any>();
  @Output() onDeleteQuery = new EventEmitter<any>();

  @Output() onResultsAvailable = new EventEmitter<any>();
  @Output() onCoverageAvailable = new EventEmitter<any>();

  isQueryPanelVisible: boolean = false;
  queryRunning = false;

  totalCount;
  queryCount;
  coverageCount;

  constructor(private activatedRoute: ActivatedRoute,private queryService: QueryService, private variantService: VariantService, private router: Router) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.cachedQueryInfo){ // && (changes.cachedQueryInfo.firstChange || !deepEqual(changes.cachedQueryInfo.currentValue, changes.cachedQueryInfo.previousValue))){
      this.setQuery();
    }
  }

  delete() {
    if (this.isLinkedUUID()) {
      this.queryService.deleteQuery(this.cachedQueryInfo.uuid).subscribe(
        (data) => this.onDeleteQuery.emit(this.cachedQueryInfo.uuid),
        (err) => console.error(err)
      );
    }
  }

  copy() {
    this.onCopyQuery.emit(clonedeep(this.cachedQueryInfo));
  }

  public viewResults() {
    if (this.canSeeVariants()){
      this.router.navigate(["/query",{queryId:this.cachedQueryInfo.uuid}])
    }
  }

  public viewCoverage() {
    if (this.canSeeCoverage()){
      this.router.navigate(["/query",{queryId:this.cachedQueryInfo.uuid,showCoverage:true}])
    }
  }

  isLinkedUUID(): boolean {
    return this.cachedQueryInfo &&  this.cachedQueryInfo.uuid
  }

  submit(){

    let submittedQueryObs = this.variantQuery.submitQuery();
    if (submittedQueryObs) {

      submittedQueryObs.pipe(switchMap(queryId =>
        queryId ? this.queryService.getQuery(queryId) : EMPTY
      )).subscribe(
        (cachedQueryInfo) => {
          this.isQueryPanelVisible = false;
          if (cachedQueryInfo && cachedQueryInfo['uuid']) {
            this.router.navigate(['.', {queryId: cachedQueryInfo['uuid']}], {relativeTo: this.activatedRoute});
          }else{
            this.router.navigate(['.'], {relativeTo: this.activatedRoute});
          }
        },
        (error1 ) => {
          this.router.navigate(['.'], {relativeTo: this.activatedRoute});
        }
      )
    }
  }

  setQuery(cachedQueryInfo?) {
    if(cachedQueryInfo) this.cachedQueryInfo = cachedQueryInfo;


    if (this.isLinkedUUID()) {
      this.queryRunning = this.cachedQueryInfo.resultStatus!="FINISHED";

      let queryId = this.cachedQueryInfo.uuid;

      if (this.cachedQueryInfo.countStatus=="FINISHED"||this.cachedQueryInfo.resultStatus=="FINISHED" || this.cachedQueryInfo.coverageStatus=="FINISHED"){
        this.variantService.getResults(this.cachedQueryInfo.uuid, -1, 0).subscribe(
          (response) => this.setResults(response),
          (err) => console.error(err)
        )
      }

      if (this.cachedQueryInfo.countStatus == "RUNNING" || this.cachedQueryInfo.resultStatus == "RUNNING" || this.cachedQueryInfo.coverageStatus=="RUNNING"){
        observableTimer(500, 500)
          .pipe(
            switchMap(() => this.queryService.getQuery(queryId)),
            filter(response => response!=null),
            takeWhile(() => (
              this.cachedQueryInfo.uuid == queryId &&
              (this.cachedQueryInfo.countStatus == "RUNNING" || this.cachedQueryInfo.resultStatus == "RUNNING"|| this.cachedQueryInfo.coverageStatus=="RUNNING"))
            ),
            tap((response) => {
              this.cachedQueryInfo = response;
              this.queryRunning = this.cachedQueryInfo.resultStatus!="FINISHED";
            }),
            filter(response => (
              response['countStatus']=="FINISHED" ||
              response['resultStatus']=="FINISHED" ||
              response['coverageStatus']=="FINISHED"
            )),
            switchMap(response =>  this.variantService.getResults(response['uuid'], -1, 0))
          ).subscribe(
            (response) => this.setResults(response),
            (err) => console.error(err)
          );
        }
    } else{
      this.totalCount = null;
      this.queryCount = null;
      this.coverageCount = null;
    }
  }

  private setResults(countResponse) {
    if (countResponse) {
      let countStatus = countResponse['countResult']['status'];
      let variantStatus = countResponse['variantResult']['status'];
      let coverageStatus = countResponse['coverageResult']['status'];

      if (countStatus=="FINISHED"){
        this.totalCount = countResponse['countResult']['totalCount']
      }else{
        this.totalCount = null;
      }
      if (coverageStatus=="FINISHED"){
        this.coverageCount = countResponse['coverageResult']['count'];
        this.onCoverageAvailable.emit(this.cachedQueryInfo);
      }else{
        this.coverageCount = null
      }

      if (countStatus!="RUNNING" && variantStatus=="FINISHED"){
        this.queryCount = countResponse['variantResult']['queryCount'];
        if (this.canSeeVariants()) {
          this.onResultsAvailable.emit(this.cachedQueryInfo);
        }
      }else {
        this.queryCount = null;
      }
    }
  }

  canSeeVariants() {
    return (
      this.cachedQueryInfo &&
      this.cachedQueryInfo.resultStatus=="FINISHED" &&
      this.cachedQueryInfo.countStatus!="RUNNING" &&
      (this.cachedQueryInfo.countStatus!="FINISHED" || (this.queryCount>=this.totalCount))
    )
  }

  canSeeCoverage() {
    return (
      this.cachedQueryInfo &&
      this.cachedQueryInfo.coverageStatus=="FINISHED" &&
      this.coverageCount
    )
  }

  getCountColor() {
    if (this.cachedQueryInfo && this.cachedQueryInfo.countStatus=="ERROR") return "label-danger";
    if (this.cachedQueryInfo && this.cachedQueryInfo.countStatus=="TIMEOUT") return "label-warning";
    if (this.cachedQueryInfo && this.cachedQueryInfo.countStatus=="FINISHED") return "label-success";

    return "label-default";
  }

  getCountMessage() {
    if (this.cachedQueryInfo && this.cachedQueryInfo.countStatus=="ERROR") return "The count query experienced an error. Please contact the administrator.";
    if (this.cachedQueryInfo && this.cachedQueryInfo.countStatus=="TIMEOUT") return "The count query timed out.";

    return null;
  }

  getVariantColor() {


    if (this.cachedQueryInfo && this.cachedQueryInfo.resultStatus=="FINISHED" && this.cachedQueryInfo.countStatus!="RUNNING") {

      if (this.cachedQueryInfo.countStatus!="FINISHED") {
        return "btn label-warning";
      }else if (this.queryCount<this.totalCount){
        return "label-danger"
      } else {
        return "btn label-success";
      }
    }

    if (this.cachedQueryInfo && this.cachedQueryInfo.resultStatus=="ERROR") {
      return "label-danger";
    }

    return "label-default";
  }

  getVariantMessage() {
    if (this.cachedQueryInfo && this.cachedQueryInfo.resultStatus=="FINISHED" && this.cachedQueryInfo.countStatus!="RUNNING") {

      if (this.cachedQueryInfo.countStatus!="FINISHED") {
        return "Results are available but we are not sure they're all since the count query did not complete.";
      }else if (this.queryCount<this.totalCount){
        return "The query returned too many results (more than 2000). Please refine your search.";
      }

    }else if (this.cachedQueryInfo && this.cachedQueryInfo.resultStatus=="ERROR") {
      return "The query experienced an error. Please contact the administrator.";
    }

    return null;
  }

  getCoverageColor(){
    if (this.cachedQueryInfo && (this.cachedQueryInfo.coverageStatus=="NOT_PRESENT" || this.cachedQueryInfo.coverageStatus=="ERROR")) return "label-danger";
    if (this.cachedQueryInfo && this.cachedQueryInfo.coverageStatus=="FINISHED") {
      if (this.coverageCount) return "btn label-warning";
      return "label-success"
    }

    return "label-default";
  }

  getCoverageMessage() {
    if (this.cachedQueryInfo && (this.cachedQueryInfo.coverageStatus=="NOT_PRESENT")) return "Coverage data was not loaded.";
    if (this.cachedQueryInfo && (this.cachedQueryInfo.coverageStatus=="ERROR")) return "The coverage query experienced an error. Please contact the administrator.";
    if (this.cachedQueryInfo && this.cachedQueryInfo.coverageStatus=="FINISHED" && this.coverageCount)  return "There are some regions not properly covered.";

    return null;
  }



}
