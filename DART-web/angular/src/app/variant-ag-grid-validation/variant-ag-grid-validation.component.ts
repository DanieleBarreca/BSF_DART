import { Component } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {AuthenticationService} from "../authentication.service";
import {AnnotationService} from "../annotation.service";

@Component({
  selector: 'app-variant-ag-grid-validation',
  templateUrl: './variant-ag-grid-validation.component.html',
  styleUrls: ['./variant-ag-grid-validation.component.css']
})
export class VariantAgGridValidationComponent implements ICellRendererAngularComp {
  public params: any;
  private active = true;

  possibleValidationStatus=['ARTIFACT','CONFIRMED', 'UNCONFIRMED'];

  constructor(private authService: AuthenticationService, private annotationService: AnnotationService){}

  agInit(params: any): void {
    this.params = params;
    this.active = params.parent.active;
  }

  refresh(): boolean {
    return false;
  }

  canChangeStatus() {
    return (this.active && this.authService.getPermissions().canValidateVariants);
  }

  changeValidationStatus(validationStatus) {
    if (window.confirm("Do you want to change the validation status to "+validationStatus+"?")){
      this.changeValidationStatusInternal(validationStatus);
    }
  }


  private changeValidationStatusInternal(validationStatus) {

    if (this.canChangeStatus()) {

      let annotedVariantSample = {
        REF_GENOME: this.params.parent.getVcfGenome(),
        HGVSg: this.params.data['TRANSCRIPT']['INTERNAL:HGVSg'],
        HGVSc: this.params.data['TRANSCRIPT']['INTERNAL:HGVSc'],
        GENE: this.params.data['TRANSCRIPT']['INTERNAL:GENE'],
        SAMPLE: {
          REF_ID: this.params.sample
        },
        VARIANT_MODEL_REF: this.params.data['REF_ID'],
        VALIDATION_STATUS: validationStatus
      };

      this.annotationService.postValidationStatus(
        annotedVariantSample
      ).subscribe((data)=> {
          this.params.parent.emitDataChanged();
        },
        (error) => {
          console.error(error);
          window.alert("An error occurred while saving the annotation data");
        }
      )

    }
  }

  removeValidationStatus(refId){
    if (window.confirm("Do you want to remove the validation status?")){
      this.removeValidationStatusInternal(refId);
    }
  }

  private removeValidationStatusInternal(refId) {

    if (this.canChangeStatus()) {


      this.annotationService.deleteVariantSampleAnnotation(
        refId
      ).subscribe((data)=> {
          this.params.parent.emitDataChanged();
        },
        (error) => {
          console.error(error);
          window.alert("An error occurred while saving the annotation data");
        }
      )

    }
  }


}
