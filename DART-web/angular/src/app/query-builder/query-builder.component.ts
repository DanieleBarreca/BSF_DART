import {
  Component,
  OnInit,
  OnChanges,
  ViewChild,
  ElementRef,
  Input,
  ViewEncapsulation,
  Output,
  EventEmitter
} from '@angular/core';
import { QueryFilter } from './query-filter';
import {qb} from '../imports';
import { QueryBuilderConfig } from './query-builder-config';

@Component({
  selector: 'app-query-builder',
  templateUrl: './query-builder.component.html',
  styleUrls: ['./query-builder.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class QueryBuilderComponent implements OnInit, OnChanges {
  @ViewChild('queryBuilder') queryBuilderElement: ElementRef;
  private qbEl: JQuery;

  @Output()
  ruledChanged: EventEmitter<null> = new EventEmitter();

  @Input()
  config: QueryBuilderConfig;

  constructor() { }

  ngOnInit() {

  }

  ngOnChanges() {
    if (this.config){
      this.buildQueryBuilder();
    }
  }

  private restyleGroup (groupEl) {
    let removeButton = groupEl.find('.rules-group-header button.btn-danger').first();
    $(removeButton).removeClass('btn-danger');
    $(removeButton).html("<i class='glyphicon glyphicon-minus'></i>");
    $(removeButton).css('color', '#d9534f');

    let addButtons = groupEl.find('.rules-group-header button.btn-success');
    $(addButtons).removeClass('btn-success');
    $(addButtons[0]).html("<i class='glyphicon glyphicon-plus'></i><span> Rule</span>");
    $(addButtons[1]).html("<i class='glyphicon glyphicon-plus'></i><span> Ruleset</span>");
  }

  private restyleRule (ruleEl) {
    let button = ruleEl.find('.rule-header button.btn-danger').first();
    $(button).removeClass('btn-danger');
    $(button).html("<i class='glyphicon glyphicon-minus'></i>");
    $(button).css('color', '#d9534f');
  }

  private initSelectize (rule) {
    rule.$el.find('.rule-value-container').css('min-width', '200px').find('.selectize-control').removeClass('form-control');

    rule.filter.valueGetter = function (rule) {
      if (rule.operator.nb_inputs !== 0) {
        return rule.$el.find('.rule-value-container input')[0].selectize.items;
      } else {
        return null;
      }
    };

    rule.filter.valueSetter = function (rule, value) {
      if (rule.operator.nb_inputs !== 0) {
        if (Array.isArray(value)) {
          for (let item of value) {
            rule.$el.find('.rule-value-container input')[0].selectize.addOption({value:item,text:item});
            rule.$el.find('.rule-value-container input')[0].selectize.addItem(item);
          }
        } else {
          rule.$el.find('.rule-value-container input')[0].selectize.addOption({value:value,text:value});
          rule.$el.find('.rule-value-container input')[0].selectize.addItem(value);
        }
      }
    };
  };

  private setBuilderRules (data: any, option?: any, ) {
    if (data && data['rules']) {
      this.qbEl.queryBuilder('setRules', data, option);
    }
  }

  getBuilderRules() {
    if (this.qbEl) {
      return this.qbEl.queryBuilder('getRules');
    }

    return null;
  }

  private buildQueryBuilder(): void {

    this.qbEl = $(this.queryBuilderElement.nativeElement);

    let filter = [];
    for (let field_id in this.config.getFields()) {
      let field = this.config.getFields()[field_id];
      if (field['FIELD_PATH']!="SAMPLE.SAMPLE_NAME"){
        filter.push(new QueryFilter(field));
      }
    }


    this.qbEl.off('afterAddRule.queryBuilder');
    this.qbEl.off('afterAddGroup.queryBuilder');
    this.qbEl.off('afterCreateRuleInput.queryBuilder');
    this.qbEl.off('afterUpdateRuleOperator.queryBuilder');
    this.qbEl.off('rulesChanged.queryBuilder');

    this.qbEl.queryBuilder('destroy');

    let plugins = {
      'bt-selectpicker': { 'liveSearch': "true", 'width': '200px', 'container': 'body' }
    };
    if (this.config.isActive()) {
      plugins['sortable'] = null;
    }
    this.qbEl.queryBuilder({
      plugins: plugins,
      filters: filter,
      operators: qb.DEFAULTS.operators.concat([
        { type: 'array_size', nb_inputs: 1, apply_to: ['string', 'integer', 'double'] }
      ]),
      lang: {
        operators: {
          array_size: 'has size'
        }
      },
      allow_empty: true,
      rules: []
    });

    this.qbEl.on('afterAddRule.queryBuilder', (event, rule) => {
      this.restyleRule(rule.$el);
    });

    this.qbEl.on('afterAddGroup.queryBuilder', (event, group) => {
      this.restyleGroup(group.$el);
    });

    this.qbEl.on('afterCreateRuleInput.queryBuilder', (event, rule) => {
      if (rule.filter.plugin === 'selectize') {
        this.initSelectize(rule);
      } else if (rule.filter.originalPlugin && rule.operator.type === 'array_size') {
        rule.filter.valueGetter = function (rule) {
          return $(rule.$el.find('.rule-value-container input')[0]).val();
        };

        rule.filter.valueSetter = function (rule, value) {
          $(rule.$el.find('.rule-value-container input')[0]).val(value);
        };
      }
    });

    this.qbEl.on('afterUpdateRuleOperator.queryBuilder', (event, rule) => {

      if (rule.filter.plugin === "selectize" && rule.operator.type === 'array_size') {
        rule.filter.plugin = null;
        rule.filter.originalPlugin = "selectize";
        event['builder'].createRuleInput(rule);
      } else if (rule.filter.originalPlugin && rule.operator.type !== 'array_size') {
        rule.filter.plugin = rule.filter.originalPlugin;
        rule.filter.originalPlugin = null;
        event['builder'].createRuleInput(rule);
      }

    });

    this.restyleGroup(this.qbEl.first());
    this.qbEl.find('.rule-container').each((index, el) => { this.restyleRule($(el)) });

    if (this.config.getRules()) {
      this.setBuilderRules(this.config.getRules(), { allow_invalid: true });
      this.ruledChanged.emit();
    }

    if (!this.config.isActive()) {
      this.qbEl.find('input, textarea, button, select').attr('disabled', 'disabled');
      this.qbEl.find('.selectized').each((index, element: any) => {
        element.selectize.disable();
      });
    }

    this.qbEl.on('rulesChanged.queryBuilder',(event, rule) => {
      if (this.qbEl.queryBuilder("validate")) {
        this.config.resetPresetId();
        this.ruledChanged.emit();
      }
    })

  }

}
