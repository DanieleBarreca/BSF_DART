import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-preset-modal',
  templateUrl: './preset-modal.component.html',
  styleUrls: ['./preset-modal.component.css']
})
export class PresetModalComponent implements OnInit {
  @Output() presetConfirmed = new EventEmitter<string>();

  mnemonic:string;

  constructor(public bsModalRef: BsModalRef) {
  }

  ngOnInit() {
  }

  onSubmit(){
    this.presetConfirmed.emit(this.mnemonic);
  }
}
