import { Component, OnInit } from '@angular/core';
import { VcfService } from '../vcf.service';
import { GroupAdminService } from '../group-admin.service';

@Component({
  selector: 'app-group-vcf-management',
  templateUrl: './group-vcf-management.component.html',
  styleUrls: ['./group-vcf-management.component.css']
})
export class GroupVcfManagementComponent implements OnInit {

  vcfs: Array<Object>;
  searchNameString: string;
  searchSampleString: string;

  constructor(private adminService: GroupAdminService){  }

  ngOnInit() {
    this.adminService.getVcfs().subscribe(data => {
        if (data['status'] == "OK"){
            let myArray = data['payload']
            myArray.map((element) => element['SAMPLE_NAMES']=element['SAMPLES'].map((sample)=>sample['SAMPLE_NAME']))
            myArray.map((element)=> element['PIPELINE_DESCRIPTION']=element['PIPELINE_DESCRIPTION'].replace(/\s/g,'<br>'))
            this.vcfs=myArray;
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized" )
        }
      }
    );
  }

  canDeleteVCF(vcf){
    return (vcf['STATUS']=='AVAILABLE' || vcf['STATUS']=='ERROR');
  }

  removeVCF(vcf) {
    if (window.confirm('Are you sure to remove data for vcf '+vcf.VCF_FILE)){
      this.adminService.removeVCF(vcf.DB_ID).subscribe(data => {
        if (data['status'] == "OK"){
          this.ngOnInit();
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized" )
        }else if (data['status'] == "ERROR") {
          window.alert("Error:"+data['message'] )
        }
      });
    }
  }
}
