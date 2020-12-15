
import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { Observable , forkJoin} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

export const links= {
  'g37': {
    ENSEMBL_WEB_GENE: "https://grch37.ensembl.org/Homo_sapiens/Gene/Summary?g={query}",
    HGNC: "https://www.genenames.org/data/gene-symbol-report/#!/hgnc_id/{query}",
    OMIM: "https://www.omim.org/entry/{query}",
    GNOMAD: "https://gnomad.broadinstitute.org/variant/{query}",
    CLINVAR: "https://www.ncbi.nlm.nih.gov/clinvar/?term={query}",
    ENSEMBL_WEB_TRANSCRIPT: "http://grch37.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t={query}",
    ENSEMBL_WEB_PROTEIN: "http://grch37.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;p={query}",
    ICGC_WEB_GENE: 'https://dcc.icgc.org/genes/{query}',
    ICGC_WEB_LOGO: 'https://dcc.icgc.org//styles/images/icgc-logo.png',
    ICGC_API_GENES: 'https://dcc.icgc.org/api/v1/genes/{query}',
    ICGC_API_PROTEINS: 'https://dcc.icgc.org/api/v1/protein/{query}',
    ENSEMBL_API_DOMAINS: "https://grch37.rest.ensembl.org/overlap/translation/{query}",
    ENSEMBL_API_SEQUENCE: "https://grch37.rest.ensembl.org/sequence/id/{query}",
    HGNC_API: "https://rest.genenames.org/fetch/ensembl_gene_id/{query}"
  },
  'g38': {
    ENSEMBL_WEB_GENE: "https://www.ensembl.org/Homo_sapiens/Gene/Summary?g={query}",
    HGNC: "https://www.genenames.org/data/gene-symbol-report/#!/hgnc_id/{query}",
    OMIM: "https://www.omim.org/entry/{query}",
    GNOMAD: "https://gnomad.broadinstitute.org/variant/{query}?dataset=gnomad_r3",
    CLINVAR: "https://www.ncbi.nlm.nih.gov/clinvar/?term={query}",
    ENSEMBL_WEB_TRANSCRIPT: "http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t={query}",
    ENSEMBL_WEB_PROTEIN: "http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;p={query}",
    ICGC_WEB_GENE: 'https://dcc.icgc.org/genes/{query}',
    ICGC_WEB_LOGO: 'https://dcc.icgc.org//styles/images/icgc-logo.png',
    ICGC_API_GENES: 'https://dcc.icgc.org/api/v1/genes/{query}',
    ICGC_API_PROTEINS: 'https://dcc.icgc.org/api/v1/protein/{query}',
    ENSEMBL_API_DOMAINS: "https://rest.ensembl.org/overlap/translation/{query}",
    ENSEMBL_API_SEQUENCE: "https://rest.ensembl.org/sequence/id/{query}",
    HGNC_API: "https://rest.genenames.org/fetch/ensembl_gene_id/{query}"
  }
};

export function getLink(vcfGenome: string, link:string, query: string): string{
  if (new RegExp("GRCh38.p[0-9]+").test(vcfGenome)) {
    return links['g38'][link].replace("{query}",query)
  }else if (new RegExp("GRCh37.p[0-9]+").test(vcfGenome)){
    return links['g37'][link].replace("{query}",query)
  }
}


@Injectable()
export class ExternalProvidersService {

  constructor(private http: HttpClient) { }


  getHGNCinfo(gene: string,vcfGenome: string) {
    let headers = new HttpHeaders().set("Accept", "application/json");

    return this.http.get(getLink(vcfGenome,"HGNC_API" ,gene), {headers});
  }

  getENSEMBLDomains(transcript: string, vcfGenome: string): Observable<any> {
    let params = new HttpParams().append('content-type', 'application-json');

    return forkJoin(
      this.http.get(getLink(vcfGenome,"ENSEMBL_API_SEQUENCE",transcript), { params: params }),
      this.http.get(getLink(vcfGenome,"ENSEMBL_API_DOMAINS",transcript), {params: params})
    ).pipe(map(([sequence, features] :Array<any>) => {
      if (sequence){
        let returnObject =  {description: transcript, length: sequence['seq'].length, domains: []}
        if (features){
          returnObject.domains = features.filter((thisFeature) => thisFeature.type=="Pfam").map( (thisFeature: any) => {
            return {
              id:  thisFeature.type+" "+thisFeature.id,
              description: thisFeature.feature_type+": "+thisFeature.description,
              start: +thisFeature.start,
              end: +thisFeature.end
            };
          });
        }
        return returnObject;
      }
      return null;
    }));
  }

  getICGCDomains(gene: string, transcript: string, vcfGenome: string): Observable<any> {
    let Params = new HttpParams();
    Params = Params.append('include', 'transcripts');

    return this.http.get(getLink(vcfGenome,"ICGC_API_GENES",gene), { params: Params }).pipe(map((data: any) => {
      if (data) {
        let selectedTranscript = data.transcripts.filter((thisTranscript) => { return thisTranscript.id == transcript })[0];
        if (selectedTranscript) {
          return {
            description: data.description,
            length: selectedTranscript.lengthAminoAcid,
            domains: selectedTranscript.domains.map((thisDomain: any) => {
              return {
                id: thisDomain.hitName,
                description: thisDomain.description,
                start: +thisDomain.start,
                end: +thisDomain.end
              };
            })
          }
        }
      }
      return [];
    }));

  }

  getICGCMutations(transcript: string, vcfGenome: string): Observable<any> {

    return this.http.get(getLink(vcfGenome,"ICGC_API_PROTEINS", transcript)).pipe(map((data: any) => {
      if (data) {
        let transcriptMutations = data.hits.filter( (mutation) => {
          return mutation.transcripts.map((singleTranscript) => {return singleTranscript.id}).includes(transcript)
        });
        if (transcriptMutations){
          return transcriptMutations.map( (mutation) => {
              let selectedTranscript = mutation.transcripts.filter((mutationTranscript) => {return mutationTranscript.id==transcript})[0];
              let position = +selectedTranscript.consequence.aaMutation.match(/\d+/)[0];
              return {
                id: mutation.id,
                x: position,
                donors: mutation.affectedDonorCountTotal,
                impact: selectedTranscript.consequence.functionalImpact.toUpperCase()
              }
          });
        }
      }
      return [];
    }));
  }

  checkLocalIGV(port: number) {
    return this.http.get("http://localhost:"+port)
  }


}
