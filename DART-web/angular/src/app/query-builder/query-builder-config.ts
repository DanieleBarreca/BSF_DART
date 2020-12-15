
export class QueryBuilderConfig {
    private fields;
    private active: boolean;
    private rules: object;
    private presetId: number;


    constructor(fields, active, rules, presetId) {
        this.fields = fields;
        this.active = active;
        this.rules = rules;
        this.presetId = presetId;
    }


    getFields() {return this.fields};
    isActive() {return this.active};
    getRules() {return this.rules};
    getPresetId() {return this.presetId}
    resetPresetId() {this.presetId = null}
    activate() {this.active = true};
 }
