import {Component} from '@angular/core';
import {IDoesFilterPassParams, IFilterParams} from "ag-grid-community/dist/lib/interfaces/iFilter";
import {IFilterAngularComp} from "ag-grid-angular";
import {RowNode} from "ag-grid-community";

@Component({
  selector: 'app-variant-ag-grid-checkbox-filter',
  templateUrl: './variant-ag-grid-checkbox-filter.component.html',
  styleUrls: ['./variant-ag-grid-checkbox-filter.component.css']
})
export class VariantAgGridCheckboxFilterComponent implements IFilterAngularComp {

  private field: string;
  private params;

  filterModel = {};

  constructor(){
  }

  agInit(params: IFilterParams){

    this.field = params.colDef.field;

    if (+params.colDef.colId>=-199 && +params.colDef.colId<-1){
      this.field += '.RELATED_SAMPLE_ZYGOSITY'
    }

    params.rowModel.forEachNode((node: RowNode, index: number) => {

          let value = this.field.split('.').reduce((a, b) => a[b], node.data).toString();
          this.filterModel[value] = true;

      });

    this.params= params;

  };

  // Return true if the filter is active. If active than 1) the grid will show the filter icon in the column
  // header and 2) the filter will be included in the filtering of the data.
  isFilterActive(): boolean {
    for ( let [value, enabled ] of Object.entries(this.filterModel)){
      if (!enabled){
        return true;
      }
    }

    return false;
  }
  // The grid will ask each active filter, in turn, whether each row in the grid passes. If any
  // filter fails, then the row will be excluded from the final set. A params object is supplied
  // with attributes node (the rowNode the grid creates that wraps the data) and data (the data
  // object that you provided to the grid for that row).
  doesFilterPass(params: IDoesFilterPassParams): boolean {
    let value = String(this.field.split('.').reduce((a, b) => a[b], params.node.data));
    return this.filterModel[value];
  }

  // Gets the filter state. If filter is not active, then should return null/undefined.
  // The grid calls getModel() on all active filters when gridApi.getFilterModel() is called.
  getModel(): any {
    if (!this.isFilterActive()){
      return null;
    }

    return this.filterModel ;
  }

  // Restores the filter state. Called by the grid after gridApi.setFilterModel(model) is called.
  // The grid will pass undefined/null to clear the filter.
  setModel(model: any): void {
    if (model  == null){
      this.resetFilterModel();
    }else {
      this.filterModel = model;
    }

    this.params.filterChangedCallback();
  }

  resetFilterModel(): void  {
    for (let value of Object.keys(this.filterModel) ){
      this.filterModel[value]=true;
    }

  }

  reset(){
    this.resetFilterModel();
    this.params.filterChangedCallback();
  }


}
