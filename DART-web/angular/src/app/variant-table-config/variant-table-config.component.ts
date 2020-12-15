import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import * as $ from 'jquery';
import 'jquery-ui-dist/jquery-ui';
import { PresetService } from '../preset.service';

@Component({
  selector: 'app-variant-table-config',
  templateUrl: './variant-table-config.component.html',
  styleUrls: ['./variant-table-config.component.css']
})
export class VariantTableConfigComponent implements OnInit {

  @Output() onApplyPreset = new EventEmitter<any>();
  @Output() onUpdateList = new EventEmitter<any>();

  fieldList: Array<any>;
  vcf: any;
  fieldNameString: string;
  fieldDescString: string;

  constructor(private bsModalRef: BsModalRef, private presetService: PresetService) { }

  sortable = true;

  ngOnInit() {
    let sortableElement = $("#sortable-table-body").sortable();

    let sortHandler = function (event, ui) {
      let sortingArray = sortableElement.sortable("toArray");
      this.fieldList.sort(function(a, b){
          return sortingArray.indexOf(a.ID.toString()) > sortingArray.indexOf(b.ID.toString()) ? 1 : -1;
      });
    }.bind(this);

    sortableElement.on("sortstop",sortHandler);

    this.init();
  }

  private init(){
    if (this.sortable){
      $("#sortable-table-body").sortable("enable");
    }else{
      $("#sortable-table-body").sortable("disable");
    }
  }


  closeModal(applySettings : boolean){
    if (applySettings) {
      this.onUpdateList.emit(this.fieldList);
    }

    this.bsModalRef.hide();
  }

  savePreset(){
    if (window.confirm("Are you sure to save the preset and overwrite the current one?")){
      let preset: number[] = [];
      this.fieldList.forEach(x =>  {
        if (x.VISIBLE){
          preset.push(x.ID);
        }
      });
      this.presetService.saveFieldsPreset(preset,this.vcf.VCF_TYPE).subscribe(
        (data) => {},
        (err) => console.error(err)
      );
    }
  }

  loadPreset(){
    this.onApplyPreset.emit();
    this.presetService.getFieldsPreset(this.vcf.VCF_TYPE).subscribe(
      (data) => {
        if (data && data.length>0){
          this.fieldList.forEach(x => x.VISIBLE = data.includes(x.ID));
          this.fieldList.sort(function (a, b) {
            let idxA = data.indexOf(a.ID);
            let idxB = data.indexOf(b.ID);
            if (idxA!=-1 || idxB!=-1) {
              if (idxA==-1) return 1;
              if (idxB==-1) return -1;

              return idxA-idxB;

            }else{
              return (this.vcf['FIELDS'].findIndex((element)=>(element['ID']==a.ID)) > this.vcf['FIELDS'].findIndex((element)=>(element['ID']==b.ID))) ? 1 : -1;
            }

            }.bind(this));
          }
        },
      (err) => console.error(err)
    );
  }

  resetPreset(){
    this.fieldList.forEach(x => x.VISIBLE = true);
    this.fieldList.sort(function (a, b) {
      return (this.vcf['FIELDS'].findIndex((element)=>(element['ID']==a.ID)) > this.vcf['FIELDS'].findIndex((element)=>(element['ID']==b.ID))) ? 1 : -1;
    }.bind(this));
  }

  setNameString(){
    if (this.fieldNameString  && this.fieldNameString != '' ){
      this.sortable = false;
    }else{
      this.sortable = true;
    }
    this.init();
  }

  setDescString(){
    if (this.fieldDescString  && this.fieldDescString != '' ){
      this.sortable = false;
    }else{
      this.sortable = true;
    }
    this.init();
  }

  toggleVisible(event) {
    this.fieldList.forEach(x => x.VISIBLE = event.target.checked)
  }

  isAllVisible() {
    return this.fieldList.every(_ => _.VISIBLE);
  }

  moveUp(fieldId: number){
    this.fieldNameString=null;
    this.fieldDescString=null;

    var i = this.fieldList.findIndex(element => element.ID === fieldId);

    // if already at start, nothing to do
    if (i === 0) return;

    // remove old occurrency, if existing
    if (i > 0) {
      let element = this.fieldList.splice( i, 1 )[0];
      this.fieldList.unshift(element);
    }

    this.sortable=true;
    this.init()
  }

  moveDown(fieldId: number){
    this.fieldNameString=null;
    this.fieldDescString=null;

    var i = this.fieldList.findIndex(element => element.ID === fieldId);

    // if already at end, nothing to do
    if (i === (this.fieldList.length-1)) return;

    // remove old occurrency, if existing
    if (i >= 0) {
      let element = this.fieldList.splice( i, 1 )[0];
      this.fieldList.push(element);
    }

    this.sortable=true;
    this.init()
  }

};
