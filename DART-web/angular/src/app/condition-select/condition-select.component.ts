import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {AnnotationService} from "../annotation.service";

declare const $: any;

@Component({
  selector: 'app-condition-select',
  templateUrl: './condition-select.component.html',
  styleUrls: ['./condition-select.component.css']
})
export class ConditionSelectComponent implements OnInit {
  @ViewChild('selectDictionary') selectDictionary: ElementRef;
  @ViewChild('selectTerm') selectTerm: ElementRef;
  @Output('conditionSelected') conditionSelected: EventEmitter<number> = new EventEmitter<number>();

  private selectTermSelectize=null;
  private selectDictionarySelectize=null;
  private countPerPage = 100;
  private totalCount = null;

  selectedDictionary = null;
  selectedCondition = null;

  constructor(private annotationService: AnnotationService) { }

  ngOnInit() {
  }

  init(){
    this.annotationService.getConditionDictionaries().subscribe(
      (data)=> {
        this.initDictionarySelectize(data);
      }
    );
  }

  initDictionarySelectize(dictionaries){
    this.selectDictionarySelectize = $(this.selectDictionary.nativeElement).selectize({
      placeholder: "Classification Std.",
      options: dictionaries.map( (dictionary: string) => { return {
        value:dictionary,
        text: dictionary
      }}),
      onChange: function(value, $item){
        if (value){
          this.selectedDictionary = value;
          if (this.selectTermSelectize) {
            this.selectTermSelectize[0].selectize.destroy();
          }
          this.initTermSelectize();
        }else{
          if (this.selectTermSelectize) {
            this.selectTermSelectize[0].selectize.destroy();
            this.selectTermSelectize = null;
          }
          this.selectedDictionary = null;
        }
        this.selectedCondition = null;
      }.bind(this)
    });
  }

  initTermSelectize(){
    this.selectTermSelectize = $(this.selectTerm.nativeElement).selectize({
      plugins: ['infinite_scroll'],
      placeholder: "Medical condition",
      create: false,
      valueField: 'REF_ID',
      labelField: 'LABEL',
      searchField: ['LABEL', 'DESCRIPTION','CODE'],
      load: this.loadConditions.bind(this),
      preload: 'focus',
      maxOptions: false,
      onChange: function(value, $item){
        if (value!= null) {
          this.selectedCondition = value as number;
        }else{
          this.selectedCondition = null;
        }
      }.bind(this)
    });
  }


  loadConditions(query, page, callback) {

     this.annotationService.getConditionTerms(this.selectedDictionary, query, ((page || 1) -1) * this.countPerPage, this.countPerPage).subscribe(
        (data) => {
          this.totalCount = data['TOTAL_COUNT'];
          callback(data['RESULTS']);
        },
        (err) => {
          callback()
        }
      );

  };

  emitCondition(){
    if (this.selectedCondition){
      this.conditionSelected.emit(this.selectedCondition);
    }
  }

}
