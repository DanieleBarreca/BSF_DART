import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';


import {BsModalService} from "ngx-bootstrap";
import {VariantTableConfigComponent} from "../variant-table-config/variant-table-config.component";
import {PresetService} from "../preset.service";
import {AgGridAngular} from "ag-grid-angular";
import {VariantAgGridPathogenicityComponent} from "../variant-ag-grid-pathogenicity/variant-ag-grid-pathogenicity.component";
import {RowDataChangedEvent} from "ag-grid-community";
import {VariantAgGridTrioComponent} from "../variant-ag-grid-trio/variant-ag-grid-trio.component";
import {VariantAgGridValidationComponent} from "../variant-ag-grid-validation/variant-ag-grid-validation.component";
import {FullQuery} from "../variant-filter-query/FullQuery";
import {VariantAgGridCheckboxFilterComponent} from "./variant-ag-grid-checkbox-filter.component";

@Component({
  selector: 'app-variant-ag-grid',
  templateUrl: './variant-ag-grid.component.html',
  styleUrls: ['./variant-ag-grid.component.css']
})
export class VariantAgGridComponent implements OnInit, OnChanges {
  @Output("dataChanged") dataChanged: EventEmitter<null> = new EventEmitter();

  @ViewChild('agGrid') agGrid: AgGridAngular;

  @Input() vcf: any;
  @Input() variants: null;
  @Input() cachedQuery: FullQuery = null;
  @Input() conditions : any[] = [];
  @Input() active = true;

  //private fieldList: Array<any> = [];
  columnDefs: Array<any> = [];

  frameworkComponents = {
    pathogenicityRenderer:VariantAgGridPathogenicityComponent,
    validationRenderer: VariantAgGridValidationComponent,
    trioComponent:VariantAgGridTrioComponent,
    checkboxFilter: VariantAgGridCheckboxFilterComponent
  };

  overlayTemplate = '<span class="ag-overlay-loading-center">Please wait while your rows are loading</span>';

  selectedVariantData= null;

  constructor(
    private modalService: BsModalService,
    private presetService:PresetService
  ) {}

  ngOnInit() {
    this.agGrid.onGridReady = () => {
      this.agGrid.api.addEventListener(
        'rowDataChanged',
        (event: RowDataChangedEvent) => {
          let inheritanceColumn = event.columnApi.getColumn(-50);
          if (inheritanceColumn) {
            event.api.getFilterInstance(inheritanceColumn).setModel(null);
          }
        });
    }
  }

  ngOnChanges(changes: SimpleChanges){
    this.selectedVariantData = null;

    if (changes['vcf'] && this.vcf){
      this.columnDefs= this.vcf['FIELDS'].map(field => {
        let columnDef =  {
          colId: field['ID'],
          headerName : field['DISPLAY_NAME'],
          field : field['FIELD_PATH'],
          sortable: true,
          hide: field.FIELD_PATH=="OTHER_SAMPLES",
          lockPinned: true,
          suppressMovable: false,
          resizable: true,
          headerTooltip: field['DESCRIPTION']
        };

        if ((field['TYPE']=='INTEGER' ||field['TYPE']=='DOUBLE') && this.active) {
          columnDef['filter']="agNumberColumnFilter";
        }else if ((field['TYPE'] == 'ARRAY_INT' || field['TYPE'] == 'ARRAY_DOUBLE') && this.active) {
          columnDef['filter'] = "agNumberColumnFilter";
          columnDef['filterParams'] = {
            filterOptions: [
              {
                displayKey: 'lessThanOrEqualCustom',
                displayName: 'Less Than Or Equal',
                test: function (filterValue, cellValue) {
                  if (cellValue == null || (cellValue as Array<any>).length == 0) {
                    return true;
                  }
                  let foundValue = (cellValue as Array<any>).find(value => value <= filterValue);
                  return foundValue !== undefined;
                }
              },
              {
                displayKey: 'greaterThanOrEqualCustom',
                displayName: 'Greater Than Or Equal',
                test: function (filterValue, cellValue) {
                  if (cellValue==null) return false;
                  let foundValue = (cellValue as Array<any>).find(value => value >= filterValue);
                  return foundValue !== undefined;
                }
              },
              {
                displayKey: 'equalCustom',
                displayName: 'Equals',
                test: function (filterValue, cellValue) {
                  if (cellValue==null) return false;
                  let foundValue = (cellValue as Array<any>).find(value => value == filterValue);
                  return foundValue !== undefined;
                }
              },
              {
                displayKey: 'notEqualCustom',
                displayName: 'Not Equals',
                test: function (filterValue, cellValue) {
                  if (cellValue==null) return true;
                  let foundValue = (cellValue as Array<any>).find(value => value == filterValue);
                  return foundValue == undefined;
                }
              }
            ]
          }
        }else{
          columnDef['filter'] =this.active
        }

        let possibleValues = field['POSSIBLE_VALUES'];
        if (possibleValues && possibleValues.length >0 && (field['TYPE']=='ARRAY_STR' || field['TYPE']=='STRING')) {
          let singleValueComparator = function(value1, value2) {

            if (!value1) return  -1;
            if (!value2) return  1;

            let idxA = possibleValues.indexOf(value1);
            let idxB = possibleValues.indexOf(value2);

            return idxA - idxB;
          };

          if (field['TYPE']=='ARRAY_STR') {
            columnDef['comparator'] = function (valueA: Array<any>, valueB: Array<any>, nodeA, nodeB, isInverted: boolean) {
              let arrayComparator = isInverted? (function (a, b) { return singleValueComparator(b,a)})  : singleValueComparator;
              valueA = valueA.sort(arrayComparator);
              valueB = valueB.sort(arrayComparator);

              return singleValueComparator(valueA[0], valueB[0]);
            }

          }else {
            columnDef['comparator'] = singleValueComparator;
          }
        }

        return columnDef;
      });

      this.columnDefs.unshift({
          colId: -1,
          headerName : "ANNOTATIONS",
          field : "ANNOTATIONS.DB_PATHOGENICITY",
          sortable: true,
          lockPinned: true,
          resizable: true,
          headerTooltip: 'ANNOTATIONS',
          cellRenderer: "pathogenicityRenderer",
          cellRendererParams: {
            sample: this.vcf['SAMPLES'].find(sample => sample['SAMPLE_NAME']==this.cachedQuery.SAMPLE_NAME)['REF_ID'],
            parent: this
          },
          autoHeight: true,
          filter: this.active
        }
      );

      for (var index=0; index<this.cachedQuery.RELATED_SAMPLES.length; index++){
        this.columnDefs.unshift({
            colId: -199+index,
            headerName: "RELATED: "+this.cachedQuery.RELATED_SAMPLES[index].SAMPLE,
            field: "ANNOTATIONS.INHERITANCE."+this.cachedQuery.RELATED_SAMPLES[index].SAMPLE,
            sortable: true,
            lockPinned: true,
            resizable: true,
            headerTooltip: 'RELATED SAMPLE ANNOTATIONS',
            cellRenderer: "trioComponent",
            filter: (this.active ? "checkboxFilter" :false),
            autoHeight: true,
            comparator: (value1, value2) => {
              if (value1 && value2) {
                return value1['INHERITANCE_RANK'] - value2['INHERITANCE_RANK'];
              } else if (value1) {
                return -1;
              } else if (value2) {
                return 1;
              } else {
                return 0;
              }
            },
          }
        );
      }

      this.columnDefs.unshift({
          colId: -200,
          headerName : "ZYGOSITY",
          field : "SAMPLE.GENOTYPE.ZYGOSITY",
          sortable: true,
          lockPinned: true,
          resizable: true,
          headerTooltip: 'ZYGOSITY',
          autoHeight: true,
          filter: (this.active ? "checkboxFilter" :false)
        }
      );


      this.columnDefs.unshift({
          colId: -300,
          headerName : "GENOMIC CHANGE",
          field : "TRANSCRIPT.INTERNAL:HGVSg",
          sortable: true,
          lockPinned: true,
          pinned: 'left',
          suppressMovable: true,
          resizable: true,
          filter: this.active,
          headerTooltip: 'HGVSg'
        }
      );

      this.columnDefs.unshift({
          colId: -400,
          headerName : "GENE",
          field : "TRANSCRIPT.INTERNAL:GENE",
          sortable: true,
          lockPinned: true,
          pinned: 'left',
          suppressMovable: true,
          resizable: true,
          filter: this.active,
          headerTooltip: 'GENE SYMBOL'
        }
      );

      this.columnDefs.unshift({
          colId: -500,
          headerName : "VALIDATION",
          field : "ANNOTATIONS.DB_VALIDATION",
          sortable: true,
          lockPinned: true,
          pinned: 'left',
          width: 114,
          resizable: false,
          suppressMovable: true,
          filter: this.active,
          headerTooltip: 'VALIDATION STATUS',
          cellRenderer: "validationRenderer",
          cellRendererParams: {
            sample: this.vcf['SAMPLES'].find(sample => sample['SAMPLE_NAME']==this.cachedQuery.SAMPLE_NAME)['REF_ID'],
            parent: this
          },
          autoHeight: true
        }
      );

      if (this.active) {
        this.applyColumnsPreset();
      }else {
        if (this.agGrid.columnApi) {
          let columnsState = this.agGrid.columnApi.getColumnState();
          columnsState = columnsState.sort(function (a, b) {
            if (+a.colId < 0 && +b.colId < 0) {
              return +a.colId - +b.colId;
            }
            if (+a.colId < 0) return -1;
            if (+b.colId < 0) return 1;


            return (this.vcf['FIELDS'].findIndex((element) => (element['ID'] == +a.colId)) > this.vcf['FIELDS'].findIndex((element) => (element['ID'] == +b.colId))) ? 1 : -1;


          }.bind(this));

          this.agGrid.columnApi.setColumnState(columnsState);
        }
      }
    }

  }

  private applyColumnsPreset(){
    this.presetService.getFieldsPreset(this.vcf.VCF_TYPE).subscribe(
      (data) => {
        if (data && data.length>0){
          let columnsState = this.agGrid.columnApi.getColumnState() ;

          columnsState.filter(x=> +x.colId >=0 ).forEach(x => {
            x.hide = (!data.includes(+x.colId));
          });

          columnsState= columnsState.sort(function (a, b) {
            if (+a.colId<0 && +b.colId<0 ){
              return +a.colId- +b.colId;
            }
            if (+a.colId<0) return -1;
            if (+b.colId<0) return 1;

            let idxA = data.indexOf(+a.colId);
            let idxB = data.indexOf(+b.colId);
            if (idxA!=-1 || idxB!=-1) {
              if (idxA==-1) return 1;
              if (idxB==-1) return -1;
              return (idxA > idxB) ? 1 : -1;
            }else{
              return (this.vcf['FIELDS'].findIndex((element)=>(element['ID']==+a.colId)) > this.vcf['FIELDS'].findIndex((element)=>(element['ID']==+b.colId))) ? 1 : -1;
            }

          }.bind(this));


          this.agGrid.columnApi.setColumnState(columnsState);
        }

      },
      (err) => console.error(err)
    )
  }

  public openTableConfiguration() {
    const initialState = {
      vcf: this.vcf,
      fieldList: this.agGrid.columnApi.getColumnState().filter(x=> +x.colId >=0 ).map((columnState) => {
        let columnDef = this.agGrid.columnApi.getColumn(columnState.colId);
        return {
          ID: +columnState.colId,
          VISIBLE: !(columnState.hide),
          DISPLAY_NAME: columnDef.getUserProvidedColDef().headerName,
          DESCRIPTION: columnDef.getUserProvidedColDef().headerTooltip,
        }
      })
    };

    let tabelConfigModal = this.modalService.show(VariantTableConfigComponent, {initialState, class: 'modal-lg',ignoreBackdropClick: true,keyboard: false});
    (tabelConfigModal.content as VariantTableConfigComponent).onUpdateList.subscribe(
      (columnsList) => this.applyColumnsList(columnsList)
    );
  }


  private applyColumnsList(columnsList: Array<any>){

    let columnsState = this.agGrid.columnApi.getColumnState() ;

    let columnsOrder = columnsList.map((column)=>column.ID);

    columnsState.filter(x=> +x.colId >=0 ).forEach(x => {
      x.hide = !(columnsList[columnsOrder.indexOf(+x.colId)].VISIBLE);
    });


    columnsState= columnsState.sort(function (a, b) {
      if (+a.colId<0 && +b.colId<0 ){
        return +a.colId- +b.colId;
      }
      if (+a.colId<0) return -1;
      if (+b.colId<0) return 1;

      let idxA = columnsOrder.indexOf(+a.colId);
      let idxB = columnsOrder.indexOf(+b.colId);

      return idxA - idxB;
    });

    this.agGrid.columnApi.setColumnState(columnsState);
  }

  onRowDoubleClicked(event) {
    this.selectedVariantData=event.node;
  }

  onRowSelect(event) {
    if (this.selectedVariantData && event.node.selected && (event.data.REF_ID!==this.selectedVariantData.data.REF_ID)){
      this.selectedVariantData = event.node;
    }
  }

  selectNext() {
    if (this.hasNext()) {
      this.agGrid.api.getDisplayedRowAtIndex(this.selectedVariantData.rowIndex + 1).setSelected(true, true);
    }
  }

  hasNext() {
    return (this.selectedVariantData && !this.selectedVariantData.lastChild);
  }

  selectPrev() {
    if (this.hasPrev()) {
      this.agGrid.api.getDisplayedRowAtIndex(this.selectedVariantData.rowIndex - 1).setSelected(true, true);
    }
  }

  hasPrev() {
    return (this.selectedVariantData && !this.selectedVariantData.firstChild);
  }

  clearSelectedVariant() {
    this.selectedVariantData = null;
  }

  downloadCSV() {
   let header = JSON.stringify(this.cachedQuery,(name, val) => {
      if (name.includes("REF_ID")) return undefined;
      if (name === "VALID") return undefined;
      if (name === "id") return undefined;
      if (name === "type") return undefined;
      if (name === "FIELDS") return undefined;

      console.log(name, val);
      return val;
    },2).replace(/^/gm, '##');

    let exportParams = {
      fileName: this.cachedQuery? (this.cachedQuery.SAMPLE_NAME+".csv") : "export.csv",
      processCellCallback: function (params) {
        if (+params.column.colId==-100){
          let formattedValue = params.value.map( (annotation) => {
            return annotation['ANNOTATION']['LABEL'] + "-" +annotation['INHERITANCE']['LABEL'] + " [" +annotation['CONDITION']['LABEL'] + " (" +annotation['CONDITION']['CODE'] +")]";
          });
          formattedValue = formattedValue.join("\n");
          return  formattedValue;
        } else if (+params.column.colId>=-199 && +params.column.colId<-1) {

          let formattedValue = params.value['RELATED_SAMPLE_ZYGOSITY'] + " " +
            "["+params.value['RELATED_SAMPLE_SEX'] +
            ( params.value['RELATED_SAMPLE_AFFECTED']? " - AFFECTED]": "]")

          return  formattedValue;
        } else if (+params.column.colId==-400) {
          let formattedValue = params.value ? params.value['VALIDATION_STATUS'] : null;

          return formattedValue;

        } else {
          return params.value
        }
      },
      customHeader: header
    };

    this.agGrid.api.exportDataAsCsv(exportParams);
  }


  getVcfType(): string{
    if (this.vcf){
      return this.vcf['VCF_TYPE'];
    }

    return  null;
  }

  getVcfGenome(): string {
    if (this.vcf){
      return this.vcf['REF_GENOME'];
    }

    return  null;
  }

  emitDataChanged(){
    this.dataChanged.emit();
  }


}
