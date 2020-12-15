import {Component, ElementRef, EventEmitter, OnInit, TemplateRef, ViewChild} from '@angular/core';
import { GroupAdminService } from '../group-admin.service';
import {BsModalRef, BsModalService} from "ngx-bootstrap";
import {SAMPLE_DROPDOWN_CONFIG} from "../variant-filter-query/selectize-config";
import {Sex} from "../variant-filter-query/FullQuery";

enum GENOMES {
  GRCh37 = "GRCh37",
  GRCh38 = "GRCh38"
}

@Component({
  selector: 'app-group-bed-management',
  templateUrl: './group-bed-management.component.html',
  styleUrls: ['./group-bed-management.component.css']
})
export class GroupBedManagementComponent implements OnInit {

  modalRef: BsModalRef;

  beds =[]
  searchNameString: string;


  constructor(private adminService: GroupAdminService, private modalService: BsModalService) { }

  ngOnInit() {
    this.adminService.getBEDs().subscribe(data => {
        if (data['status'] == "OK") {
          this.beds = data['payload'];
        } else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized")
        }
      }
    );
  }

  fileChange(event) {
    let fileList: FileList = event.target.files;

    if(fileList.length > 0) {
        let file: File = fileList[0];
        event.target.value = null;

        this.modalRef = this.modalService.show(BedGenomeModalComponent, {
          initialState:{
            bedFile: file
          }
        });

        this.modalRef.content.genomeSelectedEvent.subscribe(
            data => {
              this.uploadBed(data.file, data.genome)
            }
        );
    }
  }

  uploadBed(bedFile: File, genome: string){
    this.adminService.postBED(bedFile, genome).subscribe(
      data => {
        if (data['status'] == "OK"){
          this.ngOnInit();
        }else if (data['status'] == "AUTHORIZATION_ERROR") {
          window.alert("Not authorized" )
        }else if (data['status'] == "ERROR") {
          window.alert("Error:"+data['message'] )
        }
      }
    )
  }

  canDeleteBED(bed){
    return bed['STATUS']=='AVAILABLE';
  }

  removeBED(bed) {
    if (window.confirm('Are you sure to remove data for bed '+bed.FILE_NAME)){
      this.adminService.removeBED(bed.DB_ID).subscribe(data => {
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

@Component({
  selector: 'modal-content',
  template: `
    <div class="modal-header">
      <h4 class="modal-title pull-left">Select Genome</h4>
      <button type="button" class="close pull-right" aria-label="Close" (click)="modalRef.hide()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body" >

      <div class="row" style="margin-bottom: 10px" >
        <div class="col-xs-3"><span><strong>BED File: </strong></span></div>
        <div class="col-xs-2">{{bedFile.name}}</div>
        <div class="col-xs-8"></div>
      </div>
      
      <div class="row">
        <div class="col-xs-3"><span><strong>Genome: </strong></span></div>
        <div class="col-xs-4" >
          <select id="genome-select" #genomeSelectizeEl></select>
        </div>
        <div class="col-xs-5"></div>
      </div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-primary" (click)="setGenome()" >Upload</button>
      <button type="button" class="btn btn-default" (click)="modalRef.hide()" >Cancel</button>
    </div>
  `
})

export class BedGenomeModalComponent implements OnInit {
  @ViewChild('genomeSelectizeEl') genomeSelectizeEl: ElementRef;

  public genomeSelectedEvent: EventEmitter<any> = new EventEmitter();

  bedFile: File;
  selectedGenome  = GENOMES.GRCh37;

  constructor(public modalRef: BsModalRef) {
  }

  ngOnInit() {
    let selectizeObj=$(this.genomeSelectizeEl.nativeElement).selectize(SAMPLE_DROPDOWN_CONFIG)[0].selectize;

    selectizeObj.off('change');
    selectizeObj.clear();
    selectizeObj.clearOptions();

    for (let value in GENOMES) {
      selectizeObj.addOption({label: GENOMES[value], value: value});
    }

    selectizeObj.addItem(this.selectedGenome)

    selectizeObj.on('change', (value) => {
      this.selectedGenome=value
    });

  }

  setGenome(){
      this.genomeSelectedEvent.emit({
        genome:this.selectedGenome,
        file:this.bedFile
      });

      this.modalRef.hide();
  }

}
