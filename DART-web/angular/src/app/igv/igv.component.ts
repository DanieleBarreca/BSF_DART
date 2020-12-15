import {Component, OnInit, Input, OnChanges, SimpleChanges, Output} from '@angular/core';
import {igv} from '../imports';
import {Subject} from "rxjs";
import {debounceTime} from "rxjs/operators";
import {ExternalProvidersService} from "../external-providers.service";
import {AuthenticationService} from "../authentication.service";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-igv',
  templateUrl: './igv.component.html',
  styleUrls: ['./igv.component.css']
})
export class IgvComponent implements OnChanges {

  @Input() vcf: Object = null;
  @Input() variant: Object = null;


  @Input() locus: String = null;
  @Input() sampleName :String = null;
  @Input() selected: boolean = false;

  localIgvFilesOptions: String = null;
  localIgvLocus: String = null;
  localIGVport = 60151;
  isLocalIGVrunning = false;

  browser = null;
  currentSample = null;
  currentLocus :String = null;

  private changeObservable: Subject<SimpleChanges> = new Subject<SimpleChanges>();
  private initialized = false;

  constructor(private externalProviders:ExternalProvidersService, private authService:AuthenticationService) {}

  private initObservable(){
    this.changeObservable.pipe(
      debounceTime(500)
    ).subscribe((changes) => {
      if (this.selected) {
        if (changes['vcf']){
          this.init();
        }else if (changes['variant']){
          if (!this.browser || this.currentSample!=this.variant['SAMPLE']['SAMPLE_NAME']){
            this.init();
          }else{
            let chrom = this.variant['CHROM'];
            let pos = this.variant['POS'];
            this.currentLocus = chrom + ":" + (pos - 20) + "-" + (pos + 20);
            this.localIgvLocus="locus="+this.currentLocus;
            this.browser.search(this.currentLocus)
          }
        }else if (changes['locus']){
          this.currentLocus = this.locus;
          this.localIgvLocus="locus="+this.currentLocus;
          this.browser.search(this.currentLocus)
        }else if (changes['sampleName'] ){
          if (!this.browser || this.currentSample!=this.sampleName) {
            this.init();
          }
        }
      }
    });

    this.initialized = true;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.initialized){
      this.initObservable();
    }

    this.changeObservable.next(changes);
  }

  public deselect() {
    this.selected = false;
    if (this.browser) {
      igv.removeBrowser(this.browser);
      this.browser = null;
    }
  }

  public select() {
    if (!this.selected) {
      this.selected = true;
      if (this.vcf!=null && this.variant!=null) {
        this.init();
      }
    }
  }

  public init() {
    this.externalProviders.checkLocalIGV(this.localIGVport).subscribe(
      data => {
      },
      error => {
        if (error.status == 200) {
          this.isLocalIGVrunning = true;
        }else{
          this.isLocalIGVrunning = false;
        }
      }
    )

    if (this.browser) {
      igv.removeBrowser(this.browser);
      this.browser = null;
    }

    if ( (this.variant == null && (this.locus==null || this.sampleName==null)) || this.vcf == null) {
      return;
    }

    let genome = this.vcf['REF_GENOME'];
    if (new RegExp("GRCh38.p[0-9]+").test(genome)) {
      genome = "hg38"
    }else if (new RegExp("GRCh37.p[0-9]+").test(genome)){
      genome = "hg19"
    }

    if (this.sampleName == null) {
      this.currentSample=this.variant['SAMPLE']['SAMPLE_NAME']
    }else {
      this.currentSample = this.sampleName
    }

    let sampleData = this.vcf['SAMPLES'].filter(sample => sample['SAMPLE_NAME']==[this.currentSample])[0];

    if (this.locus==null){
      let chrom = this.variant['CHROM'];
      let pos = this.variant['POS'];
      this.currentLocus = chrom + ":" + (pos - 20) + "-" + (pos + 20);
    }else{
      this.currentLocus = this.locus
    }


    let tracks = [];

    if (sampleData != null && sampleData['VARIANT_URL'] !== null && sampleData['VARIANT_URL'].length !== 0) {

      tracks.push({
        name: 'Variants',
        indexed: true,
        type: "variant",
        visibilityWindow: 10000000,
        url: this.getURL(sampleData['VARIANT_URL']),
        headers:this.getHeaders(sampleData['VARIANT_URL']),
      });
    }

    if (sampleData != null && sampleData['ALIGNMENT_URL'] !== null && sampleData['ALIGNMENT_URL'].length !== 0) {
      tracks.push({
        name: 'Reads',
        type: "alignment",
        indexed: true,
        samplingDepth: 500,
        alignmentRowHeight: 10,
        url: this.getURL(sampleData['ALIGNMENT_URL']),
        headers:this.getHeaders(sampleData['ALIGNMENT_URL'])
      });
    }

    if (sampleData != null && sampleData['COVERAGE_TRACK_URL'] !== null && sampleData['COVERAGE_TRACK_URL'].length !== 0) {
      tracks.push({
        name: 'Coverage',
        type: "annotation",
        displayMode:'EXPANDED',
        url: this.getURL(sampleData['COVERAGE_TRACK_URL']),
        headers:this.getHeaders(sampleData['COVERAGE_TRACK_URL'])
      });
    }


    let myOptions:any = {
      locus: this.currentLocus,
      showNavigation: true,
      showCenterGuide: true,
      trackDefaults: {
        bam: {
          coverageThreshold: 0.2,
          coverageQualityWeight: true
        }
      },
      flanking: 5000,
      tracks: tracks
    };

    if (navigator.onLine){
      myOptions.genome=genome;
    }else{
      myOptions.reference={
        "id":genome,
        "name": "Human ("+genome+")",
        "fastaURL": environment.server_static+"genomes/"+genome+"/genome.fasta",
        "indexURL":environment.server_static+"genomes/"+genome+"/genome.fasta.fai",
        "cytobandURL":environment.server_static+"genomes/"+genome+"/cytoBandIdeo.txt.gz",
        "tracks":[{
          "name":"Refseq Genes",
          "format":"refgene",
          "url":environment.server_static+"genomes/"+genome+"/refGene.txt.gz",
          "indexed":false,
          "visibilityWindow":-1,
          "removable":false,
          "order":1000000}]
      }
    }


    this.localIgvLocus="locus="+myOptions.locus;
    this.localIgvFilesOptions="genome="+myOptions.genome;

    let files = Array();
    let names = Array();
    for (let track in myOptions.tracks){
      files.push(myOptions.tracks[track].url);
      names.push(myOptions.tracks[track].name);
    }
    this.localIgvFilesOptions+="&file="+files.toString();
    this.localIgvFilesOptions+="&name="+names.toString();
    this.localIgvFilesOptions+="&merge=true";

    igv.createBrowser($("#igv_container"), myOptions).then(function(theBrowser){
      this.browser = theBrowser;
    }.bind(this));
  }

  public canShowLocalIGV(){
      return this.localIgvLocus && this.localIgvFilesOptions && this.isLocalIGVrunning;
  }

  private getHeaders(url){

    let headers={};

    if (url.startsWith("local://")){
      headers= {
        'Authorization': 'Basic ' + this.authService.getAuthToken()
      }
    }

    return headers;
  }

  private getURL(url:string){

    if (url.startsWith("local://")){
      url=url.replace("local://",environment.server_static);
    }

    return url;
  }
}


