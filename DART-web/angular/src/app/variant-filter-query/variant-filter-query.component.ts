import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges,
  ViewEncapsulation,
  TemplateRef,
  ViewChild,
  ElementRef, ViewChildren, QueryList
} from '@angular/core';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {VcfService} from '../vcf.service';
import {VariantService} from '../variant.service';
import {SAMPLE_DROPDOWN_CONFIG, GENES_INPUT_CONFIG} from './selectize-config';
import {PresetModalComponent} from '../preset-modal/preset-modal.component';
import {PresetService} from '../preset.service';
import {QueryBuilderComponent} from '../query-builder/query-builder.component';
import {QueryBuilderConfig} from '../query-builder/query-builder-config';
import {QueryPresetsComponent} from '../query-presets/query-presets.component';
import {BedService} from '../bed.service';
import {GenePanelPresetsComponent} from '../gene-panel-presets/gene-panel-presets.component';
import * as deepEqual from "deep-equal";
import {AuthenticationService} from '../authentication.service';
import {FullQuery, QueryFilter, QueryPanel, RelatedSampleInfo, Sex} from "./FullQuery";

declare const $: any;

@Component({
  selector: ' app-variant-filter-query',
  templateUrl: './variant-filter-query.component.html',
  styleUrls: ['./variant-filter-query.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class VariantFilterQueryComponent implements OnInit, OnChanges {

  @ViewChild('queryBuilder') qb: QueryBuilderComponent;
  @ViewChild('genesSelectize') genesSelectizeEl: ElementRef;

  @ViewChildren('otherSamplesSelectizeEl') otherSamplesSelectizeEl:QueryList<ElementRef>;
  @ViewChildren('otherSamplesSexSelectizeEl') otherSamplesSexSelectizeEl:QueryList<ElementRef>;

  @Input() query: FullQuery;
  @Input() reportMode: boolean = false;
  @Input() active: boolean = true;
  @Input() vcfHeader: Object = null;

  isVCFSectionCollapsed = false;
  isBEDSectionCollapsed = false;
  isQuerySectionCollapsed = false;

  modalRef: BsModalRef;
  //vcfHeader: Object;
  queryBuilderConfig: QueryBuilderConfig;

  constructor(
    private auth: AuthenticationService,
    private vcfService: VcfService,
    private bedService: BedService,
    private variantService: VariantService,
    private modalService: BsModalService,
    private presetService: PresetService) {

  }

  ngOnInit() {
    if (this.reportMode) this.active = false;

    this.isVCFSectionCollapsed = !this.active;
    this.isBEDSectionCollapsed = !this.active;
    this.isQuerySectionCollapsed = !this.active;

    if (!this.reportMode) {
      this.retrieveExistingPreset();
      this.retrieveExistingPanelPreset();

      if (this.qb) {
        this.qb.ruledChanged.subscribe(
          data => {

            if (this.qb.config.getPresetId() == null || this.qb.config.getPresetId() != this.query.FILTER.REF_ID) {
              this.query.FILTER.RULE = this.qb.getBuilderRules();

              if (this.query.FILTER.RULE == null) {
                this.query.FILTER.REF_ID = null;
                this.query.FILTER.MNEMONIC = null;
              } else {
                this.retrieveExistingPreset();
              }
            }
          }
        );
      }
    }

  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.reportMode) this.active = false;

    if (changes.query && (changes.query.firstChange || !deepEqual(changes.query.currentValue, changes.query.previousValue))) {
      if (!this.query) {
        this.query = new FullQuery();
        this.vcfHeader = null;
        this.queryBuilderConfig = null;
      } else if (this.query.VCF_REF_ID) {
        this.resetQuery(this.query.VCF_REF_ID);
      }
    }
  }

  private initSampleSelectize(index) {

    let selectizeEl=undefined

    if (index<this.otherSamplesSelectizeEl.length){
      selectizeEl=this.otherSamplesSelectizeEl.toArray()[index];
    }

    if (selectizeEl){

      let selectizeObj=$(selectizeEl.nativeElement).selectize(SAMPLE_DROPDOWN_CONFIG)[0].selectize;

      selectizeObj.off('change');
      selectizeObj.clear();
      selectizeObj.clearOptions();

      if (this.vcfHeader) {

        Object.keys(this.vcfHeader['SAMPLES']).forEach((key, sampleIdx) => {
          let sampleName = this.vcfHeader['SAMPLES'][key]['SAMPLE_NAME'];
          if ((sampleName !== this.query.SAMPLE_NAME)) {
            let addOption = true;

            for (var relatedSampleIdx in this.query.RELATED_SAMPLES) {
              if ((index!=relatedSampleIdx) && (sampleName==this.query.RELATED_SAMPLES[relatedSampleIdx].SAMPLE)){
                addOption = false;
                break;
              }
            }
            if (addOption) selectizeObj.addOption({label: sampleName, value: sampleName});
          }
        });
      }

      if (!this.query.RELATED_SAMPLES[index].SAMPLE){
        this.query.RELATED_SAMPLES[index].SAMPLE=selectizeObj.options[Object.keys(selectizeObj.options)[0]].value
      }
      selectizeObj.addItem(this.query.RELATED_SAMPLES[index].SAMPLE);

      selectizeObj.on('change', (value) => {
        if (value == ""){
          this.query.RELATED_SAMPLES.splice(index,1);
        }else {
          this.query.RELATED_SAMPLES[index].SAMPLE = value
          for (var otherIndex = 0; otherIndex < this.otherSamplesSelectizeEl.length; otherIndex++) {
            if (otherIndex !== index) {
              this.initSampleSelectize(otherIndex);
            }
          }
        }
      });



    }

  }

  private initOtherSamplesSelectize() {
    for (var index=0; index < this.otherSamplesSelectizeEl.length; index++) {
      this.initSampleSelectize(index);
    }
  }

  private initSexSelectize(index){


      let selectizeEl=this.otherSamplesSexSelectizeEl.toArray()[index];
      let selectizeObj=$(selectizeEl.nativeElement).selectize(SAMPLE_DROPDOWN_CONFIG)[0].selectize;

      selectizeObj.off('change');
      selectizeObj.clear();
      selectizeObj.clearOptions();

      for (let value in Sex) {
        selectizeObj.addOption({label: Sex[value], value: value});
      }

      selectizeObj.addItem( this.query.RELATED_SAMPLES[index].SEX)

      selectizeObj.on('change', (value) => {
        this.query.RELATED_SAMPLES[index].SEX=value
      });

  }


  private initOtherSamplesSexSelectize() {
    for (var index=0; index < this.otherSamplesSexSelectizeEl.length; index++) {
      this.initSexSelectize(index);
    }
  }

  private retrieveExistingPreset(){
    this.presetService.checkQueryPreset(this.query.FILTER.RULE).subscribe(
      (data) => {
        this.query.FILTER.REF_ID = data.REF_ID;
        this.query.FILTER.MNEMONIC = data.MNEMONIC;
        this.query.FILTER.VALID = data.VALID;
      },
      (error) => {
        this.query.FILTER.REF_ID = null;
        this.query.FILTER.MNEMONIC = null;
        this.query.FILTER.VALID = null;
        console.error(error);
      }
    );
  }

  private retrieveExistingPanelPreset(){
    this.presetService.checkPanelPreset(this.query.PANEL).subscribe(
      (data) => {
        this.query.PANEL.REF_ID = data.REF_ID;
        this.query.PANEL.MNEMONIC = data.MNEMONIC;
        this.query.PANEL.VALID = data.VALID;
      },
      (error) => {
        this.query.PANEL.REF_ID = null;
        this.query.PANEL.MNEMONIC = null;
        this.query.PANEL.VALID = null;
        console.error(error);
      }
    );
  }

  private initGenesSelectize() {
    if (this.genesSelectizeEl) {
      let geneSelectize= $(this.genesSelectizeEl.nativeElement).selectize(GENES_INPUT_CONFIG)[0].selectize;
      geneSelectize.off('change');
      geneSelectize.clear();
      geneSelectize.clearOptions();


      if (this.query && this.query.PANEL.GENES) {
        this.query.PANEL.GENES.forEach(
          (gene) => {
            geneSelectize.addOption({label: gene, value: gene})
            geneSelectize.addItem(gene);
          }
        );
      }

      geneSelectize.on('change', () => {
        this.query.PANEL.REF_ID = null;
        this.query.PANEL.MNEMONIC = null;
        this.query.PANEL.GENES = geneSelectize.items;
        this.retrieveExistingPanelPreset();
      });
    }
  }


  setNewFilter() {
    this.query.FILTER = new QueryFilter();
    this.queryBuilderConfig = new QueryBuilderConfig(this.vcfHeader['FIELDS'], this.active && this.canSavePreset(), this.query.FILTER.RULE, this.query.FILTER.REF_ID);
  }

  setNewPanel() {
    this.query.PANEL.REF_ID = null;
    this.query.PANEL.MNEMONIC = null;
    this.query.PANEL.BED_NAME = null;
    this.query.PANEL.BED_REF_ID = null;
    this.query.PANEL.GENES = [];
    this.initGenesSelectize();
    this.retrieveExistingPanelPreset();
  }


  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template, {class: 'file-select-modal'});
  }


  savePresetModal() {
    this.modalRef = this.modalService.show(PresetModalComponent);
    (this.modalRef.content as PresetModalComponent).presetConfirmed.subscribe(
      (data) => {
        this.query.FILTER.MNEMONIC = data;
        this.presetService.saveQueryPreset(this.query.FILTER).subscribe(
          (data) => {
            this.retrieveExistingPreset();
            this.modalRef.hide();
          },
          (err) => console.error(err)
        );

      }
    )
  }

  loadPresetModal() {
    this.modalRef = this.modalService.show(QueryPresetsComponent, {
      class: 'file-select-modal',
      initialState: {vcfId: this.vcfHeader['DB_ID']}
    });
    (this.modalRef.content as QueryPresetsComponent).presetSelected.subscribe(
      (preset) => {

        this.modalRef.hide();
        this.query.FILTER=preset;

        this.queryBuilderConfig = new QueryBuilderConfig(this.vcfHeader['FIELDS'], this.canSavePreset() && this.active, this.query.FILTER.RULE, this.query.FILTER.REF_ID);
      },
      (err) => console.error(err)
    )
  }

  savePanelModal() {
    this.modalRef = this.modalService.show(PresetModalComponent);
    this.savePanelPreset(this.modalRef.content);
  }

  private savePanelPreset(presetModal: PresetModalComponent) {
    presetModal.presetConfirmed.subscribe(
      (data) => {
        this.query.PANEL.MNEMONIC = data;
        this.presetService.savePanelPreset(this.query.PANEL).subscribe(
          (data) => {
            this.modalRef.hide();
            this.retrieveExistingPanelPreset();
          },
          (err) => console.error(err)
        );

      }
    )
  }

  loadPanelModal() {
    this.modalRef = this.modalService.show(GenePanelPresetsComponent, {class: 'file-select-modal'});
    (this.modalRef.content as GenePanelPresetsComponent).presetSelected.subscribe(
      (panel: QueryPanel) => {
        this.query.PANEL = panel;
        this.initGenesSelectize();
        this.modalRef.hide();
      },
      (err) => console.error(err)
    );
  }

  setSampleVcf(event) {
    if ( (event.SAMPLE.SAMPLE_NAME!=this.query.SAMPLE_NAME) || event.VCF.DB_ID!=this.query.VCF_REF_ID) {
      this.query.SAMPLE_NAME = event.SAMPLE.SAMPLE_NAME;
      this.query.SAMPLE_REF_ID = event.SAMPLE.REF_ID;
      this.resetQuery(event.VCF.DB_ID);
    }

    if (this.modalRef) {
      this.modalRef.hide();
    }
  }

  setBedFile(bedFile: any) {
    this.query.PANEL.REF_ID = null;
    this.query.PANEL.MNEMONIC = null;
    this.query.PANEL.BED_NAME = bedFile['FILE_NAME'];
    this.query.PANEL.BED_REF_ID = bedFile['DB_ID'];
    if (this.modalRef) {
      this.modalRef.hide();
    }
    this.retrieveExistingPanelPreset();
  }

  resetBedFile() {
    this.query.PANEL.REF_ID = null;
    this.query.PANEL.MNEMONIC = null;
    this.query.PANEL.BED_NAME = null;
    this.query.PANEL.BED_REF_ID = null;
    if (this.modalRef) {
      this.modalRef.hide();
    }
    this.retrieveExistingPanelPreset();
  }

  submitQuery() {
    if (this.query) {
      if (this.query.FILTER.REF_ID == null && !this.canSavePreset()) {
        window.alert("Please select a query preset")
        return null;
      } else if (this.query.PANEL.REF_ID == null && !this.canSavePanel()) {
        window.alert("Please select a query panel")
        return null;
      } else {
        return this.variantService.submitQuery(this.query);
      }

    } else {
      return null;
    }
  }

  private resetQuery(vcfFileId: number) {

     if (!this.vcfHeader || this.vcfHeader['DB_ID']!=vcfFileId) {
       this.vcfService.getVcfById(vcfFileId).subscribe(
        (data) => {
          if (this.vcfHeader && data['DB_ID'] !== this.vcfHeader['DB_ID']) {
            this.query.FILTER = new QueryFilter();
            this.query.PANEL = new QueryPanel();
          }
          this.vcfHeader = data;
          this.initQueryBuilder();
        },
        (err) => {
          console.error(err);
        }
      );
    }else{
      this.initQueryBuilder()
    }
  }

  initQueryBuilder(){
    if (!this.query.SAMPLE_REF_ID) {
      let firstSample = Object.values(this.vcfHeader['SAMPLES'])[0];
      this.query.SAMPLE_NAME = firstSample['SAMPLE_NAME'];
      this.query.SAMPLE_REF_ID = firstSample['REF_ID'];
    }

    let otherSamplesFieldIdx = this.vcfHeader['FIELDS'].findIndex((element) => (element['FIELD_PATH'] == 'OTHER_SAMPLES'));
    this.vcfHeader['FIELDS'][otherSamplesFieldIdx]['POSSIBLE_VALUES'] = [];
    Object.keys(this.vcfHeader['SAMPLES']).forEach((key, index) => {
      let sampleName = this.vcfHeader['SAMPLES'][key]['SAMPLE_NAME'];
      if (sampleName != this.query.SAMPLE_NAME) {
        this.vcfHeader['FIELDS'][otherSamplesFieldIdx]['POSSIBLE_VALUES'].push(sampleName + ":HET");
        this.vcfHeader['FIELDS'][otherSamplesFieldIdx]['POSSIBLE_VALUES'].push(sampleName + ":HOM");
      }
    });

    if (this.active) {
      this.otherSamplesSelectizeEl.changes.subscribe(() => {
        this.initOtherSamplesSelectize();
      })

      this.otherSamplesSexSelectizeEl.changes.subscribe(() => {
        this.initOtherSamplesSexSelectize();
      })

      this.initGenesSelectize();
    }

    if (this.query && this.query.FILTER.RULE) {
      this.queryBuilderConfig = new QueryBuilderConfig(this.vcfHeader['FIELDS'].filter(field => field['QUERYABLE']), this.canSavePreset() && this.active, this.query.FILTER.RULE, this.query.FILTER.REF_ID);
    }
    this.query.VCF_REF_ID = this.vcfHeader['DB_ID'];
    this.query.VCF_NAME = this.vcfHeader['VCF_FILE'];
  }

  addOtherSample() {
    this.query.RELATED_SAMPLES.push(new RelatedSampleInfo())
  }

  canAddOtherSample() {
    return this.vcfHeader && (this.query.RELATED_SAMPLES.length < (this.vcfHeader['SAMPLES'].length-1))
  }

  canSavePreset() {
    if (this.auth.getUser() && this.auth.getPermissions() && this.auth.getPermissions().canSavePreset) {
      return true;
    }

    return false;
  }

  canSavePanel() {
    if (this.auth.getUser() &&this.auth.getPermissions() && this.auth.getPermissions().canSavePanel) {
      return true;
    }

    return false;
  }

}
