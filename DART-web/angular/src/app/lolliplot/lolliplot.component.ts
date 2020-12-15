import {Component, OnInit, Input, OnChanges, EventEmitter} from '@angular/core';
import {ExternalProvidersService, getLink} from '../external-providers.service';

import ProteinLolliplot from './oncojs';

import * as d3 from 'd3';
import { AggregationsService } from '../aggregations.service';

import {forkJoin, Observable} from 'rxjs';
import {of} from 'rxjs';
import {debounceTime,switchMap} from "rxjs/operators";

@Component({
  selector: 'app-lolliplot',
  templateUrl: './lolliplot.component.html',
  styleUrls: ['./lolliplot.component.css']
})
export class LolliplotComponent implements OnInit, OnChanges {
  @Input() variant = null;
  @Input() vcf = null;
  @Input() online = true;

  private newVariantEmitter: EventEmitter<null> = new EventEmitter();

  lolliplot: any;
  selected = true;
  loading = true;


  transcript: string;
  position: number;
  ensemblGene:string;
  ensemblProtein: string;

  constructor(private etxternalProvidersService: ExternalProvidersService, private aggregationsService: AggregationsService) { }


  ngOnInit() {

    this.newVariantEmitter.pipe(
      debounceTime(500),
      switchMap(() =>
        forkJoin(
        (this.vcf.VCF_TYPE == "GERMLINE") ? this.etxternalProvidersService.getENSEMBLDomains(this.ensemblProtein, this.vcf.REF_GENOME) : this.etxternalProvidersService.getICGCDomains(this.ensemblGene, this.transcript, this.vcf.REF_GENOME),
        (this.vcf.VCF_TYPE == "GERMLINE") ? (this.online? this.aggregationsService.getMutations(this.vcf.DB_ID, this.transcript) : of([])) : this.etxternalProvidersService.getICGCMutations(this.transcript, this.vcf.REF_GENOME),
        this.online?  this.aggregationsService.getMutations(this.vcf.DB_ID, this.transcript, this.variant['SAMPLE']['SAMPLE_NAME']): of([])
        )
      )
    ).subscribe(([res1, res2, res3]) => {

        if (this.lolliplot){
          this.lolliplot.remove();
        }

        let data = {
          proteins: res1.domains,
          mutations: res2
        };

        let logoElement = (this.vcf.VCF_TYPE == "GERMLINE") ? null : "<a target='_blank' href='" + getLink(this.vcf.REF_GENOME,"ICGC_WEB_GENE",this.ensemblGene) + "'><img src='"+getLink(this.vcf.REF_GENOME,"ICGC_WEB_LOGO","")+"' style='height:100%'><span style='color:#283e5d' style:'font-size:16px'><b/><strong>ICGC</strong></span></a>";
        this.lolliplot = ProteinLolliplot({
          d3: d3,
          element: null,
          selector: "#lolliplot_container",
          data: data,
          domainWidth: res1.length,
          currentPosition: +this.position,
          otherPositions: res3,
          logoElement: logoElement,
          onProteinMouseover: null
        });

        this.loading = false;
      },
      (err) => {
        console.log(err)
        this.loading  = false;
      });

    this.ngOnChanges();

  }

  ngOnChanges(){
    this.loading = true;

    if (this.lolliplot){
      this.lolliplot.remove();
    }

    if (!this.selected || !this.isValid()){
      this.loading = false;
      return;
    }

    this.newVariantEmitter.emit();

  }

  private isValid(){
    if (!this.vcf) return false;

    if (this.vcf.VCF_TYPE!='GERMLINE' && this.vcf.VCF_TYPE!='SOMATIC') return false;

    if (!this.variant) return false;

    this.transcript = this.variant['TRANSCRIPT']['CSQ:Feature'];
    this.position = this.variant['TRANSCRIPT']['CSQ:Protein_position_start'];

    if (!this.transcript) return false;
    if (!this.position) return false;

    this.ensemblGene = this.variant['TRANSCRIPT']['CSQ:Gene'];
    this.ensemblProtein = this.variant['TRANSCRIPT']['CSQ:ENSP'];

    if (this.vcf.VCF_TYPE=='SOMATIC' && !this.ensemblGene) return false;
    if (this.vcf.VCF_TYPE=='GERMLINE' && !this.ensemblProtein) return false;

    return true;
  }

  select(){
    this.selected = true;
    this.ngOnChanges();
  }

  deselect(){
    this.selected = false;
    this.ngOnChanges();
  }

}
