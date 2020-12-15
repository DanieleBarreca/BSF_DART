import { Component, OnInit, OnChanges, Input, SimpleChanges, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-impact-chart',
  templateUrl: './impact-chart.component.html',
  styleUrls: ['./impact-chart.component.css']
})
export class ImpactChartComponent implements OnInit, OnChanges {
  
  @Input() variant: any;

  private sources:string[] = ['PolyPhen', 'AF', 'VEP', 'ClinVar', 'CADD', 'SIFT'];
  public labels: string[] =[];
  public data:Number[] = [];

  public options: any = {
    legend: {
      position: 'right'
    },
    scale: {
      ticks: {
        max: 1,
        min: 0,
        stepSize: 0.2,
        display: false
      }
    },
    tooltips: {
      callbacks: {
        label: function (tooltipItem, data) {
          return data.labels[tooltipItem.index];
        }
      }
    },
    animation: false
  }

  constructor() {}

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.init();
  }


  private init() {
    if (!this.variant || !this.variant['TRANSCRIPT']) return;

    this.data = [];
    this.labels.length=0;

    this.sources.forEach(source =>  {
        let value: Number = NaN;
        let valueLabel: string = "";

        switch(source){
          case "PolyPhen": {
            if (this.variant['TRANSCRIPT']["CSQ:PolyPhen_score"]!=null){
              value  = new Number(this.variant['TRANSCRIPT']["CSQ:PolyPhen_score"]);
            }
            if (this.variant['TRANSCRIPT']["CSQ:PolyPhen_prediction"]!=null){
              valueLabel  = this.variant['TRANSCRIPT']["CSQ:PolyPhen_prediction"];
            }
            break;
          }
          case "AF": {
            if (this.variant['TRANSCRIPT']["CSQ:MAX_AF"]!=null){
              value = Math.min(-10.0*Math.log10(this.variant['TRANSCRIPT']["CSQ:MAX_AF"])/25,1);
              valueLabel = new Number(this.variant['TRANSCRIPT']["CSQ:MAX_AF"]*100).toLocaleString() +"%";
            }
            break;
          }
          case "VEP": {
            let attribute = this.variant['TRANSCRIPT']["CSQ:IMPACT"];
            if (attribute != null) {
                switch(attribute){
                    case "HIGH":
                        value = 1.0;
                        break;
                    case "MODERATE":
                        value = 0.8;
                        break;
                    case "LOW":
                        value = 0.4;
                        break;
                    case "MODIFIER":
                        value = 0.2;
                        break;
                }
                let attributeDesc = this.variant['TRANSCRIPT']["CSQ:Consequence"];
                if (attributeDesc!=null){
                  valueLabel = attributeDesc;
                }else{
                  valueLabel = attribute;
                }
            }
            break;
          }
          case "ClinVar": {
            if (this.variant['TRANSCRIPT']["CSQ:CLIN_SIG"]!=null){
              let maxClinSig = this.getMaxClinSig(this.variant['TRANSCRIPT']["CSQ:CLIN_SIG"]);
              if (maxClinSig!=""){
                value = this.getClinSigScore(maxClinSig);
                valueLabel = maxClinSig;
              }
            }
            break;
          }
          case "CADD": {
            if (this.variant['TRANSCRIPT']["CSQ:CADD_PHRED"]!=null){
              value  = this.normalizeCADDScore(this.variant['TRANSCRIPT']["CSQ:CADD_PHRED"]);
              valueLabel = this.variant['TRANSCRIPT']["CSQ:CADD_PHRED"];
            }
            break;
          }
          case "SIFT": {
            if (this.variant['TRANSCRIPT']["CSQ:SIFT_score"]!=null){
              value  = 1.0 - this.variant['TRANSCRIPT']["CSQ:SIFT_score"];
            }
            if (this.variant['TRANSCRIPT']["CSQ:SIFT_prediction"]!=null){
              valueLabel  = this.variant['TRANSCRIPT']["CSQ:SIFT_prediction"];
            }
            break;
          }
          
        }
        
        if (valueLabel==="" && value){
          valueLabel = value.toLocaleString();
        }
        let legendLabel = source;
        if (valueLabel!=null){
          legendLabel += ': '+valueLabel;
        }

        this.labels.push(legendLabel);
        this.data.push(value);        
    });
  }

  private normalizeCADDScore (caddScore: number) : number{
    return Math.min(caddScore/40, 1.0);
  }

  private getMaxClinSig(attribute: Array<String>): string{
    let score: number = 0;
    let result: string = "";
    attribute.forEach((clinSig: string) => {
      let thisScore = this.getClinSigScore(clinSig);
      if ( thisScore > score){  
        score = thisScore;
        result = clinSig;
      }
    });

    return result;
}

private getClinSigScore (clinSig: string) : number{
   switch (clinSig) {
       case "benign":
           return 0.2;
       case "likely_benign":
           return 0.4;
       case "uncertain_significance":
           return 0.5;
       case "likely_pathogenic":
           return 0.8;
       case "pathogenic":
           return 1.0;
       case "risk_factor":
           return 0.6;
   }
   
   return 0;
}

  
}
