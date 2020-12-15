import {Component, OnInit, ViewChild} from '@angular/core';
import {ReportService} from "../report.service";
import {Report} from "./Report";
import {first, skipWhile, switchMap, tap} from "rxjs/operators";
import {timer} from "rxjs";
import {RowEvent} from "ag-grid-community";
import {AgGridAngular, ICellRendererAngularComp} from "ag-grid-angular";
import {Router} from "@angular/router";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  @ViewChild("agGrid") reportGrid: AgGridAngular;

  frameworkComponents = {
    conditionRender:ConditionRenderer,
    loaderRenderer: LoaderRenderer
  };

  reports: Report[] = [];
  columnDefs =  [
    {
      headerName : 'VCF FILE',
      field : 'QUERY.VCF_NAME',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },
    {
      headerName : 'SAMPLE',
      field : 'QUERY.SAMPLE_NAME',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },
    {
      headerName : 'CONDITIONS',
      field : 'CONDITIONS',
      cellRenderer: 'conditionRender',
      pinned: "left",
      filter:true,
      resizable: true,
      autoHeight: true
    },
    {
      headerName : 'PANEL',
      field : 'QUERY.PANEL.MNEMONIC',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },    {
      headerName : 'FILTER',
      field : 'QUERY.FILTER.MNEMONIC',
      sortable: true,
      pinned: "left",
      filter:true,
      resizable: true
    },
    {
      headerName : 'DATE',
      filter: "agDateColumnFilter",
      floatingFilter: true,
      valueGetter: function(params) {
        if (params.data) {
          let creationDate = new Date(params.data.CREATION_DATE);
          return new Date(creationDate.getFullYear(),creationDate.getMonth() , creationDate.getDate());
        }
        return null;
      },
      valueFormatter: function(params) {
        if (params.value) {
          return (params.value as Date).toDateString();
        }
        return "";
      },
      sortable: true,
      suppressMovable: true,
      resizable: true
    },
    {
      headerName : 'USER',
      field : 'CREATION_USER',
      suppressMovable: true,
      resizable: true
    },
    {
      headerName : 'VARIANTS',
      field : 'VARIANTS',
      cellRenderer: 'loaderRenderer',
      suppressMovable: true,
      resizable: true
    }
    ,
    {
      headerName : 'NON CALLABLE LOCI',
      field : 'COVERAGE_ENTRIES',
      cellRenderer: 'loaderRenderer',
      suppressMovable: true,
      resizable: true
    },

  ];

  constructor(private reportService: ReportService,private router: Router) { }

  ngOnInit() {
    this.reportService.getAllReports().subscribe(
      (data) =>{
        this.reports = data;


        this.reports.forEach(
          (report, index) => {
            if (report.VARIANTS === null || (report.HAS_COVERAGE && report.COVERAGE_ENTRIES === null)) {
              timer(500, 1000).pipe(
                switchMap(counts => this.reportService.getReport(report.REF_ID)),
                tap(
                  report => {
                    if (this.reports[index].VARIANTS ===null && report.VARIANTS !== null) {
                      this.reports[index].VARIANTS = report.VARIANTS;
                      this.reportGrid.api.refreshRows([this.reportGrid.api.getRowNode(String(index))])
                    }
                  }
                ),
                skipWhile(report => (report.VARIANTS === null || (report.HAS_COVERAGE && report.COVERAGE_ENTRIES === null))),
                first()
              ).subscribe(
                report => {
                  this.reports[index].VARIANTS = report.VARIANTS;
                  this.reports[index].COVERAGE_ENTRIES = report.COVERAGE_ENTRIES;
                  this.reportGrid.api.refreshRows([this.reportGrid.api.getRowNode(String(index))])
                }
              )
            }
          }
        )
      }
    )
  }

  selectRow(selectedRow: RowEvent) : void {
    this.router.navigate(["/report",{reportId:selectedRow.data.REF_ID}])
  }

}

@Component({
  template: `
    <div class="row" *ngFor="let condition of conditions"  style="line-height: 19px">
      <div class="col-xs-12">
        <span class="label label-default">
          {{condition.LABEL}}
        </span>
      </div>
    </div>
    `
})
export class ConditionRenderer implements ICellRendererAngularComp {
  public conditions: any[] = [];

  constructor(){}

  agInit(params: any): void {
    this.conditions = params.value;
  }

  refresh(): boolean {
    return false;
  }
}

@Component({
  template: `
    <img src="assets/images/loading.gif" style="height: 20px;" *ngIf="shouldShowLoader()">
    <span *ngIf="!shouldShowLoader()">{{value}}</span>
    `
})
export class LoaderRenderer implements ICellRendererAngularComp {

  value: number;
  showLoader:boolean = true;

  constructor(){}

  agInit(params: any): void {
    this.value = params.value;
    if (params.colDef.field=="COVERAGE_ENTRIES" && !params.data.HAS_COVERAGE){
      this.showLoader = false;
    }
  }

  refresh(): boolean {
    return false;
  }

  shouldShowLoader(){
    return (this.value==null && this.showLoader);
  }

}
