import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICard } from '../card.model';
import { CardService } from '../service/card.service';
import { CardDeleteDialogComponent } from '../delete/card-delete-dialog.component';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
})
export class CardComponent implements OnInit {
  cards?: ICard[];
  isLoading = false;
  currentSearch: string;

  constructor(protected cardService: CardService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cardService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<ICard[]>) => {
            this.isLoading = false;
            this.cards = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.cardService.query().subscribe(
      (res: HttpResponse<ICard[]>) => {
        this.isLoading = false;
        this.cards = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICard): number {
    return item.id!;
  }

  delete(card: ICard): void {
    const modalRef = this.modalService.open(CardDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.card = card;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
