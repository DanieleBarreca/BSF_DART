import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {AgGridAngular} from "ag-grid-angular";
import {CoverageTableConfig} from "./coverage-table-config";

@Component({
  selector: 'app-coverage-ag-grid',
  templateUrl: './coverage-ag-grid.component.html',
  styleUrls: ['./coverage-ag-grid.component.css']
})
export class CoverageAgGridComponent implements OnInit, OnChanges {
  @Input('config') config: CoverageTableConfig;
  @ViewChild('agGrid') agGrid: AgGridAngular;

  selectedRegion= null;

  rowData = []
  columnDef = [
    {
      headerName : 'CHROM',
      field : 'chrom',
      suppressMovable: true,
      resizable: true,
      width:80
    },
    {
      headerName : 'START',
      field : 'start',
      suppressMovable: true,
      resizable: true,
      width:130
    },
    {
      headerName : 'END',
      field : 'end',
      suppressMovable: true,
      resizable: true,
      width:130
    },
    {
      headerName : 'GENES',
      field : 'genes',
      filterParams: {
        suppressAndOrCondition:true,
        filterOptions: ['contains'],
        debounceMs: 500,
      },
      filter: true,
      suppressMovable: true,
      resizable: true,
      width:200
    },
    {
      headerName : 'MAPPING STATUS',
      field : 'mappingStatus',
      filterParams: {
        suppressAndOrCondition:true,
        filterOptions: ['contains'],
        debounceMs: 500,
      },
      filter: true,
      suppressMovable: true,
      resizable: true,
      width:200
    }
    ];

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['config']){
      let previousConfig =  changes['config'].previousValue;
      let newConfig =  changes['config'].currentValue;
      if (previousConfig && newConfig &&
        (previousConfig as CoverageTableConfig).datasource.getUUID()!==(newConfig as CoverageTableConfig).datasource.getUUID()){
        this.selectedRegion=null;
      }

    }
  }


  onRowDoubleClicked(event) {
    this.selectedRegion=event.node;
  }

  onRowSelect(event) {
    if (this.selectedRegion && event.node.selected && (event.data.REF_ID!==this.selectedRegion.data.REF_ID)){
      this.selectedRegion = event.node;
    }
  }

  selectNext() {
    if (this.hasNext()) {
      this.agGrid.api.getDisplayedRowAtIndex(this.selectedRegion.rowIndex + 1).setSelected(true, true);
    }
  }

  hasNext() {
    return (this.selectedRegion && this.selectedRegion.rowIndex<(this.config.datasource.getTotalCount()-1));
  }

  selectPrev() {
    if (this.hasPrev()) {
      this.agGrid.api.getDisplayedRowAtIndex(this.selectedRegion.rowIndex - 1).setSelected(true, true);
    }
  }

  hasPrev() {
    return (this.selectedRegion && this.selectedRegion.rowIndex>0);
  }

  clearSelectedRegion() {
    this.selectedRegion = null;
  }

  getCurrentLocus(){
    if (this.selectedRegion){
      let chrom = this.selectedRegion.data['chrom'];
      let start = this.selectedRegion.data['start'];
      let end =  this.selectedRegion.data['end'];
      return chrom + ":" + (start - 20) + "-" + (end + 20);
    }

    return null;
  }

}
