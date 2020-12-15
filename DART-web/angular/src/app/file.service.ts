import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';
import { environment } from '../environments/environment';
import { saveAs } from 'file-saver/FileSaver';

const fileUrl = "v1/export";

@Injectable()
export class FileService {

  constructor(private http: HttpClient, private auth: AuthenticationService ) { }

  downloadFile(queryId: string, callback: () => void, fields) { 
    let options = this.auth.getOptionsForFile();
    options.addParam('id',queryId);
    if (fields) {
      for (var i = 0; i < fields.length; i++) {
        options.appendParam('fields',fields[i]);      
      }      
    }

    this.http.get(environment.server + fileUrl,options).subscribe(
       (response: any) => {
          let contentDisposition = response.headers.get('content-disposition') || '';
          let fileName = (contentDisposition.match( /filename=([^;]+)/)[1] || 'untitled').trim();
          let blob = new Blob([response.body], {type: "application/octet-stream"});
          saveAs(blob, fileName);
          callback();
        },
        err => {
          console.log(err);
          alert("Server error while downloading file.");
          callback();
        }
    );
  };

  downloadReport(reportId: any, callback: () => void, fields) { 
    let options = this.auth.getOptionsForFile();
    options.addParam('id',reportId);
    if (fields) {
      for (var i = 0; i < fields.length; i++) {
        options.appendParam('fields',fields[i]);      
      }      
    }

    this.http.get(environment.server + fileUrl+"/report",options).subscribe(
       (response: any) => {
          let contentDisposition = response.headers.get('content-disposition') || '';
          let fileName = (contentDisposition.match( /filename=([^;]+)/)[1] || 'untitled').trim();
          let blob = new Blob([response.body], {type: "application/octet-stream"});
          saveAs(blob, fileName);
          callback();
        },
        err => {
          console.log(err);
          alert("Server error while downloading file.");
          callback();
        }
    );
  };
}
