export enum FilterType {
    string = "string",
    integer = "integer",
    double = "double",
    date = "date",
    time = "time",
    datetime = "datetime",
    boolean = "boolean"
}

export enum FilterInput {
    text = "text",
    number = "number",
    textarea = "textarea",
    radio = "radio",
    checkbox = "checkbox",
    select = "select"
}

export enum FilterOperator {
    equal = "equal",
    not_equal = "not_equal",
    in = "in",
    not_in = "not_in",
    less = "less",
    less_or_equal = "less_or_equal",
    greater = "greater",
    greater_or_equal = "greater_or_equal",
    between = "between",
    not_between = "not_between", 
    begins_with = "begins_with",
    not_begins_with = "not_begins_with",
    contains = "contains",
    not_contains = "not_contains",
    ends_with = "ends_with",
    not_ends_with = "not_ends_with",
    is_empty = "is_empty",
    is_not_empty = "is_not_empty",
    is_null = "is_null",
    is_not_null = "is_not_null",
    array_size = "array_size"
}


export class QueryFilter{
    id: string;
    field: string;
    label: string;
    type: string;
    input: string;
    operators: string[] = [];
    values = [];
    default_value: any;

    plugin: string;
    plugin_config: object = {};

    constructor(vcfField: Object) {
        this.id=vcfField['ID'];
        this.field=vcfField['FIELD_PATH'];
        this.label=vcfField['DISPLAY_NAME']+" ["+vcfField['DESCRIPTION']+"]";

        switch(vcfField['TYPE']){
            case "STRING":
            case "ARRAY_STR": {
                this.type = FilterType.string;
                if (vcfField['FIELD_PATH']=='OTHER_SAMPLES' || (vcfField['POSSIBLE_VALUES'] && vcfField['POSSIBLE_VALUES'].length>0)){
                    this.plugin = 'selectize';
                    this.operators = [FilterOperator.in, FilterOperator.not_in];
                    let valuesOption = [];
                    for (let value in vcfField['POSSIBLE_VALUES'].sort() ){
                        valuesOption.push({'value':vcfField['POSSIBLE_VALUES'][value]});
                    }
                    this.plugin_config['options'] = valuesOption;
                    this.plugin_config['valueField'] = 'value';
                    this.plugin_config['labelField'] = 'value';
                    this.plugin_config['searchField'] = 'value'; 
                    this.plugin_config['dropdownParent'] = 'body';

                }else if (vcfField['DISPLAY_NAME']=='CSQ:SYMBOL'){
                    this.plugin = 'selectize';
                    this.plugin_config['create'] = 'true';
                    this.plugin_config['createOnBlur'] = 'true';
                    this.plugin_config['persist'] = 'false';
                    this.plugin_config['separator'] = ',';
                    this.operators = [FilterOperator.in, FilterOperator.not_in];
                }else{
                    this.input = FilterInput.text;
                    this.operators = [
                        FilterOperator.begins_with,
                        FilterOperator.not_begins_with,
                        FilterOperator.ends_with,
                        FilterOperator.not_ends_with,
                        FilterOperator.contains,
                        FilterOperator.not_contains,
                        FilterOperator.equal,
                        FilterOperator.not_equal
                    ]
                }


                break;
            }
            case "INTEGER":
            case "ARRAY_INT": {
                this.type = FilterType.integer;
                this.input = FilterInput.number;
                this.operators =[
                    FilterOperator.equal,
                    FilterOperator.not_equal,
                    FilterOperator.greater_or_equal,
                    FilterOperator.less_or_equal,
                    FilterOperator.between
                ];
                
                break;
            }
            case "DOUBLE":
            case "ARRAY_DOUBLE": {
                this.type = FilterType.double;
                this.input = FilterInput.number;
                this.operators =[
                    FilterOperator.equal,
                    FilterOperator.not_equal,
                    FilterOperator.greater_or_equal,
                    FilterOperator.less_or_equal,
                    FilterOperator.between
                ];
                
                break;
            }
            case "FLAG": {
                this.type = FilterType.boolean;
                this.input = FilterInput.radio;
                this.operators =[
                    FilterOperator.equal
                ];
                this.values =[true,false];
                this.default_value = true;
                break;
            }
        }

        this.operators.push(FilterOperator.is_null, FilterOperator.is_not_null);
        if (vcfField['TYPE'].startsWith('ARRAY_')){
            this.operators.push(FilterOperator.array_size, FilterOperator.is_not_empty);
        }
    }
}
