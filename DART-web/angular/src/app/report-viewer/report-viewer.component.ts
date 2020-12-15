import {Component, OnInit, ViewChild} from '@angular/core';
import {Report} from "../reports/Report";
import {
  CoverageFixedDatasource,
  CoverageQueryDatasource,
  CoverageTableConfig
} from "../coverage-ag-grid/coverage-table-config";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {VariantQueryContainerComponent} from "../variant-query-container/variant-query-container.component";
import {VariantAgGridComponent} from "../variant-ag-grid/variant-ag-grid.component";

@Component({
  selector: 'app-report-viewer',
  templateUrl: './report-viewer.component.html',
  styleUrls: ['./report-viewer.component.css']
})
export class ReportViewerComponent implements OnInit {

  @ViewChild('variantsGrid') variantsGrid: VariantAgGridComponent;


  selectedFile: File
  coverage : Array<any> = [];

  coverageTableConfig: CoverageTableConfig = null;

  showCoverage=false;
  isQueryPanelVisible= false;

  report: Report = null;
  variants : Array<any> = null;


  constructor(private activatedRoute: ActivatedRoute,private router: Router) { }

  ngOnInit() {

    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
      if (params.has('showCoverage')) {
        this.showCoverage = (params.get('showCoverage') == "true");
      } else {
        this.showCoverage = false;
      }
    })
  }



  onFileChanged(event) {


    const fileReader = new FileReader();
    this.report=null;
    this.variants=null;
    this.coverage=null;
    this.coverageTableConfig=null;
    this.selectedFile = null;

    fileReader.readAsText(event.target.files[0], "UTF-8");

    fileReader.onload = () => {
      let importObj = JSON.parse(fileReader.result);
      this.selectedFile=event.target.files[0];
      console.log(this.selectedFile);
      this.report=importObj['REPORT_INFO']
      this.variants=importObj['VARIANTS_INFO']
      this.coverage=importObj['COVERAGE_INFO']
      this.coverageTableConfig= new CoverageTableConfig(
        this.report.VCF_FILE,
        this.report.QUERY.SAMPLE_NAME,
        new CoverageFixedDatasource(this.report.REF_ID.toString(), this.coverage)
      );

    };
    fileReader.onerror = (error) => {
      console.log(error);
    }
  }

  getCoverageColor(){
    if (!this.report.HAS_COVERAGE || this.report.COVERAGE_ENTRIES == null) return "label-danger";

    if (this.report.COVERAGE_ENTRIES) return "btn label-warning";

    return "label-success";

  }

  public viewResults() {
    this.showCoverage=false;
  }

  public viewCoverage() {
    this.showCoverage=true;
  }

}

