import { Component } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {AuthenticationService} from "../authentication.service";
import {BsModalRef, BsModalService} from "ngx-bootstrap";
import {VariantAnnotationModalComponent} from "../variant-annotation-modal/variant-annotation-modal.component";

@Component({
  selector: 'app-variant-ag-grid-pathogenicity',
  templateUrl: './variant-ag-grid-pathogenicity.component.html',
  styleUrls: ['./variant-ag-grid-pathogenicity.component.css']
})
export class VariantAgGridPathogenicityComponent implements ICellRendererAngularComp {
  public params: any;
  private  annotateModal: BsModalRef;
  private active = true;

  constructor(private authService: AuthenticationService, private modalService:BsModalService){}

  agInit(params: any): void {
    this.params = params;
    this.active = params.parent.active;
  }


  refresh(): boolean {
    return false;
  }

  addAnnotation(annotation= null) {
    const initialState = {
      genome: this.params.parent.getVcfGenome(),
      sample: this.params.sample,
      conditions: this.getFilteredConditions(),
      variant: this.params.data,
      annotation: annotation,
      vcfType: this.params.parent.getVcfType()
    };
    this.annotateModal = this.modalService.show(VariantAnnotationModalComponent, {initialState});
    (this.annotateModal.content as VariantAnnotationModalComponent).dataChanged.subscribe(
      (data) => {
        this.params.parent.emitDataChanged();
      }
    );
  }

  canAnnotateVariants() {
    return (this.active && this.authService.getPermissions().canAnnotatePathogenicity && this.getConditions().length >0);
  }

  hasAnnotation(){

    return this.params.value.some(
      (element) => this.isActiveCondition(element['CONDITION']['REF_ID'])
    );

  }

  canAddAnnotation(){
    return this.canAnnotateVariants() && this.getFilteredConditions().length >0;
  }

  isActiveCondition(refId: number){
    return this.getConditions().some((condtionTerm) => condtionTerm.REF_ID === refId);
  }

  getFilteredConditions(){
    return this.getConditions().filter( (condition) =>
      !this.params.value.map((element) => element.CONDITION.REF_ID).includes(condition.REF_ID)
    )
  }

  getConditions() {
    return this.params.parent.conditions;
  }

}
