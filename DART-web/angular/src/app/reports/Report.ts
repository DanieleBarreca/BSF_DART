import {FullQuery} from "../variant-filter-query/FullQuery";

export class Report {
  REF_ID: number;
  CREATION_USER: string;
  CREATION_DATE: string;
  VCF_FILE: any;
  QUERY: FullQuery = new FullQuery();
  HAS_COVERAGE: boolean;
  COVERAGE_ENTRIES: number;
  VARIANTS: number;
  CONDITIONS: any;

}
