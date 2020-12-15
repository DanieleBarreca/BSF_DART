import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {BsModalRef} from "ngx-bootstrap";
import {AnnotationService} from "../annotation.service";

@Component({
  selector: 'app-variant-annotation-modal',
  templateUrl: './variant-annotation-modal.component.html',
  styleUrls: ['./variant-annotation-modal.component.css', '../variant-ag-grid-pathogenicity/variant-ag-grid-pathogenicity.component.css']
})
export class VariantAnnotationModalComponent implements OnInit {

  dataChanged: EventEmitter<null> = new EventEmitter();

  genome:string;
  sample: number;
  variant: any;
  annotation: any;
  vcfType: string;
  conditions: any;

  @ViewChild('selectCondition') selectCondition: ElementRef;
  @ViewChild('selectDictionary') selectDictionary: ElementRef;
  @ViewChild('selectTerm') selectTerm: ElementRef;
  @ViewChild('selectInheritance') selectInheritance: ElementRef;

  private selectTermSelectize=null;
  private selectDictionarySelectize=null;
  private selectInheritanceSelectize=null;
  private selectConditionSelectize = null;

  selectedTerm: number;
  selectedDictionary: string;
  selectedInheritance: string;
  selectedCondition: number;

  selectedConditionLabel: string;

  constructor(public modalRef: BsModalRef, private annotationService: AnnotationService) {}

  ngOnInit() {

      this.initConditionSelectize();
  }

  destroySelectize(selectzizeElement){
    if (selectzizeElement && selectzizeElement[0].selectize) {
      selectzizeElement[0].selectize.destroy();
    }
    return null;
  }

  initConditionSelectize(){
    if (this.annotation){
      this.selectedCondition = this.annotation.CONDITION.REF_ID;
      this.selectedConditionLabel = this.annotation.CONDITION.LABEL + " [" + this.annotation.CONDITION.CODE + "]"
      this.initDictionarySelectize();
    }else if (this.conditions.length == 1){

      this.selectedCondition = this.conditions[0].REF_ID;
      this.selectedConditionLabel = this.conditions[0].LABEL + " [" + this.conditions[0].CODE + "]"
      this.initDictionarySelectize();

    }else {
      this.selectConditionSelectize = $(this.selectCondition.nativeElement).selectize({
        options: this.conditions.map((condition: any) => {
          return {
            value: condition.REF_ID,
            text: condition.LABEL + " [" + condition.CODE + "]"
          }
        }),
        onChange: function (value, $item) {
          if (value) {
            this.selectedCondition = value;
            if (!this.selectDictionarySelectize) {
              this.initDictionarySelectize();
            }
          } else {
            this.selectDictionarySelectize = this.destroySelectize(this.selectDictionarySelectize);
            this.selectTermSelectize = this.destroySelectize(this.selectTermSelectize);
            this.selectInheritanceSelectize = this.destroySelectize(this.selectInheritanceSelectize);
            this.selectedTerm = null;
            this.selectedInheritance = null;
            this.selectedDictionary = null;
            this.selectedCondition = null;
          }
        }.bind(this)
      });

      this.selectConditionSelectize[0].selectize.setValue(this.conditions[0].REF_ID, false);

    }
  }

  initDictionarySelectize(){
    this.annotationService.getAnnotationDictionaries().subscribe(
      (dictionaries) => {
            this.selectDictionarySelectize = $(this.selectDictionary.nativeElement).selectize({
              options: dictionaries.map( (dictionary: string) => { return {
                value:dictionary,
                text: dictionary
              }}),
              onChange: function(value, $item){
                if (value){
                  this.selectedDictionary = value;
                  this.selectTermSelectize = this.destroySelectize(this.selectTermSelectize);
                  this.selectInheritanceSelectize = this.destroySelectize(this.selectInheritanceSelectize);
                  this.selectedTerm = null;
                  this.selectedInheritance = null;
                  this.initTermSelectize();
                }else{
                  this.selectTermSelectize = this.destroySelectize(this.selectTermSelectize);
                  this.selectInheritanceSelectize = this.destroySelectize(this.selectInheritanceSelectize);
                  this.selectedTerm = null;
                  this.selectedInheritance = null;
                  this.selectedDictionary = null;;
                }
              }.bind(this)
            });

            let selectedDictionary;
            if (this.annotation){
              selectedDictionary = this.annotation['ANNOTATION']['TYPE'];
            } else if (this.vcfType=='GERMLINE') {
              selectedDictionary = "ACMG_AMP_GERMLINE";
            }else if (this.vcfType=='SOMATIC') {
              selectedDictionary = "ACMG_AMP_CANCER";
            }

            if (selectedDictionary) {
              this.selectDictionarySelectize[0].selectize.setValue(selectedDictionary, false);
            }
        }
    );
  }

  initTermSelectize(){
    this.annotationService.getAnnotationTerms(this.selectedDictionary).subscribe(
      (terms) => {
        this.selectTermSelectize = $(this.selectTerm.nativeElement).selectize({
          options: terms.map((term) => {
            return {
              value: term['REF_ID'],
              text: term['LABEL']
            }
          }),
          onChange: function (value, $item) {
            if (value) {
              this.selectedTerm = value;
              this.selectInheritanceSelectize = this.destroySelectize(this.selectInheritanceSelectize);
              this.selectedInheritance = null;
              this.initInheritanceSelectize();
            } else {
              this.selectInheritanceSelectize = this.destroySelectize(this.selectInheritanceSelectize);
              this.selectedInheritance = null;
              this.selectedTerm = null;
            }
          }.bind(this)
        });
        if (this.annotation){
          this.selectTermSelectize[0].selectize.setValue(this.annotation['ANNOTATION']['REF_ID'], false);
        }
      });


  }

  initInheritanceSelectize() {
    this.annotationService.getInheritanceTerms(this.selectedDictionary).subscribe(
      (terms) => {
        this.selectInheritanceSelectize = $(this.selectInheritance.nativeElement).selectize({
          options: terms.map((term) => {
            return {
              value: term['REF_ID'],
              text: term['LABEL']
            }
          }),
          onChange: function (value, $item) {
            if (value) {
              this.selectedInheritance = value;
            } else {
              this.selectedInheritance = null;
            }
          }.bind(this)
        });
        if (this.annotation) {
          this.selectInheritanceSelectize[0].selectize.setValue(this.annotation['INHERITANCE']['REF_ID'], false);
        }
      });

  }

  canSave(){
    let result: boolean = (
      this.genome != null &&
      this.sample!=null &&
      this.selectedCondition!=null &&
      this.variant!=null &&
      this.variant['TRANSCRIPT']['INTERNAL:HGVSg']!=null &&
      this.selectedDictionary!=null &&
      this.selectedTerm!=null &&
      this.selectedInheritance!=null
    );

    if (this.annotation){
      result = result &&
        (this.selectedDictionary != this.annotation['ANNOTATION']['TYPE'] ||
          this.selectedTerm != this.annotation['ANNOTATION']['REF_ID'] ||
          this.selectedInheritance != this.annotation['INHERITANCE']['REF_ID']
        );
    }

    return result;

  }

  save(){
    if (window.confirm("Do you want to save a new annotation?")){
      this.saveInternal()
    }
  }

  edit(){
    if (window.confirm("Do you want to modify the existing annotation?")){
      this.saveInternal()
    }
  }

  delete(){
    if (window.confirm("Do you want to delete the existing annotation?")){
      this.deleteInternal();
    }
  }

  private saveInternal(){
    if (this.canSave()){
      this.annotationService.postAnnotation(
        {
          REF_GENOME: this.genome,
          HGVSg: this.variant['TRANSCRIPT']['INTERNAL:HGVSg'],
          HGVSc: this.variant['TRANSCRIPT']['INTERNAL:HGVSc'],
          GENE: this.variant['TRANSCRIPT']['INTERNAL:GENE'],
          SAMPLE: {
            REF_ID: this.sample
          },
          ANNOTATION: {
            REF_ID: +this.selectedTerm
          },
          CONDITION: {
            REF_ID:  +this.selectedCondition
          },
          INHERITANCE: {
            REF_ID:  +this.selectedInheritance
          },
          VARIANT_MODEL_REF: this.variant['REF_ID']
        }
      ).subscribe((data)=> {
          this.dataChanged.emit();
          this.modalRef.hide();
        },
        (error) => {
          console.error(error);
          window.alert("An error occurred while saving the annotation data");
        }

      )
    }else{
      window.alert("Please check you inserted all the values");
    }

  }

  private deleteInternal(){
    if (this.annotation){
      this.annotationService.deleteAnnotation(
        this.annotation['REF_ID']
      ).subscribe((data)=> {
          this.dataChanged.emit();
          this.modalRef.hide();
        },
        (error) => {
          console.error(error);
          window.alert("An error occurred while deleting the existing annotation");
        }

      )
    }
  }
}
