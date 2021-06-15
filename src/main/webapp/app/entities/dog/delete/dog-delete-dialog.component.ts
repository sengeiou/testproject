import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDog } from '../dog.model';
import { DogService } from '../service/dog.service';

@Component({
  templateUrl: './dog-delete-dialog.component.html',
})
export class DogDeleteDialogComponent {
  dog?: IDog;

  constructor(protected dogService: DogService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dogService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
