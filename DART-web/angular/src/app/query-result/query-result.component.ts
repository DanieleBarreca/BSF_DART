import { Component, OnInit, ViewChild } from '@angular/core';
import {ActivatedRoute, NavigationEnd, ParamMap, Router} from '@angular/router';
import { QueryService } from '../query.service';
import { VariantQueryContainerComponent } from '../variant-query-container/variant-query-container.component';
import {VcfService} from "../vcf.service";
import {VariantService} from "../variant.service";
import {CoverageQueryDatasource, CoverageTableConfig} from "../coverage-ag-grid/coverage-table-config";
import {VariantAgGridComponent} from "../variant-ag-grid/variant-ag-grid.component";
import {AnnotationService} from "../annotation.service";
import {ReportService} from "../report.service";
import {AuthenticationService} from "../authentication.service";

@Component({
  selector: 'app-query-result',
  templateUrl: './query-result.component.html',
  styleUrls: ['./query-result.component.css']
})
export class QueryResultComponent implements OnInit {
  @ViewChild('queryContainer') queryContainer: VariantQueryContainerComponent;
  @ViewChild('variantsGrid') variantsGrid: VariantAgGridComponent;

  vcf = null;
  coverageTableConfig: CoverageTableConfig = null;

  cachedQueryInfo: any = {theQuery: null}
  variants : Array<any> = null;
  coverage : Array<any> = [];

  showCoverage=false;

  constructor(private activatedRoute: ActivatedRoute,
              private authService: AuthenticationService,
              private router: Router,
              private queryService: QueryService,
              private vcfService: VcfService,
              private variantService: VariantService,
              private annotationService:AnnotationService,
              private reportService: ReportService) { }

  ngOnInit() {

    this.queryContainer.onResultsAvailable.subscribe(
      () => {
        this.submitResultQuery();
      }
    );

    this.queryContainer.onCoverageAvailable.subscribe(
      () => {
        if (!this.coverageTableConfig || (this.coverageTableConfig.datasource.getUUID()!==this.cachedQueryInfo.uuid)) {
          this.coverageTableConfig = new CoverageTableConfig(
            this.vcf,
            this.cachedQueryInfo.theQuery.SAMPLE_NAME,
            new CoverageQueryDatasource(this.variantService, this.cachedQueryInfo.uuid)
          );
        }
      }
    );


    let paramsChanged = false;

    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
      paramsChanged = true;

      this.vcf=null;
      this.variants=[];
      this.coverageTableConfig = null;
      this.cachedQueryInfo= {theQuery: null}
      this.coverage  = [];

      if (params.has('showCoverage')){
        this.showCoverage=(params.get('showCoverage')=="true");
      }else{
        this.showCoverage = false;
      }

      if (params.has('queryId')){

        this.queryService.getQuery(params.get('queryId')).subscribe(
          (cachedQueryInfo: any) => {
              this.cachedQueryInfo=cachedQueryInfo;
              paramsChanged=false;
              this.retrieveVcf();
          },
          (err) => {
            console.warn(err);
          }
        )
      }
    });

    this.router.events.subscribe((next) => {
      if (next instanceof NavigationEnd){
        if (!paramsChanged) {
          this.retrieveVcf();
        }
      }
    })


  }

  private retrieveVcf(){
    this.vcfService.getVcfById(this.cachedQueryInfo.theQuery.VCF_REF_ID).subscribe(
      (data) => {
        if (data) {
          this.vcf = data;
          this.queryContainer.setQuery(this.cachedQueryInfo);
        }else{
          this.vcf = null;
        }
      },
      (err) => {
        console.error(err);
        this.vcf=null;
      }
    );
  }

  private submitResultQuery(queryId = this.cachedQueryInfo.uuid) {
    if (queryId) {
      this.variantService.getResults(queryId).subscribe(
        (response) => {
          if (response['variantResult']['status'] == 'FINISHED') {
            if (response['variantResult']['queryCount'] != 0) {
              this.variants = response['variantResult']['variants'];
            } else {
              this.variants = [];
            }
          } else if (response['variantResult']['status'] == 'ERROR') {
            this.variants = [];
          }
        },
        (err) => {
          console.error(err);
          this.cachedQueryInfo  = null;
          this.variants = [];
        }
      );
    }
  }

  getConditions(){
    if (this.cachedQueryInfo){
      return this.cachedQueryInfo.conditions;
    }else{
      return []
    }
  }

  saveReport(){
    if (this.cachedQueryInfo.theQuery.FILTER.REF_ID == null) {
      window.alert("Please select a preset filter!");
    }else if (this.cachedQueryInfo.theQuery.PANEL.REF_ID == null){
      window.alert("Please select a preset panel!");
    } else if (window.confirm("Are you sure to create a new report from this data?")){
      this.reportService.submitQuery(this.cachedQueryInfo.uuid).subscribe(
        data => this.router.navigate(["/reports"]),
        error1 => console.error(error1)
      )
    }
  }

  addCondition(event: number) {
    this.annotationService.addConditionToSample(this.cachedQueryInfo.theQuery.SAMPLE_REF_ID, event).subscribe(
      (data)=>{
        this.cachedQueryInfo.conditions = data;
      },
      (error1 => console.error(error1))
    );
  }

  removeCondition(event: number) {
    this.annotationService.removeConditionFromSample(this.cachedQueryInfo.theQuery.SAMPLE_REF_ID, event).subscribe(
      (data)=>{
        this.cachedQueryInfo.conditions = data;
      },
      (error1 => console.error(error1))
    )
  }

  canSaveReport(){
    return (this.authService.getPermissions().canSaveReport);
  }

  canAnnotateVariants() {
    return (this.authService.getPermissions().canAnnotatePathogenicity);
  }

}
