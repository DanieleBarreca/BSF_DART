import {IDatasource, IGetRowsParams} from "ag-grid-community";
import {VariantService} from "../variant.service";
import {CoverageDataService} from "../CoverageDataService";

export class CoverageTableConfig {
  vcf: any;
  sampleName: string;
  datasource: CoverageDataSource;

  constructor(vcf, sampleName: string, datasource: CoverageDataSource) {
    this.vcf=vcf;
    this.sampleName = sampleName;
    this.datasource = datasource;
  }
}

export interface CoverageDataSource extends IDatasource {
  getTotalCount(): number;
  getUUID(): string;
}

export class CoverageQueryDatasource implements CoverageDataSource{

  totalCount=0;

  constructor(private variantService: CoverageDataService, private currentQueryId: string) {
  }

  getTotalCount(): number {
    return this.totalCount;
  }

  getUUID(): string {
    return this.currentQueryId;
  }

  getRows(params: IGetRowsParams): void {

    let first = params.startRow;
    let pageSize = params.endRow - params.startRow;

    let geneFilter = null
    if (params.filterModel && params.filterModel.genes){
      geneFilter=params.filterModel.genes.filter;
    }

    let statusFilter = null
    if (params.filterModel && params.filterModel.mappingStatus){
      statusFilter=params.filterModel.mappingStatus.filter;
    }

    this.variantService.getCoverageResults(this.currentQueryId, first, pageSize, geneFilter, statusFilter).subscribe(
      (response) => {
        if (response['status'] == 'FINISHED') {

          this.totalCount=response['filteredCount']

          let rowCount = null;
          if (this.totalCount<=(first+pageSize)){
            rowCount=this.totalCount;
          }

          params.successCallback(response['entries'], rowCount);
        }else {
          params.failCallback();
        }
      },
      (err) =>{
        console.log(err);
        params.failCallback();
      }
    )

  }

}

export class CoverageFixedDatasource implements CoverageDataSource{

  totalCount=0;

  constructor(private uuid: string,private  data : Array<any> ) {
    this.totalCount = data.length;
  }

  getTotalCount(): number {
    return this.totalCount;
  }

  getUUID(): string {
    return this.uuid;
  }

  getRows(params: IGetRowsParams): void {

    let results=Object.assign([], this.data);

    if (params.filterModel && params.filterModel.genes){
      var regex = new RegExp(".*" + params.filterModel.genes.filter + ".*", "i");
      results=results.filter(coverageEntry =>
        coverageEntry['genes'].some(gene => regex.test(gene))
      );
    }

    if (params.filterModel && params.filterModel.mappingStatus){
      var regex = new RegExp(".*" + params.filterModel.mappingStatus.filter + ".*", "i");
      results=results.filter(coverageEntry =>
        regex.test(coverageEntry['mappingStatus'])
      );
    }

    this.totalCount=results.length;

    results=results.slice(params.startRow, params.endRow);

    let rowCount = null;
    if (this.totalCount<=(params.endRow)){
      rowCount=this.totalCount;
    }

    params.successCallback(results, rowCount);

  }

}
