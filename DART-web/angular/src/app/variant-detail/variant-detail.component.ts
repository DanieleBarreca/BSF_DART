import {Component, OnInit, Input, OnChanges, ViewChild} from '@angular/core';
import { VcfService } from '../vcf.service';
import {ExternalProvidersService, getLink, links} from "../external-providers.service";
import {Subject} from "rxjs";
import {debounceTime,switchMap} from "rxjs/operators";
import {AuthenticationService} from "../authentication.service";
import {LolliplotComponent} from "../lolliplot/lolliplot.component";

@Component({
  selector: 'app-variant-detail',
  templateUrl: './variant-detail.component.html',
  styleUrls: ['./variant-detail.component.css']
})
export class VariantDetailComponent implements OnChanges, OnInit{

  @Input() variant:any;
  @Input() vcf:any = null;
  @ViewChild("lolliplot") lolliplot: LolliplotComponent;

  hgncId = null;
  geneDescription = null;
  omimId = null;

  fieldsTabSelected: boolean =false;

  private geneObservable : Subject<string> = new Subject<string>();

  constructor(private vcfService: VcfService,  private externalProvidersService: ExternalProvidersService, private authService: AuthenticationService) { }

  ngOnInit() {

    if (this.isOnline()){
      this.geneObservable.pipe(
        debounceTime(500),
        switchMap(gene => this.externalProvidersService.getHGNCinfo(gene,this.vcf.REF_GENOME ))
      ).subscribe(
        (data) => {
          if (data && data['response'] && data['response']['numFound'] >= 1) {
            let doc = data['response']['docs'][0];
            this.hgncId = doc['hgnc_id'];
            this.geneDescription = doc['name'];
            if (doc["omim_id"] && (doc["omim_id"].length >= 1)) {
              this.omimId = doc["omim_id"][0]
            }
          }
        });

      this. ngOnChanges()
    }

  }

  getLink(type:string, query: string): string {
    return getLink(this.vcf.REF_GENOME,type, query);
  }

  ngOnChanges(){
    this.hgncId = null;
    this.geneDescription = null;
    this.omimId= null;

    if (this.variant && this.variant['TRANSCRIPT']['CSQ:Gene']) {
        this.geneObservable.next(this.variant['TRANSCRIPT']['CSQ:Gene'])
    }
  }

  getRsId(){

    if (this.variant['TRANSCRIPT']['CSQ:Existing_variation']){
      let ids = this.variant['TRANSCRIPT']['CSQ:Existing_variation'].filter(
        id => id.startsWith("rs")
      )
      if (ids.length>0){
        return ids[0]
      }
    }

    return null;

  }

  showTable(){
    this.fieldsTabSelected = true;
  }

  hideTable(){
    this.fieldsTabSelected = false;
  }

  isOnline(){
    return navigator.onLine;
  }

  variantSelect(){
    if (this.lolliplot) {
      this.lolliplot.select();
    }
  }

  variantDeselect(){
    if (this.lolliplot) {
      this.lolliplot.deselect();
    }
  }

}
