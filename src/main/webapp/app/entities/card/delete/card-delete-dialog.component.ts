import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICard } from '../card.model';
import { CardService } from '../service/card.service';

@Component({
  templateUrl: './card-delete-dialog.component.html',
})
export class CardDeleteDialogComponent {
  card?: ICard;

  constructor(protected cardService: CardService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
