
import {FilterOperator, FilterType} from "../query-builder/query-filter";


export class FullQuery {

  VCF_REF_ID : number = null;
  SAMPLE_REF_ID : number = null;
  VCF_NAME: string = null;
  SAMPLE_NAME: string = null;
  PANEL: QueryPanel = new QueryPanel();
  RELATED_SAMPLES: RelatedSampleInfo[] = new Array();
  FILTER: QueryFilter = new QueryFilter();

}

export class QueryPanel {
  REF_ID: number  = null;
  MNEMONIC: string = null;
  BED_REF_ID: number = null;
  BED_NAME: string = null;
  VALID: boolean = null;
  GENES : Array<string> = [];
}

export class RelatedSampleInfo {
  SAMPLE: string = null;
  AFFECTED: boolean = false;
  SEX: Sex = Sex.UNK

}

export class QueryFilter {
  REF_ID: number = null;
  MNEMONIC: string = null;
  RULE: FilterRule = new FilterRule();
  VALID: boolean = null;
  FIELDS : any[] = null;

}

export class FilterRule {
  id : number = null;
  fieldPath :string = null;
  type : FilterType;
  operator : FilterOperator;
  value: any;

  condition: GroupCondition = GroupCondition.AND;
  rules: Array<FilterRule> = [];

}

enum GroupCondition {
  AND = "AND",
  OR = "OR"
}

export enum Sex {
  M = "M",
  F = "F",
  UNK = "UNK"
}
