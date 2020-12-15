import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-frequency-chart',
  templateUrl: './frequency-chart.component.html',
  styleUrls: ['./frequency-chart.component.css']
})
export class FrequencyChartComponent implements OnInit, OnChanges {

  @Input() variant: any;

  populations = ['ALL', 'AFR', 'AMR', 'EAS', 'FIN', 'EUR', 'SAS', 'OTH'];

  data = [];

  public barChartOptions: any = {
    scaleShowVerticalLines: false,
    responsive: true,
    tooltips: {
      callbacks: {
        label: function (tooltipItem, data) {
          const source = data.datasets[tooltipItem.datasetIndex].label;
          return source+": "+(new Number(tooltipItem.yLabel)).toLocaleString() + '%';
        }
      }
    },
    scales: {
      yAxes: [{
        type: 'logarithmic',
        ticks: {
          min: 0,
          max: 100,
          callback: function (value, index, ticks) {
            const remain = value / (Math.pow(10, Math.floor(Math.log10(value))));

            if (remain === 1 || remain === 2 || remain === 5 || index === 0 || index === (ticks.length -2)) {
              return (new Number(value)).toLocaleString() + '%';
            } else {
              return '';
            }
          }
        }
      }]
    },
    animation: false
  };

  private fields = {
    "ExAC": {
      "ALL": "ExAC_AF",
      "AFR": "ExAC_AFR_AF",
      "AMR": "ExAC_AMR_AF",
      "EAS": "ExAC_EAS_AF",
      "FIN": "ExAC_FIN_AF",
      "EUR": "ExAC_NFE_AF",
      "SAS": "ExAC_SAS_AF",
      "OTH": "ExAC_OTH_AF"
    },
    "1000 Genomes": {
      "ALL": "AF",
      "AFR": "AFR_AF",
      "AMR": "AMR_AF",
      "EAS": "EAS_AF",
      "EUR": "EUR_AF",
      "SAS": "SAS_AF"
    },
    'GnomAD':{
      "ALL": "gnomAD_AF",
      "AFR": "gnomAD_AFR_AF",
      "AMR": "gnomAD_AMR_AF",
      "EAS": "gnomAD_EAS_AF",
      "FIN": "gnomAD_FIN_AF",
      "EUR": "gnomAD_NFE_AF",
      "SAS": "gnomAD_SAS_AF",
      "OTH": "gnomAD_OTH_AF"
    }
  }

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.init();
  }


  private init() {
    if (!this.variant) return;

    this.data = [];
    Object.keys(this.fields).forEach(source => {
      let sourceDataPoints = []
      this.populations.forEach(population => {
        let value = NaN;
        let field = this.fields[source][population];
        if (field && this.variant['TRANSCRIPT'] != null && this.variant['TRANSCRIPT']['CSQ:' + field]) {
          let freq = this.variant['TRANSCRIPT']['CSQ:' + field];
          if (freq instanceof Array && freq[0] != null) {
            value = +freq[0] * 100;
          } else {
            value = +freq * 100;
          }
        }
        sourceDataPoints.push(value);
      });
      this.data.push({ data: sourceDataPoints, label: source });
    });
  }

}
