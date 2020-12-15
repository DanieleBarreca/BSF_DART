import {Component, OnInit, ViewChild} from '@angular/core';
import {VariantQueryContainerComponent} from "../variant-query-container/variant-query-container.component";
import {VariantAgGridComponent} from "../variant-ag-grid/variant-ag-grid.component";
import {CoverageQueryDatasource, CoverageTableConfig} from "../coverage-ag-grid/coverage-table-config";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {AuthenticationService} from "../authentication.service";
import {ReportService} from "../report.service";
import {Report} from "../reports/Report";

@Component({
  selector: 'app-report-result',
  templateUrl: './report-result.component.html',
  styleUrls: ['./report-result.component.css']
})
export class ReportResultComponent implements OnInit {

  @ViewChild('variantsGrid') variantsGrid: VariantAgGridComponent;

  report: Report = new Report();

  coverageTableConfig: CoverageTableConfig = null;

  variants : Array<any> = null;
  coverage : Array<any> = [];

  showCoverage=false;

  isQueryPanelVisible= false;

  constructor(private activatedRoute: ActivatedRoute,
              private authService: AuthenticationService,
              private router: Router,
              private reportService: ReportService) { }

  ngOnInit() {

    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
      if (params.has('showCoverage')){
        this.showCoverage=(params.get('showCoverage')=="true");
      }else{
        this.showCoverage = false;
      }

      if (params.has('reportId')){
        this.coverageTableConfig = null;

        this.reportService.getReport(+params.get('reportId')).subscribe(
          (report: Report) => {
            this.report=report;
            this.coverageTableConfig = new CoverageTableConfig(
              this.report.VCF_FILE,
              this.report.QUERY.SAMPLE_NAME,
              new CoverageQueryDatasource(this.reportService, this.report.REF_ID.toString())
            );
            this.getVariantData();
          },
          (err) => {
            console.warn(err);
          }
        )
      }
    });
  }

  getVariantData(){
    this.reportService.getReportVariants(this.report.REF_ID).subscribe(
      (data) => this.variants = data,
      (err) => {
        console.warn(err);
      }

    );
  }

  exportJson(){

    let exportObj = {}
    exportObj['REPORT_INFO']=this.report;
    exportObj['VARIANTS_INFO']=this.variants;


    let allCoverage : Array<any> = [];
    this.reportService.getCoverageResults(this.report.REF_ID.toString(),0,2147483647, null, null).subscribe(
      (data) =>{
        exportObj['COVERAGE_INFO']=data.entries;
        let fileContent = JSON.stringify(exportObj,null, 2);

        let blob = new Blob([fileContent], { type: 'text/json;charset=utf-8;' });
        let dwldLink = document.createElement("a");
        let url = URL.createObjectURL(blob);
        dwldLink.setAttribute("href", url);
        dwldLink.setAttribute("download", this.report.QUERY.SAMPLE_NAME + ".report.json");
        dwldLink.style.visibility = "hidden";
        document.body.appendChild(dwldLink);
        dwldLink.click();
        document.body.removeChild(dwldLink);

      },
      (err => console.log(err))
    )

  }

  getCoverageColor(){
    if (!this.report.HAS_COVERAGE || this.report.COVERAGE_ENTRIES == null) return "label-danger";

    if (this.report.COVERAGE_ENTRIES) return "btn label-warning";

    return "label-success";

  }

  public viewResults() {
    this.router.navigate(["/report",{reportId:this.report.REF_ID}])
  }

  public viewCoverage() {
    this.router.navigate(["/report",{reportId:this.report.REF_ID,showCoverage:true}])
  }


}
