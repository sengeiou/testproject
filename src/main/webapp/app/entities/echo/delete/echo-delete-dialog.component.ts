import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEcho } from '../echo.model';
import { EchoService } from '../service/echo.service';

@Component({
  templateUrl: './echo-delete-dialog.component.html',
})
export class EchoDeleteDialogComponent {
  echo?: IEcho;

  constructor(protected echoService: EchoService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.echoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
